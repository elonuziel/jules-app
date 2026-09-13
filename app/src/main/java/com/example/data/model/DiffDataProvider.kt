package com.example.data.model

object DiffDataProvider {

    val mainFile = DiffFile(
        fileName = ".../sync/SyncWorker.kt",
        addedCount = 18,
        deletedCount = 6,
        lines = listOf(
            DiffLine(104, 104, DiffLineType.CONTEXT, "    override suspend fun doWork(): Result {"),
            DiffLine(105, 105, DiffLineType.CONTEXT, "        val db = databaseHelper.readableDatabase"),
            DiffLine(106, null, DiffLineType.DELETION, "        val cursor = db.rawQuery(QUERY_PENDING_BATCH, null)"),
            DiffLine(107, null, DiffLineType.DELETION, "        while (cursor.moveToNext()) { processRow(cursor) }"),
            DiffLine(108, null, DiffLineType.DELETION, "        cursor.close() // Danger: skipped if exception thrown"),
            DiffLine(null, 106, DiffLineType.ADDITION, "        // AutoCloseable lifecycle safely managed via Kotlin use"),
            DiffLine(null, 107, DiffLineType.ADDITION, "        db.rawQuery(QUERY_PENDING_BATCH, null).use { cursor ->"),
            DiffLine(null, 108, DiffLineType.ADDITION, "            while (cursor.moveToNext()) {"),
            DiffLine(null, 109, DiffLineType.ADDITION, "                processRow(cursor)"),
            DiffLine(null, 110, DiffLineType.ADDITION, "            }"),
            DiffLine(null, 111, DiffLineType.ADDITION, "        }"),
            DiffLine(109, 112, DiffLineType.CONTEXT, "        return Result.success()")
        )
    )

    val secondaryFile1 = DiffFile(
        fileName = ".../sync/SyncWorkerTest.kt",
        addedCount = 42,
        deletedCount = 0,
        lines = listOf(
            DiffLine(null, 85, DiffLineType.ADDITION, "    @Test"),
            DiffLine(null, 86, DiffLineType.ADDITION, "    fun testCursorClosedOnIntermittentException() = runTest {"),
            DiffLine(null, 87, DiffLineType.ADDITION, "        // Simulate CursorWindowAllocationException"),
            DiffLine(null, 88, DiffLineType.ADDITION, "        val result = worker.doWork()"),
            DiffLine(null, 89, DiffLineType.ADDITION, "        assertThat(db.activeCursorsCount).isEqualTo(0)"),
            DiffLine(null, 90, DiffLineType.ADDITION, "    }")
        ),
        testDescription = "// 42 test assertions added verifying Cursor allocation metrics under simulated OOM.",
        testPassed = true
    )

    val secondaryFile2 = DiffFile(
        fileName = ".../sync/DatabaseHelper.kt",
        addedCount = 4,
        deletedCount = 1,
        lines = listOf(
            DiffLine(48, 48, DiffLineType.CONTEXT, "    override fun onConfigure(db: SQLiteDatabase) {"),
            DiffLine(49, null, DiffLineType.DELETION, "        super.onConfigure(db)"),
            DiffLine(null, 49, DiffLineType.ADDITION, "        super.onConfigure(db)"),
            DiffLine(null, 50, DiffLineType.ADDITION, "        // Enable StrictMode thread policy tag for cursor leak early detection"),
            DiffLine(null, 51, DiffLineType.ADDITION, "        db.enableWriteAheadLogging()"),
            DiffLine(50, 52, DiffLineType.CONTEXT, "    }")
        ),
        testDescription = "// Added StrictMode thread policy tag for cursor leak early detection.",
        testPassed = true
    )

    fun getSampleActivities(sessionId: String): List<com.example.data.remote.dto.JulesActivityDto> {
        return listOf(
            com.example.data.remote.dto.JulesActivityDto(
                id = "act-1",
                createTime = "2026-09-13T10:00:00Z",
                originator = "ORIGINATOR_AGENT",
                description = "Jules analyzed the issue and drafted an execution plan.",
                planGenerated = com.example.data.remote.dto.JulesPlanGeneratedDto(
                    plan = com.example.data.remote.dto.JulesPlanDto(
                        steps = listOf(
                            com.example.data.remote.dto.JulesPlanStepDto(
                                index = 1,
                                title = "Locate cursor management routines",
                                description = "Identify query execution points in SyncWorker.kt"
                            ),
                            com.example.data.remote.dto.JulesPlanStepDto(
                                index = 2,
                                title = "Wrap with AutoCloseable scoping",
                                description = "Refactor db.rawQuery to use Kotlin .use { } block"
                            ),
                            com.example.data.remote.dto.JulesPlanStepDto(
                                index = 3,
                                title = "Add regression unit tests",
                                description = "Simulate intermittent exceptions and assert cursor closure"
                            )
                        )
                    )
                )
            ),
            com.example.data.remote.dto.JulesActivityDto(
                id = "act-2",
                createTime = "2026-09-13T10:01:00Z",
                originator = "ORIGINATOR_AGENT",
                agentMessaged = com.example.data.remote.dto.JulesAgentMessagedDto(
                    message = "I have drafted the plan to fix the SQLite cursor leak. Please review the plan steps above and approve so I can proceed with code generation."
                )
            ),
            com.example.data.remote.dto.JulesActivityDto(
                id = "act-3",
                createTime = "2026-09-13T10:02:00Z",
                originator = "ORIGINATOR_USER",
                userMessaged = com.example.data.remote.dto.JulesUserMessagedDto(
                    message = "Please ensure strict backward compatibility with Android API 24+."
                )
            ),
            com.example.data.remote.dto.JulesActivityDto(
                id = "act-4",
                createTime = "2026-09-13T10:02:30Z",
                originator = "ORIGINATOR_AGENT",
                agentMessaged = com.example.data.remote.dto.JulesAgentMessagedDto(
                    message = "Understood. The .use extension function is part of kotlin-stdlib and supports API 24+ without desugaring issues."
                )
            ),
            com.example.data.remote.dto.JulesActivityDto(
                id = "act-5",
                createTime = "2026-09-13T10:03:00Z",
                originator = "ORIGINATOR_AGENT",
                progressUpdated = com.example.data.remote.dto.JulesProgressUpdatedDto(
                    progressPercent = 75,
                    message = "Patch synthesized and verifying against test suite"
                )
            )
        )
    }
}
