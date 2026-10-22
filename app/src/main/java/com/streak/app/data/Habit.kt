package com.streak.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A tracked habit.
 *
 * [reminderHour] / [reminderMinute] are null when the user did not pick a reminder time.
 */
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val emoji: String,
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/** True when the habit has a daily reminder time configured. */
val Habit.hasReminder: Boolean
    get() = reminderHour != null && reminderMinute != null
