package com.streak.app.reminders

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.streak.app.data.Habit
import com.streak.app.data.hasReminder
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

/**
 * Schedules one work request per habit and day. The worker re-schedules itself for the next
 * day when it fires, so no exact alarm permission is required.
 */
class ReminderScheduler(context: Context) {

    private val appContext: Context = context.applicationContext
    private val workManager: WorkManager by lazy { WorkManager.getInstance(appContext) }

    /** Schedules the next reminder for the habit (today if the time is still ahead). */
    fun schedule(habit: Habit) {
        if (!habit.hasReminder) {
            cancel(habit.id)
            return
        }
        val hour = habit.reminderHour ?: return
        val minute = habit.reminderMinute ?: return
        val now = ZonedDateTime.now()
        var target = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        // Safety margin so that a worker that fires a few milliseconds early does not
        // re-schedule itself for the very same day.
        if (!target.isAfter(now.plusSeconds(60))) {
            target = target.plusDays(1)
        }
        enqueue(habit, target.toLocalDate(), Duration.between(now, target).toMillis())
    }

    /** Schedules the reminder for tomorrow. Called by the worker after it fires. */
    fun scheduleForTomorrow(habit: Habit) {
        if (!habit.hasReminder) return
        val hour = habit.reminderHour ?: return
        val minute = habit.reminderMinute ?: return
        val now = ZonedDateTime.now()
        val tomorrow = LocalDate.now().plusDays(1)
        val target = now.plusDays(1).withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        enqueue(habit, tomorrow, Duration.between(now, target).toMillis())
    }

    fun cancel(habitId: Long) {
        workManager.cancelAllWorkByTag(tagFor(habitId))
        Log.i(TAG, "Cancelled reminders for habit $habitId")
    }

    fun rescheduleAll(habits: List<Habit>) {
        habits.forEach { schedule(it) }
        Log.i(TAG, "Rescheduled reminders for ${habits.size} habits")
    }

    private fun enqueue(habit: Habit, day: LocalDate, delayMillis: Long) {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(ReminderWorker.KEY_HABIT_ID to habit.id))
            .addTag(ReminderWorker.TAG_REMINDER)
            .addTag(tagFor(habit.id))
            .build()
        val name = workName(habit.id, day.toEpochDay())
        workManager.enqueueUniqueWork(name, ExistingWorkPolicy.REPLACE, request)
        Log.i(TAG, "Scheduled habit ${habit.id} for $day (in ${delayMillis}ms, work=$name)")
    }

    companion object {
        const val TAG = "StreakReminder"

        fun tagFor(habitId: Long): String = "habit-reminder-$habitId"

        fun workName(habitId: Long, epochDay: Long): String = "habit-reminder-$habitId-$epochDay"
    }
}
