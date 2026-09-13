package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JulesSessionDao {
    @Query("SELECT * FROM jules_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<JulesSessionEntity>>

    @Query("SELECT * FROM jules_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): JulesSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: JulesSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<JulesSessionEntity>)

    @Update
    suspend fun updateSession(session: JulesSessionEntity)

    @Query("DELETE FROM jules_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: String)

    @Query("DELETE FROM jules_sessions WHERE id IN ('JLS-8492', 'JLS-7914', 'JLS-6230') OR id LIKE 'JLS-%'")
    suspend fun deleteSampleSessions()

    @Query("DELETE FROM jules_sessions")
    suspend fun deleteAllSessions()

    @Query("SELECT COUNT(*) FROM jules_sessions")
    suspend fun getSessionCount(): Int
}
