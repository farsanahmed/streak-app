package com.streak.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.streak.app.AppContainer
import com.streak.app.StreakApplication

/** Access to the app wide dependency container from composables. */
@Composable
fun rememberAppContainer(): AppContainer {
    val context = LocalContext.current
    return remember(context) {
        (context.applicationContext as StreakApplication).container
    }
}
