package com.example.thuchanh3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thuchanh3.ui.theme.ThucHanh3Theme

//class MainActivity: Activity chính của ứng dụng.
//onCreate(): nơi Android khởi tạo màn hình.
//setContent { ... }: nơi bắt đầu khai báo giao diện Compose.

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThucHanh3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

//Đánh dấu nơi có thể tạo giao diện compose
@Composable
fun Greeting( modifier: Modifier = Modifier) {
//    Khai báo biến number 1, number 2
    var number1 by remember { mutableStateOf("") }
    var number2 by remember { mutableStateOf("") }

//      Khai báo state lưu phép tính đang chọn (để tạo viền active như hình bên phải)
    var selectedOperator by remember { mutableStateOf("+") }

//      Danh sách định nghĩa 4 nút: ký tự và mã màu tương ứng trong mẫu
    val operators = listOf(
        "+" to Color(0xFFDE5246), // Đỏ
        "-" to Color(0xFFEAA646), // Vàng cam
        "*" to Color(0xFF5C52E5), // Tím
        "/" to Color(0xFF1E1E1E)  // Đen
    )

    Column(
//          Các thành phần con được căn giữa theo chiều ngang.
        horizontalAlignment = Alignment.CenterHorizontally,
//          Nhóm thành phần con nằm trên theo chiều dọc trong vùng.
        verticalArrangement = Arrangement.Top,
//          horizontal: Đệm trái và phải, vertical: Đệm trên và dưới
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Spacer(modifier = Modifier.height( 150.dp))
        Text(
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            text = "Thực Hành 03"
        )

        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(
            value = number1,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            onValueChange = { number1 = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Nhập số thứ nhất") }
        )

        Spacer(modifier = Modifier.height(25.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // Khoảng cách giữa các nút
        ) {
            operators.forEach { (op, color) ->
                val isSelected = selectedOperator == op

                Button(
                    onClick = { selectedOperator = op },
                    modifier = Modifier.size(65.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp), // Triệt tiêu padding thừa
                    // Viền đen khi nút được chọn (như ảnh bên phải)
                    border = if (isSelected) BorderStroke(2.dp, Color.Black) else null,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = op,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))
        OutlinedTextField(
            value = number2,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            onValueChange = { number2= it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Nhập số thứ hai") }
        )

        val n1 = number1.toDoubleOrNull()
        val n2 = number2.toDoubleOrNull()
        val resultNumber: Double? = if (n1 != null && n2 != null) {
            when (selectedOperator) {
                "+" -> n1 + n2
                "-" -> n1 - n2
                "*" -> n1 * n2
                "/" -> if (n2 != 0.0) n1 / n2 else null
                else -> null
            }
        } else {
            null
        }
        val resultText = when {
            // Trường hợp phép chia cho 0
            selectedOperator == "/" && n2 == 0.0 -> "Không thể chia cho 0"
            // Trường hợp tính toán thành công
            resultNumber != null -> {
                if (resultNumber % 1.0 == 0.0) {
                    resultNumber.toLong().toString()
                } else {
                    resultNumber.toString()
                }
            }
            // Trường hợp chưa nhập đủ cả 2 số
            else -> ""
        }

        Spacer(modifier = Modifier.height(50.dp))
        Text(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start),
            text = "Kết quả: $resultText"
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ThucHanh3Theme {
        Greeting()
    }
}