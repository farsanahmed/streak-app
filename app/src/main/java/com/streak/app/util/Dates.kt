package com.streak.app.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Calendar-day helpers. Days are identified by their epoch day number (local time). */
object Dates {

    const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L

    /** Today as an epoch day in the device's local time zone. */
    fun todayEpochDay(zone: ZoneId = ZoneId.systemDefault()): Long =
        LocalDate.now(zone).toEpochDay()

    fun epochDayToDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

    fun dateToEpochDay(date: LocalDate): Long = date.toEpochDay()

    fun formatTime(hour: Int, minute: Int): String =
        String.format("%02d:%02d", hour, minute)

    fun formatDate(epochDay: Long, pattern: String = "EEE d MMM"): String =
        epochDayToDate(epochDay).format(DateTimeFormatter.ofPattern(pattern))
}
