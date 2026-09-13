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
}
