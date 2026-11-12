package com.example.btvn_th3_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.btvn_th3_2.ui.theme.BTVNTH32Theme




fun xepLoaiHocLuc(diem: Double): String {
    return if (diem < 0 || diem > 10) {
        "Không hợp lệ" // Phải kiểm tra trước tiên
    } else if (diem >= 8.0) {
        "Giỏi"
    } else if (diem >= 6.5) {
        "Khá"
    } else if (diem >= 5.0) {
        "Trung bình"
    } else {
        "Yếu"
    }
}


fun layMauChu(xepLoai: String): Color {
    return if (xepLoai == "Giỏi") {
        Color(0xFF2E7D32) // Xanh lá đậm
    } else if (xepLoai == "Khá") {
        Color(0xFF1976D2) // Xanh dương
    } else if (xepLoai == "Trung bình") {
        Color(0xFFED6C02) // Màu cam
    } else if (xepLoai == "Yếu") {
        Color(0xFFD32F2F) // Màu đỏ
    } else {
        Color(0xFF757575) // Màu xám (Không hợp lệ)
    }
}


fun layMauNen(xepLoai: String): Color {
    return if (xepLoai == "Giỏi") {
        Color(0xFFE8F5E9) // Xanh lá nhạt
    } else if (xepLoai == "Khá") {
        Color(0xFFE3F2FD) // Xanh dương nhạt
    } else if (xepLoai == "Trung bình") {
        Color(0xFFFFF3E0) // Cam nhạt
    } else if (xepLoai == "Yếu") {
        Color(0xFFFFEBEE) // Đỏ nhạt
    } else {
        Color(0xFFEEEEEE) // Xám nhạt
    }
}

// Định dạng số hiển thị (ví dụ: 9.0 hiển thị là 9, 5.5 giữ nguyên 5.5)
fun dinhDangDiem(diem: Double): String {
    return if (diem % 1.0 == 0.0) {
        diem.toLong().toString()
    } else {
        diem.toString()
    }
}

// =====================================================================
// ACTIVITY CHÍNH CỦA ANDROID
// =====================================================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XepLoaiScreen()
        }
    }
}

// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XepLoaiScreen() {
    // YÊU CẦU 1: Khai báo biến điểm có giá trị 8.5
    val diemTrungBinh = 8.5
    val xepLoaiChinh = xepLoaiHocLuc(diemTrungBinh)

    // YÊU CẦU 4: Danh sách các điểm cần "Kiểm tra thêm"
    val danhSachKiemTra = listOf(9.0, 7.0, 5.5, 3.0, 11.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Xếp loại học lực",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A8A) // Màu xanh tím đậm như mẫu
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F4F6)) // Nền xám nhạt toàn màn hình
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // -------------------------------------------------------------
            // KHỐI 1: HIỂN THỊ ĐIỂM TRUNG BÌNH CHÍNH (8.5)
            // -------------------------------------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Điểm trung bình",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = dinhDangDiem(diemTrungBinh),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1E1E)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Khối ô xếp loại hình chữ nhật bo góc
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = layMauNen(xepLoaiChinh),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = xepLoaiChinh,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = layMauChu(xepLoaiChinh)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tiêu đề nhỏ: KIỂM TRA THÊM
            Text(
                text = "KIỂM TRA THÊM",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // -------------------------------------------------------------
            // KHỐI 2: DANH SÁCH KIỂM TRA THÊM (9, 7, 5.5, 3, 11)
            // -------------------------------------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    danhSachKiemTra.forEachIndexed { index, diem ->
                        val xepLoai = xepLoaiHocLuc(diem)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Điểm ${dinhDangDiem(diem)}",
                                fontSize = 14.sp,
                                color = Color(0xFF1E1E1E),
                                fontWeight = FontWeight.Medium
                            )

                            // Nhãn xếp loại bo góc nhỏ bên phải
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = layMauNen(xepLoai),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = xepLoai,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = layMauChu(xepLoai)
                                )
                            }
                        }

                        // Vẽ đường kẻ phân cách mảnh giữa các dòng (trừ dòng cuối)
                        if (index < danhSachKiemTra.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.5.dp)
                                    .background(Color(0xFFEEEEEE))
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun XepLoaiPreview() {
    XepLoaiScreen()
}