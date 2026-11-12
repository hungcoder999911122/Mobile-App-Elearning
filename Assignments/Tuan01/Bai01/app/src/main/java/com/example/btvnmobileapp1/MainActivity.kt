package com.example.btvnmobileapp1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                ProfileScreen()
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. Thanh điều hướng trên cùng (Top Bar) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút Back hình bo góc có viền nhẹ
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Nút Edit hình bo góc có viền nhẹ
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit Profile",
                    tint = Color(0xFF00C853), 
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(140.dp))

        // --- 2. Cụm ảnh đại diện hình tròn ---
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F1FC)) 
        ) {
            Image(
                painter = painterResource(id = R.drawable.anhdaidien),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 3. Họ và tên ---
        Text(
            text = "Nguyễn Minh Anh", 
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // --- 4. Mã số sinh viên ---
        Text(
            text = "SV0123456",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF9E9E9E)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
