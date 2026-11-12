package com.example.bai02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bai02.navigation.AppNavigation
import com.example.bai02.ui.theme.Bai01Theme
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

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

        // 4. XIN QUYỀN GỬI THÔNG BÁO TỪ NGƯỜI DÙNG (FCM)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Kiểm tra xem người dùng đã từng cấp quyền này chưa
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            // Nếu chưa có quyền, bật popup hỏi người dùng
            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101 // Mã yêu cầu (Request Code) bất kỳ
                )
            }
        }

        //  5. LẤY MÃ "FCM TOKEN" ĐỂ PHỤC VỤ VIỆC TEST GỬI THÔNG BÁO
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Lấy được mã Token thành công
                val fcmToken = task.result
                Log.d("FCM_DEBUG", "==================================================")
                Log.d("FCM_DEBUG", "MÃ TOKEN FCM CỦA BẠN LÀ:")
                Log.d("FCM_DEBUG", fcmToken)
                Log.d("FCM_DEBUG", "==================================================")
            } else {
                // Nếu thất bại (thường do không có kết nối mạng)
                Log.e("FCM_DEBUG", "Lỗi không lấy được Token FCM: ${task.exception?.message}")
            }
        }
        // Khởi động dịch vụ Firebase Remote Config
        com.example.bai02.data.repository.RemoteConfigManager.init()

        // 6. Chạy giao diện Compose
        setContent {
            Bai01Theme {
                // 4. Gọi AppNavigation (có thể truyền analytics vào nếu cần log bên trong các màn hình)
                AppNavigation()
            }
        }
    }
}