package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ByokStorage
import com.example.data.local.JulesDatabase
import com.example.data.model.AgentSettingsState
import com.example.data.model.PullRequestStatus
import com.example.data.model.RepoSourceItem
import com.example.data.model.SessionItem
import com.example.data.model.SessionStatus
import com.example.data.model.TaskCategory
import com.example.data.repository.JulesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class JulesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JulesRepository
    private val byokStorage = ByokStorage(application)

    // API Keys and Settings State (initialized from client-side BYOK storage)
    val settingsState = MutableStateFlow(
        AgentSettingsState(
            byokApiKey = byokStorage.julesApiKey,
            githubPatToken = byokStorage.githubPat,
            isDarkTheme = byokStorage.isDarkTheme,
            hasCompletedWelcome = byokStorage.hasCompletedWelcome
        )
    )

    // GitHub Token Management Modal visibility
    val showGitHubTokenModal = MutableStateFlow(false)

    // Active Session Drawer for Enhanced PR review
    val activeDrawerSession = MutableStateFlow<SessionItem?>(null)

    // Sources View State
    val sourcesList = MutableStateFlow(
        listOf(
            RepoSourceItem(
                id = "repo-1",
                fullName = "google/cloud-android-sdk",
                defaultBranch = "main",
                isPrivate = false,
                permissions = "Read & Write AST",
                lastSynced = "Synced 2m ago",
                openPrCount = 1,
                activeTasksCount = 1,
                language = "Kotlin"
            ),
            RepoSourceItem(
                id = "repo-2",
                fullName = "my-org/developer-portal",
                defaultBranch = "production",
                isPrivate = true,
                permissions = "Read & Write AST",
                lastSynced = "Synced 18m ago",
                openPrCount = 1,
                activeTasksCount = 0,
                language = "TypeScript"
            ),
            RepoSourceItem(
                id = "repo-3",
                fullName = "android-gemini-client",
                defaultBranch = "dev",
                isPrivate = false,
                permissions = "Read & Write AST",
                lastSynced = "Synced 1h ago",
                openPrCount = 0,
                activeTasksCount = 1,
                language = "Kotlin"
            )
        )
    )
    val isRefreshingSources = MutableStateFlow(false)

    init {
        val db = JulesDatabase.getInstance(application)
        repository = JulesRepository(db.sessionDao())
        viewModelScope.launch {
            repository.ensureInitialData()
        }
    }

    // Navigation Tab (0: Sessions, 1: Sources, 2: New Task, 3: Live Diff, 4: API Keys)
    private val _currentTabIndex = MutableStateFlow(0)
    val currentTabIndex: StateFlow<Int> = _currentTabIndex.asStateFlow()

    fun selectTab(index: Int) {
        _currentTabIndex.value = index
    }

    fun openSessionDrawer(session: SessionItem) {
        activeDrawerSession.value = session
    }

    fun closeSessionDrawer() {
        activeDrawerSession.value = null
    }

    // PULL REQUEST WORKFLOW METHODS
    fun approvePr(session: SessionItem) {
        viewModelScope.launch {
            val updated = session.copy(prApproved = true)
            repository.insertSession(updated)
            if (activeDrawerSession.value?.id == session.id) {
                activeDrawerSession.value = updated
            }
        }
    }

    fun mergePr(session: SessionItem) {
        viewModelScope.launch {
            val updated = session.copy(
                prStatus = PullRequestStatus.MERGED,
                status = SessionStatus.COMPLETED,
                currentStep = "Merged into ${session.targetBaseBranch}"
            )
            repository.insertSession(updated)
            if (activeDrawerSession.value?.id == session.id) {
                activeDrawerSession.value = updated
            }
        }
    }

    fun deleteBranch(session: SessionItem) {
        viewModelScope.launch {
            val updated = session.copy(isBranchDeleted = true)
            repository.insertSession(updated)
            if (activeDrawerSession.value?.id == session.id) {
                activeDrawerSession.value = updated
            }
        }
    }

    // SOURCES REFRESH METHOD
    fun refreshSources() {
        if (isRefreshingSources.value) return
        viewModelScope.launch {
            isRefreshingSources.value = true
            delay(1000)
            val current = sourcesList.value.toMutableList()
            // Ensure newly granted repositories are surfaced
            if (current.none { it.fullName == "google/jules-android-samples" }) {
                current.add(
                    RepoSourceItem(
                        id = "repo-new-1",
                        fullName = "google/jules-android-samples",
                        defaultBranch = "main",
                        isPrivate = false,
                        permissions = "Read & Write AST",
                        lastSynced = "Synced just now",
                        openPrCount = 0,
                        activeTasksCount = 0,
                        language = "Kotlin"
                    )
                )
            }
            if (current.none { it.fullName == "android/compose-samples" }) {
                current.add(
                    RepoSourceItem(
                        id = "repo-new-2",
                        fullName = "android/compose-samples",
                        defaultBranch = "main",
                        isPrivate = false,
                        permissions = "Read & Write AST",
                        lastSynced = "Synced just now",
                        openPrCount = 0,
                        activeTasksCount = 0,
                        language = "Kotlin"
                    )
                )
            }
            // Update timestamp for all
            val refreshed = current.map { it.copy(lastSynced = "Synced just now") }
            sourcesList.value = refreshed
            isRefreshingSources.value = false
        }
    }

    // CLIENT-SIDE BYOK GITHUB PAT MANAGEMENT
    fun updateGitHubPat(token: String) {
        byokStorage.githubPat = token
        settingsState.update { it.copy(githubPatToken = token) }
    }

    fun disconnectGitHubPat() {
        byokStorage.clearGitHubPat()
        settingsState.update { it.copy(githubPatToken = "") }
    }

    fun updateByokJulesKey(newKey: String) {
        byokStorage.julesApiKey = newKey
        settingsState.update { it.copy(byokApiKey = newKey) }
    }

    // Search and Filters on Sessions Screen
    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow("all") // "all", "running", "needs-review", "completed"

    val allSessions: StateFlow<List<SessionItem>> = repository.sessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredSessions: StateFlow<List<SessionItem>> = combine(
        allSessions,
        searchQuery,
        selectedFilter
    ) { list, query, filter ->
        list.filter { item ->
            val matchesFilter = when (filter) {
                "running" -> item.status == SessionStatus.RUNNING || item.status == SessionStatus.PATCHING
                "needs-review" -> item.status == SessionStatus.NEEDS_REVIEW
                "completed" -> item.status == SessionStatus.COMPLETED
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                item.title.contains(query, ignoreCase = true) ||
                item.repo.contains(query, ignoreCase = true) ||
                item.branch.contains(query, ignoreCase = true) ||
                item.prompt.contains(query, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // New Task Form State
    val selectedRepo = MutableStateFlow("google/cloud-android-sdk")
    val targetBranch = MutableStateFlow("jules/fix-issue-892")
    val selectedCategory = MutableStateFlow(TaskCategory.BUG_FIX)
    val promptText = MutableStateFlow("Investigate the intermittent ANR during background sync when network drops. Verify SQLite cursor leaks in SyncWorker.kt and add a regression test.")
    val isAutonomous = MutableStateFlow(true)
    val autoTestSuite = MutableStateFlow(true)
    val reasoningBudgetK = MutableStateFlow(64)
    val isExecutionSettingsExpanded = MutableStateFlow(false)
    val isDispatching = MutableStateFlow(false)
    val dispatchSuccessMessage = MutableStateFlow<String?>(null)

    fun setPrompt(text: String) {
        promptText.value = text
    }

    fun dispatchNewTask() {
        if (isDispatching.value) return
        viewModelScope.launch {
            isDispatching.value = true
            delay(1000)
            val newId = "JLS-${Random.nextInt(1000, 9999)}"
            val newTask = SessionItem(
                id = newId,
                repo = selectedRepo.value,
                branch = targetBranch.value,
                title = promptText.value.take(60) + if (promptText.value.length > 60) "..." else "",
                status = SessionStatus.RUNNING,
                category = selectedCategory.value,
                prompt = promptText.value,
                currentStep = "Cloning & Localizing Symbols",
                progressPercent = 15,
                agentType = "Jules Async Agent",
                etaRemaining = "3m remaining",
                testSuiteInfo = "pytest suite: 0/48",
                createdAt = System.currentTimeMillis()
            )
            repository.insertSession(newTask)
            isDispatching.value = false
            dispatchSuccessMessage.value = "Session $newId Queued!"
            delay(1200)
            dispatchSuccessMessage.value = null
            _currentTabIndex.value = 0 // Switch to Sessions list to see the task!
        }
    }

    // Live Diff Screen State
    val diffSelectedViewMode = MutableStateFlow(1) // 0: Step Logs, 1: Code Diff, 2: Console, 3: 14/14 Passed
    val isSecondaryFile1Expanded = MutableStateFlow(false)
    val isSecondaryFile2Expanded = MutableStateFlow(false)
    val isMainFileExpanded = MutableStateFlow(true)
    val liveDiffProgress = MutableStateFlow(83)
    val prApprovedMessage = MutableStateFlow<String?>(null)
    val showRepromptDialog = MutableStateFlow(false)

    fun approvePr() {
        viewModelScope.launch {
            prApprovedMessage.value = "PR #413 Created on GitHub! Signed off."
            delay(2500)
            prApprovedMessage.value = null
        }
    }

    fun triggerQuickDirective(directive: String) {
        viewModelScope.launch {
            prApprovedMessage.value = "Executing directive: $directive..."
            delay(1500)
            prApprovedMessage.value = "Directive $directive completed ✓"
            delay(1500)
            prApprovedMessage.value = null
        }
    }

    // API Keys and Settings State (additional actions)
    val copyToast = MutableStateFlow(false)
    val testConnectionSuccess = MutableStateFlow<String?>(null)
    val isReposAccordionExpanded = MutableStateFlow(false)

    fun pingGateway() {
        if (settingsState.value.isPinging) return
        viewModelScope.launch {
            settingsState.update { it.copy(isPinging = true) }
            delay(650)
            val newLatency = Random.nextInt(90, 135)
            settingsState.update { it.copy(isPinging = false, apiLatencyMs = newLatency) }
        }
    }

    fun copyToken() {
        viewModelScope.launch {
            copyToast.value = true
            delay(2000)
            copyToast.value = false
        }
    }

    fun regenerateToken() {
        viewModelScope.launch {
            val randomHex = (1..8).joinToString("") { Random.nextInt(0, 16).toString(16) }
            settingsState.update { it.copy(byokApiKey = "jul_live_${randomHex}c94b7aa029f6") }
        }
    }

    fun saveAndTestConnection() {
        viewModelScope.launch {
            testConnectionSuccess.value = "Testing Endpoints..."
            delay(850)
            testConnectionSuccess.value = "All Checks Passed ✓"
            delay(2200)
            testConnectionSuccess.value = null
        }
    }

    fun togglePushNotifications() {
        settingsState.update { it.copy(pushNotificationOnPr = !it.pushNotificationOnPr) }
    }

    fun toggleNotifyOnFailedTests() {
        settingsState.update { it.copy(notifyOnFailedTests = !it.notifyOnFailedTests) }
    }

    fun toggleRequireManualApproval() {
        settingsState.update { it.copy(requireManualApproval = !it.requireManualApproval) }
    }

    fun setTheme(dark: Boolean) {
        byokStorage.isDarkTheme = dark
        settingsState.update { it.copy(isDarkTheme = dark) }
    }

    fun completeWelcome(julesKey: String, githubPat: String, darkTheme: Boolean) {
        byokStorage.hasCompletedWelcome = true
        byokStorage.isDarkTheme = darkTheme
        if (julesKey.isNotBlank()) {
            byokStorage.julesApiKey = julesKey.trim()
        }
        if (githubPat.isNotBlank()) {
            byokStorage.githubPat = githubPat.trim()
        }
        settingsState.update {
            it.copy(
                hasCompletedWelcome = true,
                isDarkTheme = darkTheme,
                byokApiKey = byokStorage.julesApiKey,
                githubPatToken = byokStorage.githubPat
            )
        }
    }

    fun reopenWelcomeScreen() {
        byokStorage.hasCompletedWelcome = false
        settingsState.update { it.copy(hasCompletedWelcome = false) }
    }

    fun resetToCleanSlate() {
        byokStorage.resetToCleanSlate()
        settingsState.update {
            it.copy(
                hasCompletedWelcome = false,
                byokApiKey = "",
                githubPatToken = "",
                isDarkTheme = true
            )
        }
    }
}
