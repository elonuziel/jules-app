package com.example.data.repository

import com.example.data.local.JulesSessionDao
import com.example.data.local.JulesSessionEntity
import com.example.data.model.SessionItem
import com.example.data.model.SessionStatus
import com.example.data.model.TaskCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JulesRepository(private val dao: JulesSessionDao) {

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
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 4 - 1000 * 12 // 4m 12s ago
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

    suspend fun updateSessionStatus(sessionId: String, newStatus: SessionStatus) {
        val existing = dao.getSessionById(sessionId) ?: return
        dao.updateSession(existing.copy(status = newStatus.name))
    }
}
