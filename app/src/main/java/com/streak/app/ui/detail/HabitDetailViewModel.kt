package com.streak.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streak.app.data.Habit
import com.streak.app.data.HabitRepository
import com.streak.app.data.StreakCalculator
import com.streak.app.util.Dates
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HabitDetailUiState(
    val habit: Habit? = null,
    val completedDays: Set<Long> = emptySet(),
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalDone: Int = 0
)

class HabitDetailViewModel(
    private val repository: HabitRepository,
    private val habitId: Long
) : ViewModel() {

    val uiState: StateFlow<HabitDetailUiState> =
        combine(
            repository.observeHabit(habitId),
            repository.observeEpochDays(habitId)
        ) { habit, epochDays ->
            val days = epochDays.toSet()
            HabitDetailUiState(
                habit = habit,
                completedDays = days,
                currentStreak = StreakCalculator.currentStreak(days, Dates.todayEpochDay()),
                bestStreak = StreakCalculator.bestStreak(days),
                totalDone = days.size
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HabitDetailUiState()
        )

    fun toggleDay(epochDay: Long) {
        viewModelScope.launch {
            val done = uiState.value.completedDays.contains(epochDay)
            repository.setCompleted(habitId, epochDay, !done)
        }
    }
}
