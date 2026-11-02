package com.example.bai01.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Lớp đại diện cho danh mục công việc (OOP - Domain Entity).
 * Đóng gói cả thông tin định danh và giao diện hiển thị (icon, màu sắc chủ đạo).
 */
data class TaskCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBgColor: Color,
    val tagBgColor: Color,
    val tagTextColor: Color
) {
    companion object {
        // Định nghĩa sẵn các danh mục mặc định chuẩn theo thiết kế (Factory/Preset pattern)
        val HOC_TAP = TaskCategory(
            id = "cat_hoc_tap",
            name = "Học tập",
            icon = Icons.Default.School,
            iconColor = Color(0xFF2F69FE),
            iconBgColor = Color(0xFFEBF1FF),
            tagBgColor = Color(0xFFEDF2FE),
            tagTextColor = Color(0xFF2F69FE)
        )

        val CUOC_SONG = TaskCategory(
            id = "cat_cuoc_song",
            name = "Cuộc sống",
            icon = Icons.Outlined.Home,
            iconColor = Color(0xFFE67E22),
            iconBgColor = Color(0xFFFFF0E6),
            tagBgColor = Color(0xFFFFF0E6),
            tagTextColor = Color(0xFFE67E22)
        )

        fun getDefaultCategories(): List<TaskCategory> = listOf(HOC_TAP, CUOC_SONG)
    }
}

