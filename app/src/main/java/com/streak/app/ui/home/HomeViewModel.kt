package com.streak.app.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** UI model for a habit row on the home screen. */
data class HabitUi(
    val id: Long,
    val name: String,
    val emoji: String,
    val streak: Int,
    val doneToday: Boolean
)

/**
 * Milestone 1: home screen backed by hard coded sample data.
 * Replaced by the Room backed view model in milestone 2.
 */
class HomeViewModel : ViewModel() {

    private val _habits = MutableStateFlow(
        listOf(
            HabitUi(id = 1L, name = "Drink water", emoji = "💧", streak = 5, doneToday = false),
            HabitUi(id = 2L, name = "Read 20 pages", emoji = "📖", streak = 12, doneToday = true),
            HabitUi(id = 3L, name = "Workout", emoji = "🏋️", streak = 0, doneToday = false),
            HabitUi(id = 4L, name = "Meditate", emoji = "🧘", streak = 3, doneToday = false)
        )
    )
    val habits: StateFlow<List<HabitUi>> = _habits.asStateFlow()

    fun toggleToday(habit: HabitUi) {
        _habits.value = _habits.value.map { item ->
            if (item.id != habit.id) {
                item
            } else {
                item.copy(
                    doneToday = !item.doneToday,
                    streak = if (item.doneToday) (item.streak - 1).coerceAtLeast(0) else item.streak + 1
                )
            }
        }
    }
}
