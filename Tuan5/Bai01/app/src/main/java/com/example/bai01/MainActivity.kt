package com.example.bai01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bai01.navigation.AppNavigation
import com.example.bai01.ui.theme.Bai01Theme
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

class MainActivity : ComponentActivity() {

    // 1. Khai báo biến quản lý Firebase Analytics
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Khởi tạo Firebase Analytics
        analytics = Firebase.analytics

        // 3. Ghi lại sự kiện khởi động ứng dụng đầu tiên để kiểm tra
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN) {
            param("screen_source", "main_activity_created")
        }

        setContent {
            Bai01Theme {
                // 4. Gọi AppNavigation (có thể truyền analytics vào nếu cần log bên trong các màn hình)
                AppNavigation()
            }
        }
    }
}