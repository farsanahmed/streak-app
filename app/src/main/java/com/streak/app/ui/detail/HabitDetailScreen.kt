package com.streak.app.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.streak.app.data.hasReminder
import com.streak.app.util.Dates
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

const val GRID_DAYS = 30
private const val COLUMNS = 7

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    state: HabitDetailUiState,
    onToggleDay: (Long) -> Unit,
    onBack: () -> Unit
) {
    val habit = state.habit
    val today = Dates.todayEpochDay()
    val gridDays = remember(today) { (GRID_DAYS - 1 downTo 0).map { today - it } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = habit?.name ?: "Habit",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (habit != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = habit.emoji, style = MaterialTheme.typography.displaySmall)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = habit.name, style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = if (habit.hasReminder) {
                                "Reminder at ${Dates.formatTime(habit.reminderHour ?: 0, habit.reminderMinute ?: 0)}"
                            } else {
                                "No reminder"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Current streak",
                    value = state.currentStreak.toString()
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Best streak",
                    value = state.bestStreak.toString()
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Days done",
                    value = state.totalDone.toString()
                )
            }

            Text(text = "Last $GRID_DAYS days", style = MaterialTheme.typography.titleMedium)

            WeekdayHeader()

            gridDays.chunked(COLUMNS).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        DayCell(
                            epochDay = day,
                            done = state.completedDays.contains(day),
                            isToday = day == today,
                            onClick = { onToggleDay(day) }
                        )
                    }
                    repeat(COLUMNS - week.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Text(
                text = "Green = done, grey = missed. Tap a day to correct it.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RowScope.WeekdayHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        repeat(COLUMNS) { index ->
            val dayOfWeek = DayOfWeek.of(index + 1)
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RowScope.DayCell(epochDay: Long, done: Boolean, isToday: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    val background = when {
        done -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.surfaceVariant
        else -> Color.Transparent
    }
    val contentColor = when {
        done -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(3.dp)
            .background(color = background, shape = shape)
            .border(
                width = if (isToday && !done) 1.dp else 0.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = Dates.epochDayToDate(epochDay).dayOfMonth.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}
