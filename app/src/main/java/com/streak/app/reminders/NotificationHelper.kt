package com.streak.app.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.streak.app.MainActivity
import com.streak.app.R
import com.streak.app.data.Habit

/** Creates the notification channel and posts the daily reminder notifications. */
object NotificationHelper {

    const val TAG = "StreakReminder"
    const val CHANNEL_ID = "streak_reminders"
    const val EXTRA_HABIT_ID = "habit_id"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Habit reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily reminders for the habits you track."
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
        Log.i(TAG, "Notification channel ready: $CHANNEL_ID")
    }

    fun canPostNotifications(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    fun showReminder(context: Context, habit: Habit) {
        if (!canPostNotifications(context)) {
            Log.w(TAG, "POST_NOTIFICATIONS not granted, skipping habit ${habit.id}")
            return
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_HABIT_ID, habit.id)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            habit.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("${habit.emoji} ${habit.name}")
            .setContentText("Keep your streak going — tap to mark it done.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(habit.id.toInt(), notification)
            Log.i(TAG, "Reminder notification posted for habit ${habit.id}")
        } catch (error: SecurityException) {
            Log.w(TAG, "Could not post notification for habit ${habit.id}: ${error.message}")
        }
    }
}
