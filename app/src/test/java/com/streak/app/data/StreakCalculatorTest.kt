package com.streak.app.data

import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculatorTest {

    private val today = 20_000L

    @Test
    fun `no completions means no streak`() {
        assertEquals(0, StreakCalculator.currentStreak(emptySet(), today))
        assertEquals(0, StreakCalculator.bestStreak(emptySet()))
    }

    @Test
    fun `completing today starts a streak of one`() {
        val days = setOf(today)
        assertEquals(1, StreakCalculator.currentStreak(days, today))
        assertEquals(1, StreakCalculator.bestStreak(days))
    }

    @Test
    fun `consecutive days are counted`() {
        val days = setOf(today, today - 1, today - 2)
        assertEquals(3, StreakCalculator.currentStreak(days, today))
        assertEquals(3, StreakCalculator.bestStreak(days))
    }

    @Test
    fun `a missing today does not break the streak`() {
        val days = setOf(today - 1, today - 2)
        assertEquals(2, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `a missing yesterday ends the streak`() {
        val days = setOf(today, today - 2, today - 3)
        assertEquals(1, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `two missing days end the streak`() {
        val days = setOf(today - 2, today - 3)
        assertEquals(0, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `best streak survives gaps`() {
        val days = setOf(
            today, today - 1,
            today - 5, today - 6, today - 7, today - 8,
            today - 20
        )
        assertEquals(4, StreakCalculator.bestStreak(days))
        assertEquals(2, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `best streak counts an old run`() {
        val days = setOf(today - 100, today - 101, today - 102)
        assertEquals(3, StreakCalculator.bestStreak(days))
        assertEquals(0, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `completion rate is a percentage of the window`() {
        val days = setOf(today, today - 1, today - 2)
        assertEquals(30, StreakCalculator.completionRate(days, windowDays = 10))
        assertEquals(0, StreakCalculator.completionRate(emptySet(), windowDays = 30))
    }
}
