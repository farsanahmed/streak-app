package com.streak.app.data

/**
 * Pure streak arithmetic. Kept free of Android and database types so it can be unit tested
 * on the JVM.
 */
object StreakCalculator {

    /**
     * Length of the streak that is still alive today.
     *
     * A day that is not completed yet does not break the streak: the streak is then counted
     * backwards from yesterday. A missing yesterday (and today) means the streak is over.
     */
    fun currentStreak(completedDays: Set<Long>, today: Long): Int {
        if (completedDays.isEmpty()) return 0
        var cursor = if (completedDays.contains(today)) today else today - 1
        if (!completedDays.contains(cursor)) return 0
        var streak = 0
        while (completedDays.contains(cursor)) {
            streak++
            cursor--
        }
        return streak
    }

    /** Longest run of consecutive completed days ever recorded. */
    fun bestStreak(completedDays: Set<Long>): Int {
        if (completedDays.isEmpty()) return 0
        val sorted = completedDays.sorted()
        var best = 1
        var run = 1
        for (index in 1 until sorted.size) {
            run = if (sorted[index] == sorted[index - 1] + 1) run + 1 else 1
            if (run > best) best = run
        }
        return best
    }

    /** Percentage (0..100) of the given window that was completed. */
    fun completionRate(completedDays: Set<Long>, windowDays: Int): Int {
        if (windowDays <= 0) return 0
        val inWindow = completedDays.count { it >= windowDays }
        return (inWindow * 100) / windowDays
    }
}
