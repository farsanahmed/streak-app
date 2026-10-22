package com.streak.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.util.Log

@Database(
    entities = [Habit::class, HabitCompletion::class],
    version = 1,
    exportSchema = true
)
abstract class StreakDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    abstract fun completionDao(): CompletionDao

    companion object {
        const val TAG = "StreakDb"
        private const val DB_NAME = "streak.db"

        @Volatile
        private var instance: StreakDatabase? = null

        fun getInstance(context: Context): StreakDatabase =
            instance ?: synchronized(this) {
                instance ?: Room
                    .databaseBuilder(context.applicationContext, StreakDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        instance = it
                        Log.i(TAG, "Database opened: $DB_NAME")
                    }
            }
    }
}
