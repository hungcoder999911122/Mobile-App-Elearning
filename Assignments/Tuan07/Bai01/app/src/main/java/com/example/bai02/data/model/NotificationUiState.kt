package com.example.bai02.data.model

/**
 * Trạng thái giao diện (UI State) cho màn hình Thông báo theo mô hình MVVM.
 */
data class NotificationUiState(
    val notifications: List<NotificationItem> = emptyList()
) {
    val isEmpty: Boolean
        get() = notifications.isEmpty()

    val hasUnread: Boolean
        get() = notifications.any { it.isUnread }

    /**
     * Đánh dấu toàn bộ thông báo là đã đọc.
     */
    fun markAllAsRead(): NotificationUiState =
        copy(notifications = notifications.map { it.markAsRead() })

    /**
     * Đánh dấu một thông báo cụ thể là đã đọc.
     */
    fun markAsRead(notificationId: String): NotificationUiState =
        copy(
            notifications = notifications.map { item ->
                if (item.id == notificationId) item.markAsRead() else item
            }
        )
}

