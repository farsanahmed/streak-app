package com.streak.app.reminders

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.streak.app.StreakApplication
import com.streak.app.data.Habit

/**
 * Posts the daily reminder for one habit and schedules the reminder for the next day.
 */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val habitId = inputData.getLong(KEY_HABIT_ID, -1L)
        if (habitId < 0L) {
            Log.w(TAG, "Reminder work started without a habit id")
            return Result.failure()
        }
        val container = (applicationContext as? StreakApplication)?.container
        val habit = container?.habitRepository?.getHabit(habitId)
        if (habit == null) {
            Log.w(TAG, "Habit $habitId no longer exists, dropping reminder")
            return Result.success()
        }
        NotificationHelper.showReminder(applicationContext, habit)
        ReminderScheduler(applicationContext).scheduleForTomorrow(habit)
        Log.i(TAG, "Reminder work finished for habit $habitId")
        return Result.success()
    }

    companion object {
        const val TAG = "StreakReminder"
        const val KEY_HABIT_ID = "habit_id"
        const val TAG_REMINDER = "habit-reminder"
    }
}
