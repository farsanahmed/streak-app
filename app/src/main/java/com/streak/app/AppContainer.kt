package com.streak.app

import android.content.Context
import com.streak.app.data.HabitRepository
import com.streak.app.data.StreakDatabase

/**
 * Tiny manual dependency container. Keeping it hand rolled avoids extra build plugins
 * while still giving a single place to own the database and the repository.
 */
class AppContainer(context: Context) {

    private val appContext: Context = context.applicationContext

    val database: StreakDatabase by lazy { StreakDatabase.getInstance(appContext) }

    val habitRepository: HabitRepository by lazy {
        HabitRepository(database.habitDao(), database.completionDao())
    }
}
