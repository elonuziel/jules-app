package com.example.data.model

enum class SessionStatus(val label: String) {
    RUNNING("Running"),
    NEEDS_REVIEW("Review Req."),
    PATCHING("Patching"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    PAUSED("Paused")
}

enum class PullRequestStatus(val label: String) {
    OPEN("Open"),
    MERGED("Merged"),
    CLOSED("Closed")
}

data class GitHubPrRef(
    val owner: String,
    val repo: String,
    val number: Int
)

enum class TaskCategory(val label: String, val apiValue: String) {
    BUG_FIX("Bug Fix", "bug"),
    FEATURE("Feature", "feature"),
    REFACTOR("Refactor", "refactor"),
    SECURITY("Security", "security")
}

data class SessionItem(
    val id: String,
    val repo: String,
    val branch: String,
    val title: String,
    val status: SessionStatus,
    val category: TaskCategory = TaskCategory.BUG_FIX,
    val prompt: String = "",
    val currentStep: String = "",
    val progressPercent: Int = 0,
    val agentType: String = "Jules Async Agent",
    val etaRemaining: String = "2m remaining",
    val testSuiteInfo: String = "",
    val prNumber: String = "",
    val prTitle: String = "",
    val diffAdded: Int = 0,
    val diffRemoved: Int = 0,
    val astNodesModified: Int = 0,
    val synthesizerDetail: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val prStatus: PullRequestStatus = PullRequestStatus.OPEN,
    val isBranchDeleted: Boolean = false,
    val prApproved: Boolean = false,
    val targetBaseBranch: String = "main",
    val prUrl: String = ""
) {
    fun getGitHubPrRef(): GitHubPrRef? {
        val url = prUrl.ifBlank {
            if (repo.isNotBlank() && prNumber.isNotBlank()) {
                val cleanNum = prNumber.removePrefix("#").trim()
                "https://github.com/$repo/pull/$cleanNum"
            } else null
        } ?: return null

        val match = Regex("github\\.com/([^/]+)/([^/]+)/pull/(\\d+)").find(url) ?: return null
        val (owner, repoName, numStr) = match.destructured
        return GitHubPrRef(owner, repoName, numStr.toIntOrNull() ?: return null)
    }
}

data class DiffLine(
    val oldLineNumber: Int?,
    val newLineNumber: Int?,
    val type: DiffLineType,
    val text: String
)

enum class DiffLineType {
    CONTEXT,
    ADDITION,
    DELETION
}

data class DiffFile(
    val fileName: String,
    val addedCount: Int,
    val deletedCount: Int,
    val lines: List<DiffLine>,
    val testDescription: String = "",
    val testPassed: Boolean = true
)

data class RepoSourceItem(
    val id: String,
    val fullName: String,
    val defaultBranch: String = "main",
    val isPrivate: Boolean = false,
    val permissions: String = "Read & Write",
    val lastSynced: String = "Synced 2m ago",
    val openPrCount: Int = 0,
    val activeTasksCount: Int = 0,
    val language: String = "Kotlin"
)

data class AgentSettingsState(
    val isAutonomous: Boolean = true,
    val autoTestSuite: Boolean = true,
    val reasoningBudgetK: Int = 64,
    val pushNotificationOnPr: Boolean = true,
    val notifyOnFailedTests: Boolean = true,
    val requireManualApproval: Boolean = false,
    val isDarkTheme: Boolean = true,
    val apiLatencyMs: Int = 118,
    val isPinging: Boolean = false,
    val byokApiKey: String = "",
    val githubPatToken: String = "",
    val gcpProjectId: String = "jules-agent-prod-2025",
    val accountEmail: String = "ElonUziel@gmail.com",
    val targetRepo: String = "google/cloud-android-sdk",
    val targetBranch: String = "jules/fix-issue-892",
    val hasCompletedWelcome: Boolean = false
) {
    val isGitHubConnected: Boolean
        get() = githubPatToken.isNotBlank()

    val isJulesConfigured: Boolean
        get() = byokApiKey.isNotBlank()

    val maskedGitHubToken: String
        get() = when {
            githubPatToken.isBlank() -> "Unconfigured"
            githubPatToken.length >= 8 -> "${githubPatToken.take(4)}••••${githubPatToken.takeLast(4)}"
            else -> "ghp_••••"
        }

    val maskedJulesKey: String
        get() = when {
            byokApiKey.isBlank() -> "No Key Set"
            byokApiKey.length >= 12 -> "${byokApiKey.take(8)}••••${byokApiKey.takeLast(4)}"
            else -> "jul_••••"
        }
}
