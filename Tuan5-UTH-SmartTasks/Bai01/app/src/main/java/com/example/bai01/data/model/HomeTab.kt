package com.example.bai01.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Enum đóng gói các tab điều hướng dưới đáy (OOP - Type-safe Enumeration).
 * Khắc phục hiện tượng Primitive Obsession (dùng 0, 1, 2, 3 gây khó hiểu và dễ lỗi).
 */
enum class HomeTab(
    val label: String,
    val icon: ImageVector
) {
    HOME(
        label = "Trang chủ",
        icon = Icons.Default.Home
    ),
    TASKS(
        label = "Danh sách",
        icon = Icons.Default.FormatListBulleted
    ),
    NOTIFICATIONS(
        label = "Thông báo",
        icon = Icons.Outlined.Notifications
    ),
    SETTINGS(
        label = "Cài đặt",
        icon = Icons.Default.Tune
    )
}

