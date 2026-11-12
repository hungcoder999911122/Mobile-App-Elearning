// Service tiếp nhận thông báo
package com.example.bai02.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.bai02.MainActivity
import com.example.bai02.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Hàm này tự động kích hoạt khi thiết bị được cấp một Token mới từ Firebase
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Mã Token FCM của máy bạn: $token")
    }

    /**
     * Hàm này chạy khi có thông báo đẩy gửi về máy (kể cả khi app đang mở trên màn hình)
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Lấy tiêu đề và nội dung thông báo
        val title = remoteMessage.notification?.title ?: "UTH Todo"
        val body = remoteMessage.notification?.body ?: "Bạn có công việc mới cần hoàn thành!"

        // Hiển thị thông báo lên thanh trạng thái
        showNotification(title, body)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "uth_todo_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo Notification Channel (bắt buộc từ Android 8.0 trở lên)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nhắc nhở công việc",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh thông báo nhắc việc của UTH Todo"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Tạo hành động khi người dùng nhấn vào thông báo thì mở lại MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Xây dựng thông báo
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_logo_uth1) // Icon nhỏ hiển thị trên thanh trạng thái
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}