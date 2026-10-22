package com.streak.app.data

import android.util.Log
import com.streak.app.util.Dates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** A habit together with the state needed to render it on the home screen. */
data class HabitWithStatus(
    val habit: Habit,
    val doneToday: Boolean
)

/**
 * Single source of truth for habits and their completions.
 * All database access goes through here so that logging stays consistent.
 */
class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: CompletionDao
) {

    fun observeHabitsWithStatus(): Flow<List<HabitWithStatus>> =
        combine(habitDao.observeHabits(), completionDao.observeAll()) { habits, completions ->
            val today = Dates.todayEpochDay()
            val daysByHabit = completions.groupBy { it.habitId }
            habits.map { habit ->
                val days = daysByHabit[habit.id]?.map { it.epochDay }?.toSet().orEmpty()
                HabitWithStatus(habit = habit, doneToday = days.contains(today))
            }
        }

    fun observeHabit(id: Long): Flow<Habit?> = habitDao.observeHabit(id)

    fun observeEpochDays(habitId: Long): Flow<List<Long>> = completionDao.observeEpochDays(habitId)

    suspend fun getHabit(id: Long): Habit? = habitDao.getHabit(id)

    suspend fun getAllHabits(): List<Habit> = habitDao.getAllHabits()

    suspend fun getEpochDays(habitId: Long): List<Long> = completionDao.getEpochDays(habitId)

    suspend fun saveHabit(habit: Habit): Long = if (habit.id == 0L) {
        val newId = habitDao.insert(habit)
        Log.i(TAG, "Inserted habit id=$newId name='${habit.name}'")
        newId
    } else {
        habitDao.update(habit)
        Log.i(TAG, "Updated habit id=${habit.id} name='${habit.name}'")
        habit.id
    }

    suspend fun deleteHabit(id: Long) {
        habitDao.deleteById(id)
        Log.i(TAG, "Deleted habit id=$id (completions removed by cascade)")
    }

    suspend fun setCompleted(habitId: Long, epochDay: Long, done: Boolean) {
        if (done) {
            completionDao.insert(HabitCompletion(habitId = habitId, epochDay = epochDay))
        } else {
            completionDao.delete(habitId, epochDay)
        }
        Log.i(TAG, "habit=$habitId epochDay=$epochDay completed=$done")
    }

    suspend fun toggleToday(habitId: Long) {
        val today = Dates.todayEpochDay()
        val done = completionDao.isCompleted(habitId, today)
        setCompleted(habitId, today, !done)
    }

    companion object {
        const val TAG = "StreakRepo"
    }
}
