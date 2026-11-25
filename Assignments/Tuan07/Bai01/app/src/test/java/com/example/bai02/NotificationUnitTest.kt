package com.example.bai02

import com.example.bai02.data.model.NotificationItem
import com.example.bai02.data.model.NotificationUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationUnitTest {

    @Test
    fun sampleNotifications_containsExpectedInitialItemsAndUnreadState() {
        val state = NotificationUiState(notifications = NotificationItem.getSampleNotifications())

        assertFalse(state.isEmpty)
        assertEquals(4, state.notifications.size)
        assertTrue(state.hasUnread)
    }

    @Test
    fun markAllAsRead_marksEveryNotificationAsRead() {
        val initial = NotificationUiState(notifications = NotificationItem.getSampleNotifications())

        val updated = initial.markAllAsRead()

        assertFalse(updated.isEmpty)
        assertFalse(updated.hasUnread)
        assertTrue(updated.notifications.all { !it.isUnread })
    }

    @Test
    fun markAsRead_marksSingleNotificationAsRead() {
        val initial = NotificationUiState(notifications = NotificationItem.getSampleNotifications())

        val updated = initial.markAsRead("notif_1")

        assertFalse(updated.notifications.first { it.id == "notif_1" }.isUnread)
        assertTrue(updated.notifications.first { it.id == "notif_2" }.isUnread)
        assertTrue(updated.hasUnread)
    }

    @Test
    fun emptyNotifications_setsIsEmptyTrueAndHasUnreadFalse() {
        val emptyState = NotificationUiState(notifications = emptyList())

        assertTrue(emptyState.isEmpty)
        assertFalse(emptyState.hasUnread)
    }
}

