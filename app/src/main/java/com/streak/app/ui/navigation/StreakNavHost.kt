package com.streak.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.streak.app.ui.detail.HabitDetailScreen
import com.streak.app.ui.detail.HabitDetailViewModel
import com.streak.app.ui.editor.HabitEditorScreen
import com.streak.app.ui.editor.HabitEditorViewModel
import com.streak.app.ui.home.HomeScreen
import com.streak.app.ui.home.HomeViewModel
import com.streak.app.ui.rememberAppContainer

const val NEW_HABIT_ID = 0L

object Routes {
    const val HOME = "home"
    const val EDITOR = "editor/{habitId}"
    const val DETAIL = "detail/{habitId}"

    fun editor(habitId: Long): String = "editor/$habitId"

    fun detail(habitId: Long): String = "detail/$habitId"
}

@Composable
fun StreakNavHost() {
    val navController = rememberNavController()
    val container = rememberAppContainer()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(route = Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { HomeViewModel(container.habitRepository) }
                }
            )
            val habits by viewModel.habits.collectAsStateWithLifecycle()
            HomeScreen(
                habits = habits,
                onToggle = viewModel::toggleToday,
                onOpen = { navController.navigate(Routes.detail(it.habit.id)) },
                onEdit = { navController.navigate(Routes.editor(it.habit.id)) },
                onAdd = { navController.navigate(Routes.editor(NEW_HABIT_ID)) },
                onSettings = { }
            )
        }

        composable(
            route = Routes.EDITOR,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: NEW_HABIT_ID
            val editorViewModel: HabitEditorViewModel = viewModel(
                key = "editor-$habitId",
                factory = viewModelFactory {
                    initializer { HabitEditorViewModel(container.habitRepository, habitId) }
                }
            )
            val state by editorViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(state.finished) {
                if (state.finished) {
                    navController.popBackStack()
                }
            }

            HabitEditorScreen(
                state = state,
                onNameChange = editorViewModel::onNameChange,
                onEmojiChange = editorViewModel::onEmojiChange,
                onReminderEnabledChange = editorViewModel::onReminderEnabledChange,
                onTimeChange = editorViewModel::onTimeChange,
                onSave = editorViewModel::save,
                onDelete = editorViewModel::delete,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: NEW_HABIT_ID
            val detailViewModel: HabitDetailViewModel = viewModel(
                key = "detail-$habitId",
                factory = viewModelFactory {
                    initializer { HabitDetailViewModel(container.habitRepository, habitId) }
                }
            )
            val state by detailViewModel.uiState.collectAsStateWithLifecycle()

            HabitDetailScreen(
                state = state,
                onToggleDay = detailViewModel::toggleDay,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
