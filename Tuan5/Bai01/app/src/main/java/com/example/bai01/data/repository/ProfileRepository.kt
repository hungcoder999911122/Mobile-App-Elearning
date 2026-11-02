package com.example.bai01.data.repository

import com.example.bai01.data.model.ProfileUiState
import com.example.bai01.data.model.UserProfile
import com.example.bai01.data.model.UserStats
import com.example.bai01.data.model.WeeklyActivity

/**
 * Interface Repository cho màn hình Profile (OOP - Abstraction).
 * Chuẩn bị sẵn để tích hợp Firebase Auth / Firestore trong các bước tiếp theo.
 */
interface ProfileRepository {
    fun getProfileUiState(): ProfileUiState
    fun syncUserData(): ProfileUiState
}

/**
 * Triển khai Mock/Fake Repository phục vụ thiết kế giao diện ban đầu.
 */
class FakeProfileRepository : ProfileRepository {
    private var currentState = ProfileUiState(
        user = UserProfile(
            id = "user_01",
            fullName = "Minh Anh",
            email = "minhanh@gmail.com",
            isGoogleLinked = true,
            customInitials = "MA"
        ),
        stats = UserStats(
            completedTasks = 128,
            streakDays = 12,
            onTimePercentage = 86
        ),
        weeklyActivity = WeeklyActivity(
            totalCompleted = 18,
            totalTarget = 22,
            days = WeeklyActivity.getDefaultWeek()
        ),
        lastSyncTimeText = "Vừa xong"
    )

    override fun getProfileUiState(): ProfileUiState = currentState

    override fun syncUserData(): ProfileUiState {
        currentState = currentState.onSynced()
        return currentState
    }
}

