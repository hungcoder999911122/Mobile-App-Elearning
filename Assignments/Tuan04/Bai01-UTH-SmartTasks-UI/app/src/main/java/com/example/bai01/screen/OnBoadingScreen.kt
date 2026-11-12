package com.example.bai01.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.example.bai01.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Cấu trúc dữ liệu cho các trang OnBoardingScreen
data class OnboardingPage(
    val id: Int,
    val title: String,
    val description: String,
    val image: Int,
    val buttonText: String
)


    @Composable
    fun OnboardingContent(
        page: OnboardingPage,
        onSkipClicked: () -> Unit,
        onNextClicked: () -> Unit
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

    //      Căn chỉnh giữa các thành phần con theo chiều ngang (trái, phải)
            horizontalAlignment = Alignment.CenterHorizontally
        ){
    //        HEADER (Logo + Tên ứng dụng + Nút bỏ qua)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp ,vertical = 16.dp),

    //          Phân bổ từ đầu tiên sát mép trái và từ cuối sát mép phải, giữa các thành phần con
                horizontalArrangement =  Arrangement.SpaceBetween,

    //          Căn chỉnh tất cả thành phần con nằm trong Row theo chiều dọc (trên, dưới)
                verticalAlignment = Alignment.CenterVertically
            ){

    //          Cụm Logo và Tên ứng dụng bên trái
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.wrapContentWidth()
                ){
                    Image(
                        painter =  painterResource(id = R.drawable.ic_logo_uth1),
                        contentDescription = "Mini Logo",
                        modifier = Modifier
                            .size(36.dp)

    //                       logo có khoảng trắng thừa bên phải, dùng offset âm để kéo chữ lại gần hơn
                            .offset(x= (-4).dp)
                    )

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E1E1E)
                                )
                            ){
                                append("UTH")
                            }

                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF2F5BEA)
                                )
                            ){
                                append(" Todo")
                            }
                        },
                        fontSize = 20.sp,
                        fontFamily = BeVietnamPro,
                        letterSpacing = (-0.5).sp,                // khoảng cách chữ
                    )
                }

                TextButton(onClick = onSkipClicked){
                    Text(
                        text = "Bỏ qua",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontFamily = BeVietnamPro
                    )
                }
            }

    //       HÌNH ẢNH MINH HOẠ
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = page.image),
                contentDescription = page.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset(y= (-2).dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

    //       Tiêu đề & Mô tả
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ){
                Text(
                    text = page.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BeVietnamPro,
                    color = Color(0xFF1E1E1E),
                    lineHeight = 30.sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = page.description,
                    fontSize = 15.sp,
                    fontFamily = BeVietnamPro,
                    color = Color.Gray,
                    lineHeight = 22.sp,
                )

                Spacer (modifier = Modifier.height(16.dp))

                // 4. INDICATOR (Các chấm phân trang)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    // 4. INDICATOR (Các chấm phân trang)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        listOf(1, 2, 3).forEach { index ->
                            val isActive = (page.id == index)
                            Box(
                                modifier = Modifier
                                    .width(if (isActive) 24.dp else 8.dp)
                                    .height(8.dp)
                                    .background(
                                        color = if (isActive) Color(0xFF2F5BEA) else Color.LightGray,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNextClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F5BEA)
                )
            ){
                Text(
                    text = page.buttonText,
                    fontSize = 16.sp,
                    fontFamily = BeVietnamPro,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.padding(bottom= 24.dp))
        }
    }

    // 2. Hàm Preview riêng (Truyền dữ liệu giả để xem trước giao diện)
    @Preview(showBackground = true)
    @Composable
    fun OnboardingContentPreview() {
        OnboardingContent(
            page = OnboardingPage(
                id = 1,
                title = "Mọi việc, gọn trong một chỗ",
                description = "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào.",
                image = R.drawable.ic_container,
                buttonText = "Tiếp tục"
            ),
            onSkipClicked = {},
            onNextClicked = {}
        )
    }