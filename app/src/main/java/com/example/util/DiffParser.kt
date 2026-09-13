package com.example.util

import com.example.data.model.DiffFile
import com.example.data.model.DiffLine
import com.example.data.model.DiffLineType
import com.example.data.remote.dto.GitHubFileDto
import java.util.regex.Pattern

object DiffParser {

    private val HUNK_HEADER_PATTERN = Pattern.compile("^@@\\s+-(\\d+)(?:,(\\d+))?\\s+\\+(\\d+)(?:,(\\d+))?\\s+@@(.*)$")

    /**
     * Parses a unified git patch string into a structured list of [DiffLine] items
     * with exact line numbers and type distinctions.
     */
    fun parsePatch(patch: String?): List<DiffLine> {
        if (patch.isNullOrBlank()) {
            return emptyList()
        }

        val result = mutableListOf<DiffLine>()
        var currentOldLine = 1
        var currentNewLine = 1

        val lines = patch.lines()
        for (rawLine in lines) {
            val matcher = HUNK_HEADER_PATTERN.matcher(rawLine)
            if (matcher.find()) {
                currentOldLine = matcher.group(1)?.toIntOrNull() ?: 1
                currentNewLine = matcher.group(3)?.toIntOrNull() ?: 1
                continue
            }

            if (rawLine.startsWith("\\ No newline at end of file")) {
                continue
            }

            if (rawLine.startsWith("+")) {
                result.add(
                    DiffLine(
                        oldLineNumber = null,
                        newLineNumber = currentNewLine++,
                        type = DiffLineType.ADDITION,
                        text = rawLine.substring(1)
                    )
                )
            } else if (rawLine.startsWith("-")) {
                result.add(
                    DiffLine(
                        oldLineNumber = currentOldLine++,
                        newLineNumber = null,
                        type = DiffLineType.DELETION,
                        text = rawLine.substring(1)
                    )
                )
            } else if (rawLine.startsWith(" ")) {
                result.add(
                    DiffLine(
                        oldLineNumber = currentOldLine++,
                        newLineNumber = currentNewLine++,
                        type = DiffLineType.CONTEXT,
                        text = rawLine.substring(1)
                    )
                )
            } else if (rawLine.isNotEmpty()) {
                // Fallback for lines without standard git prefix
                result.add(
                    DiffLine(
                        oldLineNumber = currentOldLine++,
                        newLineNumber = currentNewLine++,
                        type = DiffLineType.CONTEXT,
                        text = rawLine
                    )
                )
            }
        }

        return result
    }

    /**
     * Converts a [GitHubFileDto] into a domain [DiffFile] with parsed diff lines.
     */
    fun toDiffFile(fileDto: GitHubFileDto): DiffFile {
        val parsedLines = parsePatch(fileDto.patch)
        return DiffFile(
            fileName = fileDto.filename,
            addedCount = fileDto.additions,
            deletedCount = fileDto.deletions,
            lines = parsedLines,
            testDescription = if (fileDto.filename.contains("test", ignoreCase = true)) {
                "// Tests in ${fileDto.filename}: ${fileDto.changes} changes"
            } else {
                "// ${fileDto.filename}: +${fileDto.additions} -${fileDto.deletions}"
            },
            testPassed = true
        )
    }
}
