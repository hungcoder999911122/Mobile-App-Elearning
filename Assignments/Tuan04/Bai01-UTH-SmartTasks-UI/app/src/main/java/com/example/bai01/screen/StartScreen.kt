package com.example.bai01.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bai01.R

val BeVietnamPro = FontFamily(
    Font(R.font.be_vietnam_pro, FontWeight.Normal),
    Font(R.font.be_vietnam_pro_medium, FontWeight.Medium),
    Font(R.font.be_vietnam_pro_extrabold, FontWeight.ExtraBold)
)
@Preview(showBackground = true)
@Composable
fun StartScreen() {

//  Giao diện nền xanh toàn màn hình
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F5BEA)),
        contentAlignment = Alignment.Center
    ){
//      1. Cột chứa logo, Tên ứng dụng và Solgan ở giữa
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

//          1.1 Ảnh logo
            Image(
                painter = painterResource(id = R.drawable.ic_logo_uth),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(150.dp)
            )

            Spacer(modifier = Modifier.height(1.dp))

//          1.2 Tên ứng dụng (Mix font voi nhau)
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.ExtraBold)
                    ){
                        append("UTH")
                    }

                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.Medium)
                    ){
                        append(" Todo")
                    }
                },
                color = Color.White,
                fontSize = 40.sp,
                fontFamily = BeVietnamPro,
                lineHeight = 48.sp,
                letterSpacing = (-1).sp,                // khoảng cách chữ
                modifier = Modifier
                    .width(190.dp)                      //dài rộng của chữ
                    .offset(y = (-16).dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

//          1.3 Dòng slogan phía dưới
            Text(
                text = "Việc học, việc nhà - gọn gàng mỗi ngày",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }

//          2. Thanh tiến độ và phiên bản ứng dụng
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)          // Căn dưới màn hình
                    .padding(bottom = 50.dp),               // khoảng cách đáy màn hình
                horizontalAlignment = Alignment.CenterHorizontally  // căn giữa
            ){

//          2.1 Thanh tiến độ chạy liên tục
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(80.dp)       // Độ dài cố định của thanh nền giống Figma
                        .height(4.dp),      // Độ dày của thanh
                    color = Color.White,    // Màu trắng của thỏi màu chạy luân hồi
                    trackColor = Color.White.copy(alpha = 0.3f), // Thanh nền mờ đứng yên phía sau
                    strokeCap = StrokeCap.Round     // Bo tròn hai đầu thỏi trắng và thanh nền
                )
                Spacer(modifier = Modifier.height(16.dp))

//          2.2 Phiên bản ứng dụng
                Text(
                    text = "Phiên bản 2.1.0",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontFamily = BeVietnamPro,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
