package com.example.bai02.data.model

/**
 * Lớp đại diện cho các chỉ số thống kê cá nhân (OOP - Value Object).
 * Đóng gói dữ liệu số và cung cấp các hàm format hiển thị giao diện.
 */
data class UserStats(
    val completedTasks: Int = 128,
    val streakDays: Int = 12,
    val onTimePercentage: Int = 86
) {
    val streakText: String
        get() = "$streakDays ngày"

    val onTimeText: String
        get() = "$onTimePercentage%"

    val completedTasksText: String
        get() = "$completedTasks"
}

/**
 * Lớp đại diện cho hoạt động của từng ngày trong tuần.
 */
data class DayActivity(
    val dayLabel: String,         // "T2", "T3", "T4", "T5", "T6", "T7", "CN"
    val completedCount: Int,      // Số việc đã làm trong ngày
    val isCurrentDay: Boolean = false,
    val isPastDay: Boolean = false
) {
    /**
     * Tỉ lệ chiều cao biểu đồ từ 0.1f đến 1.0f dựa theo mức tối đa trong tuần.
     */
    fun calculateBarFraction(maxCount: Int): Float {
        if (maxCount <= 0) return 0.15f
        return (completedCount.toFloat() / maxCount).coerceIn(0.15f, 1.0f)
    }
}

/**
 * Lớp đại diện cho biểu đồ hoạt động tuần (OOP - Aggregate Object).
 */
data class WeeklyActivity(
    val totalCompleted: Int = 18,
    val totalTarget: Int = 22,
    val days: List<DayActivity> = getDefaultWeek()
) {
    val summaryText: String
        get() = "$totalCompleted/$totalTarget việc"

    val maxCountInWeek: Int
        get() = days.maxOfOrNull { it.completedCount } ?: 1

    companion object {
        fun getDefaultWeek(): List<DayActivity> = listOf(
            DayActivity("T2", completedCount = 5, isPastDay = true),
            DayActivity("T3", completedCount = 8, isCurrentDay = true), // Ngày hiện tại (active)
            DayActivity("T4", completedCount = 3),
            DayActivity("T5", completedCount = 7),
            DayActivity("T6", completedCount = 6),
            DayActivity("T7", completedCount = 1),
            DayActivity("CN", completedCount = 1)
        )
    }
}

