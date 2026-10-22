package com.streak.app

import android.app.Application
import com.streak.app.reminders.NotificationHelper

class StreakApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createChannel(this)
    }
}
