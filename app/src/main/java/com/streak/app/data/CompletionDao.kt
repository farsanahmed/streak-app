package com.streak.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletionDao {

    @Query("SELECT * FROM completions")
    fun observeAll(): Flow<List<HabitCompletion>>

    @Query("SELECT epochDay FROM completions WHERE habitId = :habitId ORDER BY epochDay ASC")
    fun observeEpochDays(habitId: Long): Flow<List<Long>>

    @Query("SELECT * FROM completions WHERE habitId = :habitId")
    suspend fun getForHabit(habitId: Long): List<HabitCompletion>

    @Query("SELECT epochDay FROM completions WHERE habitId = :habitId")
    suspend fun getEpochDays(habitId: Long): List<Long>

    @Query("SELECT EXISTS(SELECT 1 FROM completions WHERE habitId = :habitId AND epochDay = :epochDay)")
    suspend fun isCompleted(habitId: Long, epochDay: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(completion: HabitCompletion)

    @Query("DELETE FROM completions WHERE habitId = :habitId AND epochDay = :epochDay")
    suspend fun delete(habitId: Long, epochDay: Long)
}
