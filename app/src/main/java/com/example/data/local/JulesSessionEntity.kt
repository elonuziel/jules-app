package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.SessionItem
import com.example.data.model.SessionStatus
import com.example.data.model.TaskCategory

@Entity(tableName = "jules_sessions")
data class JulesSessionEntity(
    @PrimaryKey
    val id: String,
    val repo: String,
    val branch: String,
    val title: String,
    val status: String,
    val category: String,
    val prompt: String,
    val currentStep: String,
    val progressPercent: Int,
    val agentType: String,
    val etaRemaining: String,
    val testSuiteInfo: String,
    val prNumber: String,
    val prTitle: String,
    val diffAdded: Int,
    val diffRemoved: Int,
    val astNodesModified: Int,
    val synthesizerDetail: String,
    val createdAt: Long,
    val prStatus: String = "OPEN",
    val isBranchDeleted: Boolean = false,
    val prApproved: Boolean = false,
    val targetBaseBranch: String = "main"
) {
    fun toSessionItem(): SessionItem {
        val parsedStatus = try {
            SessionStatus.valueOf(status)
        } catch (e: Exception) {
            SessionStatus.RUNNING
        }
        val parsedCategory = try {
            TaskCategory.valueOf(category)
        } catch (e: Exception) {
            TaskCategory.BUG_FIX
        }
        val parsedPrStatus = try {
            com.example.data.model.PullRequestStatus.valueOf(prStatus)
        } catch (e: Exception) {
            com.example.data.model.PullRequestStatus.OPEN
        }
        return SessionItem(
            id = id,
            repo = repo,
            branch = branch,
            title = title,
            status = parsedStatus,
            category = parsedCategory,
            prompt = prompt,
            currentStep = currentStep,
            progressPercent = progressPercent,
            agentType = agentType,
            etaRemaining = etaRemaining,
            testSuiteInfo = testSuiteInfo,
            prNumber = prNumber,
            prTitle = prTitle,
            diffAdded = diffAdded,
            diffRemoved = diffRemoved,
            astNodesModified = astNodesModified,
            synthesizerDetail = synthesizerDetail,
            createdAt = createdAt,
            prStatus = parsedPrStatus,
            isBranchDeleted = isBranchDeleted,
            prApproved = prApproved,
            targetBaseBranch = targetBaseBranch
        )
    }

    companion object {
        fun fromSessionItem(item: SessionItem): JulesSessionEntity {
            return JulesSessionEntity(
                id = item.id,
                repo = item.repo,
                branch = item.branch,
                title = item.title,
                status = item.status.name,
                category = item.category.name,
                prompt = item.prompt,
                currentStep = item.currentStep,
                progressPercent = item.progressPercent,
                agentType = item.agentType,
                etaRemaining = item.etaRemaining,
                testSuiteInfo = item.testSuiteInfo,
                prNumber = item.prNumber,
                prTitle = item.prTitle,
                diffAdded = item.diffAdded,
                diffRemoved = item.diffRemoved,
                astNodesModified = item.astNodesModified,
                synthesizerDetail = item.synthesizerDetail,
                createdAt = item.createdAt,
                prStatus = item.prStatus.name,
                isBranchDeleted = item.isBranchDeleted,
                prApproved = item.prApproved,
                targetBaseBranch = item.targetBaseBranch
            )
        }
    }
}
