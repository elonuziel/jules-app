package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [JulesSessionEntity::class], version = 3, exportSchema = false)
abstract class JulesDatabase : RoomDatabase() {
    abstract fun sessionDao(): JulesSessionDao

    companion object {
        @Volatile
        private var INSTANCE: JulesDatabase? = null

        fun getInstance(context: Context): JulesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JulesDatabase::class.java,
                    "jules_workspace.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
