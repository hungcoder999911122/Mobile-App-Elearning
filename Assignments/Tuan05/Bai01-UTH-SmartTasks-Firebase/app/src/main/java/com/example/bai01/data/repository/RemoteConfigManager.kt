package com.example.bai01.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

/**
 * Lớp quản lý cấu hình từ xa Firebase Remote Config (OOP - Singleton/Manager Pattern)
 */
object RemoteConfigManager {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    // Tên biến khóa cấu hình trên Firebase Console
    const val KEY_BANNER_MESSAGE = "banner_message"

    fun init() {
        // 1. Cấu hình thời gian làm mới: Trong môi trường học tập/Debug, đặt = 0 giây
        // để khi bạn đổi trên web là app nhận được ngay lập tức (không bị cache)
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0L
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // 2. Thiết lập giá trị mặc định dưới máy (đề phòng khi mất mạng)
        val defaultValues = mapOf(
            KEY_BANNER_MESSAGE to "Bắt đầu từ việc nhỏ nhất nhé."
        )
        remoteConfig.setDefaultsAsync(defaultValues)

        // 3. Kéo dữ liệu mới nhất từ máy chủ Firebase về và kích hoạt
        fetchLatestConfig()
    }

    fun fetchLatestConfig(onComplete: (() -> Unit)? = null) {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("REMOTE_CONFIG", "Lấy cấu hình thành công: ${getBannerMessage()}")
            } else {
                Log.e("REMOTE_CONFIG", "Lỗi lấy cấu hình từ xa")
            }
            onComplete?.invoke()
        }
    }

    /**
     * Lấy câu thông điệp mới nhất từ Firebase về hiển thị
     */
    fun getBannerMessage(): String {
        return remoteConfig.getString(KEY_BANNER_MESSAGE)
    }
}