package com.example.bai01.data.model

/**
 * Trạng thái giao diện toàn màn hình Profile (OOP - State Holder Pattern).
 * Đóng gói toàn bộ thông tin của màn hình Hồ sơ cá nhân.
 */
data class ProfileUiState(
    val user: UserProfile = UserProfile(),
    val stats: UserStats = UserStats(),
    val weeklyActivity: WeeklyActivity = WeeklyActivity(),
    val lastSyncTimeText: String = "Vừa xong",
    val isLoading: Boolean = false
) {
    /**
     * Hành vi cập nhật thời gian đồng bộ dữ liệu.
     */
    fun onSynced(): ProfileUiState {
        return this.copy(lastSyncTimeText = "Vừa xong")
    }

    /**
     * Bật/tắt liên kết Google.
     */
    fun toggleGoogleLink(): ProfileUiState {
        return this.copy(user = user.copy(isGoogleLinked = !user.isGoogleLinked))
    }
}

