package com.example.bai02.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bai02.data.model.NotificationItem
import com.example.bai02.data.model.NotificationUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý danh sách thông báo và logic tự động làm rỗng sau 10 giây theo mô hình MVVM.
 */
class NotificationViewModel(
    initialNotifications: List<NotificationItem> = NotificationItem.getSampleNotifications(),
    private val autoClearDelayMillis: Long = DEFAULT_AUTO_CLEAR_DELAY_MS
) : ViewModel() {

    constructor() : this(
        initialNotifications = NotificationItem.getSampleNotifications(),
        autoClearDelayMillis = DEFAULT_AUTO_CLEAR_DELAY_MS
    )

    private val _uiState = MutableStateFlow(
        NotificationUiState(notifications = initialNotifications)
    )
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private var autoClearJob: Job? = null

    /**
     * Bắt đầu đếm ngược 10 giây khi người dùng mở tab Thông báo lần đầu.
     * Sau 10 giây, danh sách thông báo được đưa về rỗng để UI tự chuyển sang Empty View.
     */
    fun onNotificationTabOpened() {
        if (autoClearJob != null || _uiState.value.isEmpty) return

        autoClearJob = viewModelScope.launch {
            delay(autoClearDelayMillis)
            _uiState.update { it.copy(notifications = emptyList()) }
        }
    }

    /**
     * Đánh dấu tất cả thông báo là đã đọc.
     */
    fun markAllAsRead() {
        _uiState.update { it.markAllAsRead() }
    }

    /**
     * Đánh dấu một thông báo cụ thể là đã đọc khi người dùng nhấn vào.
     */
    fun markAsRead(notificationId: String) {
        _uiState.update { it.markAsRead(notificationId) }
    }

    companion object {
        const val DEFAULT_AUTO_CLEAR_DELAY_MS = 10_000L
    }
}

