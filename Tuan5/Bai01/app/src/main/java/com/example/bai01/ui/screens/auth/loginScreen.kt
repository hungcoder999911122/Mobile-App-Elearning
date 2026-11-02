package com.example.bai01.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.bai01.R
import com.example.bai01.ui.screens.onboarding.BeVietnamPro
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.bai01.data.repository.AuthRepository
import com.example.bai01.data.repository.FirebaseAuthRepository
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onGuestLogin: () -> Unit = {}
)
{
    // Lấy Context để hiển thị Toast thông báo
    val context = LocalContext.current

    // Khởi tạo AuthRepository (OOP)
    val authRepository: AuthRepository = remember { FirebaseAuthRepository() }
    // Biến trạng thái để xoay vòng tròn chờ nếu muốn
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()    // Tự động tạo khoảng lùi an toàn né Status Bar và Navigation Bar
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ){

//        1. NÚT QUAY LẠI
        Spacer(modifier = Modifier.height(24.dp))
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.offset(x= (-12).dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

//        2. Logo
        Spacer(modifier = Modifier.weight(0.8f))
        Image(
            painter = painterResource(id = R.drawable.ic_logo_uth1),
            contentDescription = "Logo",
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(20.dp),  // Hình dáng của cái bóng
                    spotColor = Color(0xFF3B71F3),             // Bóng màu xanh
                    ambientColor = Color(0xFF3B71F3)
                )
                .clip(RoundedCornerShape(20.dp))  // Bo góc ảnh, khớp với bóng
        )

//        3. Tiêu đề
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Chào mừng đến với UTH Todo",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = BeVietnamPro,
            color = Color.Black,
            lineHeight = 38.sp
        )

//        4. Mô tả
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.",
            fontSize = 16.sp,
            fontFamily = BeVietnamPro,
            color = Color.Gray,
            lineHeight = 24.sp
        )

//        5. Danh sách tính năng
        Spacer(modifier = Modifier.height (24.dp))
        FeatureItem(
            icon = Icons.Default.Sync,
            text = "Tự động sao lưu, không lo mất dữ liệu"
        )

        Spacer(modifier = Modifier.height(16.dp))
        FeatureItem(
            icon = Icons.Default.PhoneIphone,
            text = "Dùng trên iPhone, iPad và Mac"
        )

//        6. Nút Login
        Spacer(modifier = Modifier.weight(0.6f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Nút Google
            OutlinedButton(
                enabled = !isLoading,
                onClick = {
                    // KIỂM TRA MẠNG: Nếu mất mạng / chế độ máy bay -> BÁO LỖI NGAY
                    if (!isNetworkAvailable(context)) {
                        Toast.makeText(context, "Đăng nhập thất bại: Không có kết nối mạng!", Toast.LENGTH_LONG).show()
                        return@OutlinedButton
                    }
                    // Nếu có mạng:
                    Toast.makeText(context, "Đăng nhập Google thành công! Chào Minh Anh", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                // Thay thế bằng Image logo Google chuẩn
                Text(text = "G", fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(end = 8.dp))
                Text(
                    text = "Tiếp tục với Google",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text(
                    text = "hoặc",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                enabled = !isLoading,
                onClick = {
                    // 1. KIỂM TRA MẠNG: Nếu bật chế độ máy bay -> BÁO LỖI VÀ CHẶN NGAY
                    if (!isNetworkAvailable(context)) {
                        Toast.makeText(context, "Đăng nhập thất bại: Không có kết nối mạng!", Toast.LENGTH_LONG).show()
                        return@TextButton
                    }
                    isLoading = true
                    authRepository.signInAnonymously(
                        onSuccess = { userProfile ->
                            isLoading = false
                            Toast.makeText(
                                context,
                                "Đăng nhập thành công! Chào ${userProfile.fullName}",
                                Toast.LENGTH_SHORT
                            ).show()
                            onGuestLogin()
                        },
                        onError = { errorMessage ->
                            isLoading = false
                            // BÁO LỖI VÀ TUYỆT ĐỐI KHÔNG GỌI onGuestLogin()
                            Toast.makeText(
                                context,
                                "Đăng nhập thất bại: $errorMessage",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            ) {
                Text(
                    text = if (isLoading) "Đang xử lý..." else "Dùng thử không cần tài khoản",
                    color = Color(0xFF3B71F3),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Điều khoản sử dụng (AnnotatedString)
            TermsAndPrivacyText()
        }
    }
}

@Composable
fun FeatureItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF3B71F3),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 14.sp, color = Color.DarkGray)
    }
}

@Composable
fun TermsAndPrivacyText() {
    val annotatedString = buildAnnotatedString {
        append("Khi tiếp tục, bạn đồng ý với ")
        withStyle(style = SpanStyle(
            textDecoration = TextDecoration.Underline,
            color = Color.DarkGray
        )
        ) {
            append("Điều khoản sử dụng")
        }
        append(" và ")
        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, color = Color.DarkGray)) {
            append("Chính\nsách quyền riêng tư")
        }
        append(" của UTH Todo.")
    }

    Text(
        text = annotatedString,
        fontSize = 12.sp,
        color = Color.Gray,
        textAlign = TextAlign.Center,
        lineHeight = 18.sp,
        modifier = Modifier.fillMaxWidth()
    )
}
// Hàm kiểm tra thiết bị có đang kết nối mạng Internet hay không
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
