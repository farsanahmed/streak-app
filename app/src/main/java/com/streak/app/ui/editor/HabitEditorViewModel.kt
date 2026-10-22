package com.streak.app.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streak.app.data.Habit
import com.streak.app.data.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val DEFAULT_EMOJI = "⭐"

val EMOJI_PRESETS = listOf(
    "⭐", "🏃", "💧", "📖", "🧘", "🏋️",
    "🍎", "🎨", "🎸", "🐶", "🌱", "☀️", "💊", "🧹", "✍️", "🚶"
)

data class HabitEditorUiState(
    val isNew: Boolean = true,
    val name: String = "",
    val emoji: String = DEFAULT_EMOJI,
    val reminderEnabled: Boolean = false,
    val hour: Int = 9,
    val minute: Int = 0,
    val loading: Boolean = false,
    val finished: Boolean = false
) {
    val canSave: Boolean get() = name.isNotBlank()
    val title: String get() = if (isNew) "New habit" else "Edit habit"
}

class HabitEditorViewModel(
    private val repository: HabitRepository,
    private val habitId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HabitEditorUiState(isNew = habitId <= 0L, loading = habitId > 0L)
    )
    val uiState: StateFlow<HabitEditorUiState> = _uiState.asStateFlow()

    init {
        if (habitId > 0L) {
            viewModelScope.launch {
                val habit = repository.getHabit(habitId)
                _uiState.value = if (habit == null) {
                    _uiState.value.copy(loading = false)
                } else {
                    HabitEditorUiState(
                        isNew = false,
                        name = habit.name,
                        emoji = habit.emoji,
                        reminderEnabled = habit.hasReminder,
                        hour = habit.reminderHour ?: 9,
                        minute = habit.reminderMinute ?: 0,
                        loading = false
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun onEmojiChange(value: String) {
        _uiState.value = _uiState.value.copy(emoji = value.ifBlank { DEFAULT_EMOJI })
    }

    fun onReminderEnabledChange(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(reminderEnabled = enabled)
    }

    fun onTimeChange(hour: Int, minute: Int) {
        _uiState.value = _uiState.value.copy(hour = hour, minute = minute)
    }

    fun save() {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            val existing = if (habitId > 0L) repository.getHabit(habitId) else null
            val habit = Habit(
                id = habitId,
                name = state.name.trim(),
                emoji = state.emoji.ifBlank { DEFAULT_EMOJI },
                reminderHour = if (state.reminderEnabled) state.hour else null,
                reminderMinute = if (state.reminderEnabled) state.minute else null,
                createdAt = existing?.createdAt ?: System.currentTimeMillis()
            )
            repository.saveHabit(habit)
            _uiState.value = _uiState.value.copy(finished = true)
        }
    }

    fun delete() {
        if (habitId <= 0L) return
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            _uiState.value = _uiState.value.copy(finished = true)
        }
    }
}
