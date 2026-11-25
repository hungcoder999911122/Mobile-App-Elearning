package com.example.bai02.data.model

/**
 * Phân loại thông báo để ánh xạ biểu tượng và màu sắc tương ứng trên UI.
 */
enum class NotificationType {
    UPCOMING_DEADLINE,
    OVERDUE,
    GREAT_JOB,
    REMINDER
}

/**
 * Model đại diện cho một mục thông báo trong màn hình Thông báo.
 */
data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timeText: String,
    val isUnread: Boolean = false
) {
    /**
     * Trả về bản sao của thông báo với trạng thái đã đọc.
     */
    fun markAsRead(): NotificationItem = if (isUnread) copy(isUnread = false) else this

    companion object {
        /**
         * Danh sách thông báo mẫu bám sát giao diện bài tập Thực hành 1.
         */
        fun getSampleNotifications(): List<NotificationItem> = listOf(
            NotificationItem(
                id = "notif_1",
                type = NotificationType.UPCOMING_DEADLINE,
                title = "Sắp đến hạn",
                message = "Ôn chương 3 – Cấu trúc dữ liệu lúc 14:00.",
                timeText = "13:45",
                isUnread = true
            ),
            NotificationItem(
                id = "notif_2",
                type = NotificationType.OVERDUE,
                title = "Đã quá hạn",
                message = "Bài đọc tham khảo môn Mạng máy tính chưa xong.",
                timeText = "10:10",
                isUnread = true
            ),
            NotificationItem(
                id = "notif_3",
                type = NotificationType.GREAT_JOB,
                title = "Làm tốt lắm!",
                message = "Bạn đã xong 2 việc trong sáng nay.",
                timeText = "11:30",
                isUnread = false
            ),
            NotificationItem(
                id = "notif_4",
                type = NotificationType.REMINDER,
                title = "Nhắc việc",
                message = "Họp nhóm đồ án trong tuần này.",
                timeText = "Hôm qua",
                isUnread = false
            )
        )
    }
}

