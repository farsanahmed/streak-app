package com.streak.app.data

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Marks one habit as done on one calendar day.
 *
 * [epochDay] is the number of days since 1970-01-01 in the device's local time zone
 * (see [com.streak.app.util.Dates]), so a row maps 1:1 to a calendar day.
 */
@Entity(
    tableName = "completions",
    primaryKeys = ["habitId", "epochDay"],
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitCompletion(
    val habitId: Long,
    val epochDay: Long,
    val completedAt: Long = System.currentTimeMillis()
)
