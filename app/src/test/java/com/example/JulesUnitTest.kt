package com.example

import com.example.data.local.JulesSessionEntity
import com.example.data.model.DiffDataProvider
import com.example.data.model.SessionItem
import com.example.data.model.SessionStatus
import com.example.data.model.TaskCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JulesUnitTest {

    @Test
    fun testSessionEntityMapping() {
        val item = SessionItem(
            id = "JLS-8492",
            repo = "google/cloud-android-sdk",
            branch = "jules/fix-auth-race",
            title = "fix(auth): Refresh token race condition on mobile login",
            status = SessionStatus.RUNNING,
            category = TaskCategory.BUG_FIX,
            prompt = "Investigate SQLite cursor leaks",
            currentStep = "Executing Tests (Step 4/6)",
            progressPercent = 68,
            agentType = "Jules Async Agent",
            etaRemaining = "2m remaining",
            testSuiteInfo = "pytest suite: 42/48",
            createdAt = 1000L,
            prUrl = "https://github.com/google/cloud-android-sdk/pull/412"
        )

        val entity = JulesSessionEntity.fromSessionItem(item)
        assertEquals("JLS-8492", entity.id)
        assertEquals("RUNNING", entity.status)
        assertEquals("BUG_FIX", entity.category)
        assertEquals("https://github.com/google/cloud-android-sdk/pull/412", entity.prUrl)

        val restored = entity.toSessionItem()
        assertEquals(item.id, restored.id)
        assertEquals(item.status, restored.status)
        assertEquals(item.category, restored.category)
        assertEquals(item.progressPercent, restored.progressPercent)
        assertEquals(item.prUrl, restored.prUrl)

        val prRef = restored.getGitHubPrRef()
        assertNotNull(prRef)
        assertEquals("google", prRef?.owner)
        assertEquals("cloud-android-sdk", prRef?.repo)
        assertEquals(412, prRef?.number)
    }

    @Test
    fun testDiffDataProvider() {
        assertNotNull(DiffDataProvider.mainFile)
        assertEquals(".../sync/SyncWorker.kt", DiffDataProvider.mainFile.fileName)
        assertTrue(DiffDataProvider.mainFile.lines.isNotEmpty())
        assertEquals(18, DiffDataProvider.mainFile.addedCount)
        assertEquals(6, DiffDataProvider.mainFile.deletedCount)
    }

    @Test
    fun testThemeColorSchemes() {
        org.junit.Assert.assertNotEquals(
            com.example.ui.theme.JulesDarkColorScheme.background,
            com.example.ui.theme.JulesLightColorScheme.background
        )
        org.junit.Assert.assertNotEquals(
            com.example.ui.theme.JulesDarkColorScheme.surface,
            com.example.ui.theme.JulesLightColorScheme.surface
        )
    }

    @Test
    fun testSampleActivities() {
        val activities = DiffDataProvider.getSampleActivities("JLS-8492")
        assertTrue("Activities should not be empty", activities.isNotEmpty())
        
        val planActivity = activities.firstOrNull { it.planGenerated != null }
        assertNotNull("Must include plan generated activity", planActivity)
        val steps = planActivity?.planGenerated?.plan?.steps
        assertNotNull("Plan steps must not be null", steps)
        assertEquals(3, steps?.size)
        assertEquals("Locate cursor management routines", steps?.firstOrNull()?.title)

        val agentMsg = activities.firstOrNull { it.agentMessaged != null }
        assertNotNull("Must include agent message", agentMsg)
        assertTrue(agentMsg?.agentMessaged?.message?.contains("SQLite cursor leak") == true)

        val userMsg = activities.firstOrNull { it.userMessaged != null }
        assertNotNull("Must include user message", userMsg)
        assertTrue(userMsg?.userMessaged?.message?.contains("API 24+") == true)

        val prog = activities.firstOrNull { it.progressUpdated != null }
        assertNotNull("Must include progress updated", prog)
        assertEquals(75, prog?.progressUpdated?.progressPercent)
    }

    @Test
    fun testMultiFileDiffList() {
        val files = listOf(
            DiffDataProvider.mainFile,
            DiffDataProvider.secondaryFile1,
            DiffDataProvider.secondaryFile2
        )
        assertEquals(3, files.size)
        assertEquals(".../sync/SyncWorker.kt", files[0].fileName)
        assertEquals(".../sync/SyncWorkerTest.kt", files[1].fileName)
        assertEquals(".../sync/DatabaseHelper.kt", files[2].fileName)

        val totalAdded = files.sumOf { it.addedCount }
        val totalDeleted = files.sumOf { it.deletedCount }
        assertEquals(18 + 42 + 4, totalAdded)
        assertEquals(6 + 0 + 1, totalDeleted)
    }
}
