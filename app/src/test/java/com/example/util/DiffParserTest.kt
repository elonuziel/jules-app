package com.example.util

import com.example.data.model.DiffLineType
import com.example.data.remote.dto.GitHubFileDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DiffParserTest {

    @Test
    fun `parsePatch parses addition deletion and context lines`() {
        val patch = """
            @@ -10,4 +10,5 @@ class Sample {
             val a = 1
            -val b = 2
            +val b = 3
            +val c = 4
             val d = 5
        """.trimIndent()

        val lines = DiffParser.parsePatch(patch)
        assertEquals(5, lines.size)

        // Line 1: context "val a = 1"
        assertEquals(DiffLineType.CONTEXT, lines[0].type)
        assertEquals(10, lines[0].oldLineNumber)
        assertEquals(10, lines[0].newLineNumber)
        assertEquals("val a = 1", lines[0].text)

        // Line 2: deletion "val b = 2"
        assertEquals(DiffLineType.DELETION, lines[1].type)
        assertEquals(11, lines[1].oldLineNumber)
        assertNull(lines[1].newLineNumber)
        assertEquals("val b = 2", lines[1].text)

        // Line 3: addition "val b = 3"
        assertEquals(DiffLineType.ADDITION, lines[2].type)
        assertNull(lines[2].oldLineNumber)
        assertEquals(11, lines[2].newLineNumber)
        assertEquals("val b = 3", lines[2].text)

        // Line 4: addition "val c = 4"
        assertEquals(DiffLineType.ADDITION, lines[3].type)
        assertNull(lines[3].oldLineNumber)
        assertEquals(12, lines[3].newLineNumber)
        assertEquals("val c = 4", lines[3].text)

        // Line 5: context "val d = 5"
        assertEquals(DiffLineType.CONTEXT, lines[4].type)
        assertEquals(12, lines[4].oldLineNumber)
        assertEquals(13, lines[4].newLineNumber)
        assertEquals("val d = 5", lines[4].text)
    }

    @Test
    fun `parsePatch handles empty or null patch safely`() {
        assertTrue(DiffParser.parsePatch(null).isEmpty())
        assertTrue(DiffParser.parsePatch("").isEmpty())
        assertTrue(DiffParser.parsePatch("   ").isEmpty())
    }

    @Test
    fun `toDiffFile converts GitHubFileDto to domain DiffFile`() {
        val dto = GitHubFileDto(
            filename = "app/src/main/Sample.kt",
            status = "modified",
            additions = 2,
            deletions = 1,
            changes = 3,
            patch = "@@ -1,2 +1,3 @@\n val a = 1\n-val b = 2\n+val b = 3\n+val c = 4"
        )

        val diffFile = DiffParser.toDiffFile(dto)
        assertEquals("app/src/main/Sample.kt", diffFile.fileName)
        assertEquals(2, diffFile.addedCount)
        assertEquals(1, diffFile.deletedCount)
        assertEquals(4, diffFile.lines.size)
        assertTrue(diffFile.testPassed)
    }
}

