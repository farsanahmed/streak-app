package com.streak.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streak.app.data.HabitRepository
import com.streak.app.data.HabitWithStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HabitRepository) : ViewModel() {

    val habits: StateFlow<List<HabitWithStatus>> = repository.observeHabitsWithStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun toggleToday(habit: HabitWithStatus) {
        viewModelScope.launch {
            repository.toggleToday(habit.habit.id)
        }
    }
}
