package com.streak.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.streak.app.ui.home.HomeScreen
import com.streak.app.ui.home.HomeViewModel

object Routes {
    const val HOME = "home"
}

@Composable
fun StreakNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(route = Routes.HOME) {
            val viewModel: HomeViewModel = viewModel()
            val habits by viewModel.habits.collectAsStateWithLifecycle()
            HomeScreen(
                habits = habits,
                onToggle = viewModel::toggleToday,
                onOpen = { },
                onAdd = { },
                onSettings = { }
            )
        }
    }
}
