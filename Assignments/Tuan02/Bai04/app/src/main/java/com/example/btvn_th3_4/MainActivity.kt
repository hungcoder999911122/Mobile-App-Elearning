package com.example.btvn_th3_4

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
import com.example.btvn_th3_4.ui.theme.BTVNTH34Theme
import java.util.Locale


data class Student(
    val name: String,
    val score: Double? // Dấu ? cho phép nhận giá trị null (chưa có điểm)
)


// CÁC HÀM XỬ LÝ LOGIC (IF / ELSE)

fun xepLoaiHocLuc(diem: Double): String {
    return if (diem < 0 || diem > 10) {
        "Không hợp lệ"
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

// Hàm gán màu chữ tương ứng với từng xếp loại
fun layMauChuXepLoai(xepLoai: String): Color {
    return if (xepLoai == "Giỏi") {
        Color(0xFF2E7D32) // Xanh lá
    } else if (xepLoai == "Khá") {
        Color(0xFF1976D2) // Xanh dương
    } else if (xepLoai == "Trung bình") {
        Color(0xFFED6C02) // Cam
    } else if (xepLoai == "Yếu") {
        Color(0xFFD32F2F) // Đỏ
    } else {
        Color(0xFF757575) // Xám (cho "Chưa có điểm")
    }
}


// ACTIVITY CHÍNH CỦA ANDROID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BangDiemScreen()
        }
    }
}


// GIAO DIỆN CHÍNH (COMPOSE UI)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BangDiemScreen() {

    val students = listOf(
        Student("Nguyễn Văn An", 9.0),
        Student("Trần Thị Bình", 7.2),
        Student("Lê Minh Chi", null),
        Student("Phạm Quốc Dũng", 4.5),
        Student("Võ Thanh Em", 5.8)
    )


    var tongDiem = 0.0
    var soLuongCoDiem = 0
    var soSinhVienGioi = 0
    var soSinhVienChuaCoDiem = 0
    var diemCaoNhat = 0.0

    for (student in students) {
        if (student.score != null) {
            tongDiem += student.score
            soLuongCoDiem++

            // Kiểm tra điểm cao nhất
            if (student.score > diemCaoNhat) {
                diemCaoNhat = student.score
            }

            // Kiểm tra số sinh viên Giỏi (>= 8.0)
            if (xepLoaiHocLuc(student.score) == "Giỏi") {
                soSinhVienGioi++
            }
        } else {
            // Trường hợp score == null
            soSinhVienChuaCoDiem++
        }
    }

    // Điểm trung bình (bỏ qua người chưa có điểm)
    val diemTrungBinh = if (soLuongCoDiem > 0) tongDiem / soLuongCoDiem else 0.0
    val diemTrungBinhStr = String.format(Locale.US, "%.2f", diemTrungBinh)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bảng điểm lớp",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A8A)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F4F6))
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // THẺ XANH TRÊN CÙNG: BẢNG THỐNG KÊ TỔNG QUAN

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF24448E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Điểm trung bình lớp",
                        color = Color(0xFFC7D2FE),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = diemTrungBinhStr,
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Đường kẻ ngang phân cách mờ
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(Color(0x40FFFFFF))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 cột thông số: Giỏi, Chưa có điểm, Cao nhất
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Giỏi", color = Color(0xFFC7D2FE), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$soSinhVienGioi", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }

                        Column {
                            Text("Chưa có điểm", color = Color(0xFFC7D2FE), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$soSinhVienChuaCoDiem", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }

                        Column {
                            Text("Cao nhất", color = Color(0xFFC7D2FE), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format(Locale.US, "%.1f", diemCaoNhat),
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tiêu đề phụ danh sách
            Text(
                text = "DANH SÁCH - ${students.size} SINH VIÊN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(10.dp))


            // THẺ TRẮNG BÊN DƯỚI: DANH SÁCH SINH VIÊN (DÙNG VÒNG LẶP FOR)
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
                    students.forEachIndexed { index, student ->
                        // Xác định hiển thị xếp loại & điểm khi score có thể là null
                        val xepLoai = if (student.score != null) {
                            xepLoaiHocLuc(student.score)
                        } else {
                            "Chưa có điểm"
                        }

                        val diemHienThi = if (student.score != null) {
                            String.format(Locale.US, "%.1f", student.score)
                        } else {
                            "—"
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cột trái: Tên sinh viên và xếp loại
                            Column {
                                Text(
                                    text = student.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E1E)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = xepLoai,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = layMauChuXepLoai(xepLoai)
                                )
                            }

                            // Cột phải: Điểm số
                            Text(
                                text = diemHienThi,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (student.score != null) Color(0xFF1E1E1E) else Color.Gray
                            )
                        }

                        // Đường kẻ phân cách giữa các dòng
                        if (index < students.size - 1) {
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
fun BangDiemPreview() {
    BangDiemScreen()
}