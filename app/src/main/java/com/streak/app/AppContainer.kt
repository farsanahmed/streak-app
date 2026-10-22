package com.streak.app

import android.content.Context
import com.streak.app.data.HabitRepository
import com.streak.app.data.StreakDatabase
import com.streak.app.reminders.ReminderScheduler
import com.streak.app.settings.SettingsRepository

/**
 * Tiny manual dependency container. Keeping it hand rolled avoids extra build plugins
 * while still giving a single place to own the database, repository and settings.
 */
class AppContainer(context: Context) {

    private val appContext: Context = context.applicationContext

    val database: StreakDatabase by lazy { StreakDatabase.getInstance(appContext) }

    val habitRepository: HabitRepository by lazy {
        HabitRepository(database.habitDao(), database.completionDao())
    }

    val settingsRepository: SettingsRepository by lazy { SettingsRepository(appContext) }

    val reminderScheduler: ReminderScheduler by lazy { ReminderScheduler(appContext) }
}
