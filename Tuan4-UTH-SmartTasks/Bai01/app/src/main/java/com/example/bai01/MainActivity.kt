package com.example.bai01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bai01.navigation.AppNavigation
import com.example.bai01.ui.theme.Bai01Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Bai01Theme {
                // 2. Gọi AppNavigation làm điểm xuất phát chính của ứng dụng
                AppNavigation()
            }
        }
    }
}