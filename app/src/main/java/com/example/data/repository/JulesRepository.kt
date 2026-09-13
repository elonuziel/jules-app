package com.example.data.repository

import android.util.Log
import com.example.data.local.JulesSessionDao
import com.example.data.local.JulesSessionEntity
import com.example.data.model.DiffFile
import com.example.data.model.PullRequestStatus
import com.example.data.model.RepoSourceItem
import com.example.data.model.SessionItem
import com.example.data.model.SessionStatus
import com.example.data.model.TaskCategory
import com.example.data.remote.NetworkClient
import com.example.data.remote.api.GitHubApiService
import com.example.data.remote.api.JulesApiService
import com.example.data.remote.dto.GitHubMergeRequestDto
import com.example.data.remote.dto.GitHubReviewRequestDto
import com.example.data.remote.dto.JulesCreateSessionRequestDto
import com.example.data.remote.dto.JulesRepoContextDto
import com.example.data.remote.dto.JulesSessionDto
import com.example.data.remote.dto.JulesSourceContextDto
import com.example.data.remote.dto.JulesSourceDto
import com.example.util.DiffParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JulesRepository(
    private val dao: JulesSessionDao,
    private val julesApi: JulesApiService = NetworkClient.julesApi,
    private val gitHubApi: GitHubApiService = NetworkClient.gitHubApi
) {

    private val tag = "JulesRepository"

    val sessions: Flow<List<SessionItem>> = dao.getAllSessions().map { entities ->
        entities.map { it.toSessionItem() }
    }

    suspend fun ensureInitialData() {
        val count = dao.getSessionCount()
        if (count == 0) {
            val initial = listOf(
                SessionItem(
                    id = "JLS-8492",
                    repo = "google/cloud-android-sdk",
                    branch = "jules/fix-auth-race",
                    title = "fix(auth): Refresh token race condition on mobile login",
                    status = SessionStatus.RUNNING,
                    category = TaskCategory.BUG_FIX,
                    prompt = "Investigate the intermittent ANR during background sync when network drops. Verify SQLite cursor leaks in SyncWorker.kt and add a regression test.",
                    currentStep = "Executing Tests (Step 4/6)",
                    progressPercent = 68,
                    agentType = "Jules Async Agent",
                    etaRemaining = "2m remaining",
                    testSuiteInfo = "pytest suite: 42/48",
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 4 - 1000 * 12
                ),
                SessionItem(
                    id = "JLS-7914",
                    repo = "my-org/developer-portal",
                    branch = "jules/webhook-dispatch",
                    title = "feat: Implement webhook dispatch for Jules task completions",
                    status = SessionStatus.NEEDS_REVIEW,
                    category = TaskCategory.FEATURE,
                    prompt = "Implement real-time webhook push notification when agent task run completes or fails.",
                    currentStep = "PR Awaiting Approval",
                    progressPercent = 100,
                    prNumber = "#412",
                    prTitle = "Ready for engineer sign-off",
                    diffAdded = 184,
                    diffRemoved = 22,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 25
                ),
                SessionItem(
                    id = "JLS-6230",
                    repo = "android-gemini-client",
                    branch = "jules/room-dao-flow",
                    title = "refactor: Migrate deprecated Room DB DAO queries to Kotlin Flow",
                    status = SessionStatus.PATCHING,
                    category = TaskCategory.REFACTOR,
                    prompt = "Update UserDao suspend fun getAll() to return Flow<List<User>> for continuous reactive updates.",
                    currentStep = "Synthesizing code patches",
                    progressPercent = 45,
                    synthesizerDetail = "> Updating UserDao.kt suspend fun getAll() -> Flow<List<User>>",
                    astNodesModified = 14,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60
                )
            )
            dao.insertAll(initial.map { JulesSessionEntity.fromSessionItem(it) })
        }
    }

    suspend fun insertSession(session: SessionItem) {
        dao.insertSession(JulesSessionEntity.fromSessionItem(session))
    }

    suspend fun updateSession(session: SessionItem) {
        dao.updateSession(JulesSessionEntity.fromSessionItem(session))
    }

    suspend fun clearSampleSessions() {
        dao.deleteSampleSessions()
    }

    suspend fun clearAllSessions() {
        dao.deleteAllSessions()
    }

    suspend fun updateSessionStatus(sessionId: String, newStatus: SessionStatus) {
        val existing = dao.getSessionById(sessionId) ?: return
        dao.updateSession(existing.copy(status = newStatus.name))
    }

    // ==================== JULES API METHODS ====================

    suspend fun fetchRemoteSources(apiKey: String): Result<List<RepoSourceItem>> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Jules API key is missing"))
            }
            val response = julesApi.getSources(apiKey.trim())
            val mapped = response.sources.map { mapSourceDtoToRepoSourceItem(it) }
            Result.success(mapped)
        } catch (e: Exception) {
            Log.e(tag, "Failed to fetch remote sources", e)
            Result.failure(e)
        }
    }

    suspend fun syncSessionsFromRemote(apiKey: String): Result<List<SessionItem>> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Jules API key is missing"))
            }
            // Remove mock sample data as real remote sync is taking place
            dao.deleteSampleSessions()

            val response = julesApi.getSessions(apiKey.trim(), pageSize = 30)
            val remoteItems = response.sessions.map { dto ->
                mapSessionDtoToSessionItem(dto)
            }

            // Fetch live activity details for running / active sessions
            val enriched = remoteItems.map { session ->
                if (session.status == SessionStatus.RUNNING || session.status == SessionStatus.PATCHING || session.status == SessionStatus.NEEDS_REVIEW) {
                    try {
                        val activitiesResp = julesApi.getSessionActivities(apiKey.trim(), sessionId = session.id, pageSize = 20)
                        val latestActivity = activitiesResp.activities.lastOrNull()
                        val stepDesc = latestActivity?.progressUpdated?.effectiveMessage
                            ?: latestActivity?.agentMessaged?.effectiveMessage
                            ?: latestActivity?.description
                            ?: session.currentStep
                        session.copy(currentStep = stepDesc)
                    } catch (e: Exception) {
                        session
                    }
                } else {
                    session
                }
            }

            if (enriched.isNotEmpty()) {
                dao.insertAll(enriched.map { JulesSessionEntity.fromSessionItem(it) })
            }
            Result.success(enriched)
        } catch (e: Exception) {
            Log.e(tag, "Failed to sync sessions from Jules API", e)
            Result.failure(e)
        }
    }

    suspend fun createRemoteSession(
        apiKey: String,
        prompt: String,
        title: String,
        source: String,
        startingBranch: String,
        requirePlanApproval: Boolean = false,
        automationMode: String = "AUTO_CREATE_PR"
    ): Result<SessionItem> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Jules API key is missing"))
            }
            val cleanSource = if (source.startsWith("sources/")) source else "sources/$source"
            val requestBody = JulesCreateSessionRequestDto(
                prompt = prompt.trim(),
                title = title.ifBlank { prompt.take(50) },
                sourceContext = JulesSourceContextDto(
                    source = cleanSource,
                    githubRepoContext = JulesRepoContextDto(startingBranch = startingBranch.ifBlank { "main" })
                ),
                requirePlanApproval = requirePlanApproval,
                automationMode = automationMode
            )

            val createdDto = julesApi.createSession(apiKey.trim(), requestBody)
            val item = mapSessionDtoToSessionItem(createdDto)
            dao.insertSession(JulesSessionEntity.fromSessionItem(item))
            Result.success(item)
        } catch (e: Exception) {
            Log.e(tag, "Failed to create session on Jules API", e)
            Result.failure(e)
        }
    }

    suspend fun fetchSessionActivities(apiKey: String, sessionId: String): Result<List<com.example.data.remote.dto.JulesActivityDto>> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Jules API key is missing"))
            }
            val cleanId = sessionId.trim().removePrefix("sessions/")
            val response = julesApi.getSessionActivities(apiKey = apiKey.trim(), sessionId = cleanId, pageSize = 50)
            Result.success(response.activities)
        } catch (e: Exception) {
            Log.e(tag, "Failed to fetch session activities", e)
            Result.failure(e)
        }
    }

    suspend fun approvePlan(apiKey: String, sessionId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Jules API key is missing"))
            }
            val cleanId = sessionId.trim().removePrefix("sessions/")
            julesApi.approvePlan(apiKey = apiKey.trim(), sessionId = cleanId)
            val existing = dao.getSessionById(cleanId)
            if (existing != null) {
                dao.updateSession(existing.copy(status = SessionStatus.RUNNING.name, prApproved = true))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to approve plan on Jules API", e)
            Result.failure(e)
        }
    }

    suspend fun sendSessionMessage(apiKey: String, sessionId: String, message: String): Result<com.example.data.remote.dto.JulesActivityDto> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Jules API key is missing"))
            }
            if (message.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Message cannot be empty"))
            }
            val cleanId = sessionId.trim().removePrefix("sessions/")
            val result = julesApi.sendMessage(
                apiKey = apiKey.trim(),
                sessionId = cleanId,
                body = com.example.data.remote.dto.JulesSendMessageRequestDto(prompt = message.trim())
            )
            Result.success(result)
        } catch (e: Exception) {
            Log.e(tag, "Failed to send message to Jules API", e)
            Result.failure(e)
        }
    }

    // ==================== GITHUB API METHODS ====================

    suspend fun loadPullRequestDiff(
        owner: String,
        repo: String,
        pullNumber: Int,
        token: String?
    ): Result<List<DiffFile>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = if (!token.isNullOrBlank()) "Bearer ${token.trim()}" else ""
            val files = gitHubApi.getPullRequestFiles(
                authHeader = authHeader,
                owner = owner,
                repo = repo,
                number = pullNumber
            )
            val diffFiles = files.map { DiffParser.toDiffFile(it) }
            Result.success(diffFiles)
        } catch (e: Exception) {
            Log.e(tag, "Failed to load PR files from GitHub", e)
            Result.failure(e)
        }
    }

    suspend fun approveRemotePr(
        owner: String,
        repo: String,
        pullNumber: Int,
        token: String,
        message: String = "Approved via Jules Mobile"
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (token.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("GitHub token is required"))
            }
            gitHubApi.createReview(
                authHeader = "Bearer ${token.trim()}",
                owner = owner,
                repo = repo,
                number = pullNumber,
                body = GitHubReviewRequestDto(body = message)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to approve PR on GitHub", e)
            Result.failure(e)
        }
    }

    suspend fun mergeRemotePr(
        owner: String,
        repo: String,
        pullNumber: Int,
        token: String,
        mergeMethod: String = "squash"
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (token.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("GitHub token is required"))
            }
            gitHubApi.mergePullRequest(
                authHeader = "Bearer ${token.trim()}",
                owner = owner,
                repo = repo,
                number = pullNumber,
                body = GitHubMergeRequestDto(mergeMethod = mergeMethod)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to merge PR on GitHub", e)
            Result.failure(e)
        }
    }

    suspend fun deleteRemoteBranch(
        owner: String,
        repo: String,
        branch: String,
        token: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (token.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("GitHub token is required"))
            }
            val cleanBranch = branch.trim().removePrefix("refs/heads/")
            gitHubApi.deleteBranch(
                authHeader = "Bearer ${token.trim()}",
                owner = owner,
                repo = repo,
                branch = cleanBranch
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to delete branch on GitHub", e)
            Result.failure(e)
        }
    }

    // ==================== MAPPING HELPERS ====================

    private fun mapSessionDtoToSessionItem(dto: JulesSessionDto): SessionItem {
        val rawId = dto.id ?: dto.name.substringAfterLast("/")
        val status = when (dto.state?.uppercase(Locale.US)) {
            "COMPLETED" -> SessionStatus.COMPLETED
            "IN_PROGRESS" -> SessionStatus.RUNNING
            "PLANNING" -> SessionStatus.PATCHING
            "AWAITING_PLAN_APPROVAL", "AWAITING_USER_FEEDBACK" -> SessionStatus.NEEDS_REVIEW
            "FAILED" -> SessionStatus.FAILED
            "PAUSED" -> SessionStatus.PAUSED
            else -> SessionStatus.RUNNING
        }

        val prOutput = dto.outputs?.firstOrNull()?.pullRequest
        val prUrl = prOutput?.url ?: ""
        val prTitle = prOutput?.title ?: ""
        val prNumber = if (prUrl.isNotBlank()) {
            Regex("pull/(\\d+)").find(prUrl)?.groupValues?.get(1)?.let { "#$it" } ?: ""
        } else ""

        val rawSource = dto.sourceContext?.source ?: ""
        val repo = if (rawSource.startsWith("sources/github/")) {
            rawSource.removePrefix("sources/github/")
        } else if (rawSource.startsWith("sources/")) {
            rawSource.removePrefix("sources/")
        } else {
            rawSource
        }

        val branch = dto.sourceContext?.githubRepoContext?.startingBranch ?: "main"
        val progress = when (status) {
            SessionStatus.COMPLETED -> 100
            SessionStatus.NEEDS_REVIEW -> 90
            SessionStatus.PATCHING -> 50
            SessionStatus.RUNNING -> 35
            SessionStatus.FAILED -> 100
            SessionStatus.PAUSED -> 20
        }

        val createdAt = parseIsoTimestamp(dto.createTime)

        return SessionItem(
            id = rawId,
            repo = repo.ifBlank { "google/jules-project" },
            branch = branch,
            title = dto.title ?: dto.prompt?.take(60) ?: "Jules Session $rawId",
            status = status,
            category = TaskCategory.BUG_FIX,
            prompt = dto.prompt ?: "",
            currentStep = when (status) {
                SessionStatus.COMPLETED -> "Task execution completed"
                SessionStatus.NEEDS_REVIEW -> "Plan/PR awaiting user approval"
                SessionStatus.PATCHING -> "Synthesizing code patches"
                SessionStatus.RUNNING -> "Agent executing steps"
                SessionStatus.FAILED -> "Session failed"
                SessionStatus.PAUSED -> "Session paused"
            },
            progressPercent = progress,
            agentType = "Jules Async Agent",
            etaRemaining = if (status == SessionStatus.COMPLETED) "Completed" else "In Progress",
            testSuiteInfo = "",
            prNumber = prNumber,
            prTitle = prTitle,
            createdAt = createdAt,
            prStatus = if (status == SessionStatus.COMPLETED) PullRequestStatus.MERGED else PullRequestStatus.OPEN,
            prUrl = prUrl
        )
    }

    private fun mapSourceDtoToRepoSourceItem(dto: JulesSourceDto): RepoSourceItem {
        val rawId = dto.id ?: dto.name.substringAfterLast("/")
        val gh = dto.githubRepo
        val fullName = if (gh != null && !gh.owner.isNullOrBlank() && !gh.repo.isNullOrBlank()) {
            "${gh.owner}/${gh.repo}"
        } else {
            dto.name.removePrefix("sources/github/").removePrefix("sources/")
        }
        val defaultBranch = gh?.defaultBranch?.displayName ?: "main"
        val isPrivate = gh?.isPrivate ?: false

        return RepoSourceItem(
            id = rawId,
            fullName = fullName,
            defaultBranch = defaultBranch,
            isPrivate = isPrivate,
            permissions = "Read & Write AST",
            lastSynced = "Synced via Jules API",
            openPrCount = 0,
            activeTasksCount = 0,
            language = "Kotlin"
        )
    }

    private fun parseIsoTimestamp(timestampStr: String?): Long {
        if (timestampStr.isNullOrBlank()) return System.currentTimeMillis()
        return try {
            val clean = timestampStr.substringBefore(".").removeSuffix("Z")
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }
            sdf.parse(clean)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    suspend fun fetchGitHubUser(patToken: String): Result<com.example.data.remote.dto.GitHubUserDto> = withContext(Dispatchers.IO) {
        try {
            if (patToken.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("GitHub token is empty"))
            }
            val user = gitHubApi.getCurrentUser(authHeader = "Bearer ${patToken.trim()}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(tag, "Failed to fetch GitHub user profile", e)
            Result.failure(e)
        }
    }
}
