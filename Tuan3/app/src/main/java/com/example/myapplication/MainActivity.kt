package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun WelcomeScreen(
    onNavigateToList: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_compose_logo1),
                contentDescription = "Compose Logo",
                modifier = Modifier
                    .size(140.dp)
                    .padding(bottom = 32.dp)
            )

            Text(
                text = "Jetpack Compose",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            Text(
                text = "Jetpack Compose is a modern UI toolkit for building native Android applications." +
                        "using a declarative programming approach.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        Button(
            onClick = onNavigateToList,
            modifier = Modifier
                .width(220.dp)
                .height(50.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text(
                text = "I'm Ready",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        WelcomeScreen(onNavigateToList = {})
    }
}

// Màn hình 2
@Composable
fun ComponentCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Cho phép bấm vào thẻ
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD4E7FE)) // Màu xanh nhạt giống ảnh
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color.DarkGray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentsListScreen(
    onNavigate: (String) -> Unit // Nhận tên route cần chuyển đến
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "UI Components List",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Nhóm 1: Display
            item {
                Text("Display", color = Color.Gray, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            item {
                // 1. Chuyển sang màn Text Detail
                ComponentCard(
                    title = "Text",
                    subtitle = "Displays text",
                    onClick = { onNavigate("detail_screen") }
                )
            }
            item {
                ComponentCard(title = "Image", subtitle = "Displays an image")
            }

            // Nhóm 2: Input
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Input", color = Color.Gray, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            item {
                // 2. GẮN SỰ KIỆN CLICK: Chuyển sang màn TextFieldScreen
                ComponentCard(
                    title = "TextField",
                    subtitle = "Input Field for text",
                    onClick = { onNavigate("textfield_detail") }
                )
            }
            item {
                ComponentCard(title = "PasswordField", subtitle = "Input Field for passwords")
            }

            // Nhóm 3: Layout
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Layout", color = Color.Gray, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            item {
                ComponentCard(title = "Column", subtitle = "Arranges elements vertically")
            }
            item {
                // 3. GẮN SỰ KIỆN CLICK: Chuyển sang màn RowLayoutScreen
                ComponentCard(
                    title = "Row",
                    subtitle = "Arranges elements horizontally",
                    onClick = { onNavigate("row_layout_detail") }
                )
            }
        }
    }
}

//Màn hình 3
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextDetailScreen(
    onBackClick: () -> Unit // Callback để quay lại màn hình trước
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Text Detail",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Tạo một khoảng trống vô hình bên phải để cân bằng với nút Back bên trái, giữ tiêu đề ở chính giữa
                    Spacer(modifier = Modifier.width(48.dp))
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center // Đưa toàn bộ đoạn văn vào chính giữa màn hình
        ) {
            // Định dạng từng từ theo đúng mẫu trên ảnh
            val annotatedText = buildAnnotatedString {
                append("The ")

                // quick: gạch ngang
                withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                    append("quick ")
                }

                // Brown: màu nâu, in đậm, to hơn
                withStyle(style = SpanStyle(color = Color(0xFFB07238), fontWeight = FontWeight.Bold, fontSize = 28.sp)) {
                    append("Brown\n")
                }

                append("fox ")

                // jumps: giãn cách ký tự
                withStyle(style = SpanStyle(letterSpacing = 3.sp)) {
                    append("jumps ")
                }

                // over: in đậm + nghiêng
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                    append("over\n")
                }

                append("the ")

                // lazy: gạch chân + in nghiêng
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, fontStyle = FontStyle.Italic)) {
                    append("lazy")
                }

                append(" dog.")
            }

            Text(
                text = annotatedText,
                fontSize = 22.sp,
                lineHeight = 34.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldScreen(onBackClick: () -> Unit) {
    // State lưu trữ văn bản người dùng gõ
    var textInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "TextField",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2196F3)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) } // Cân bằng nút Back để tiêu đề luôn ở giữa
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Căn cụm nhập liệu vào giữa màn hình theo chiều dọc
        ) {
            // Ô nhập bo tròn góc
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = {
                    Text(
                        text = "Thông tin nhập",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp), // Bo tròn mềm mại theo mẫu
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Text tự động cập nhật theo giá trị ô nhập
            Text(
                text = if (textInput.isEmpty()) "Tự động cập nhật dữ liệu theo textfield" else textInput,
                fontSize = 13.sp,
                color = if (textInput.isEmpty()) Color(0xFFD32F2F) else Color.Black, // Màu đỏ cam khi chưa nhập
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowLayoutScreen(onBackClick: () -> Unit) {
    // Bảng màu chuẩn theo mẫu: [Xanh nhạt, Xanh đậm, Xanh nhạt]
    val lightBlue = Color(0xFFB5D3FD)
    val darkBlue = Color(0xFF387BEE)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Row Layout",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2196F3)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Khoảng cách giữa 4 hàng
        ) {
            // Lặp tạo 4 hàng giống nhau theo mẫu
            repeat(4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp) // Khoảng cách giữa 3 khối trong 1 hàng
                ) {
                    // Khối 1 (Trái): Xanh nhạt
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(lightBlue)
                    )

                    // Khối 2 (Giữa): Xanh đậm
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(darkBlue)
                    )

                    // Khối 3 (Phải): Xanh nhạt
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(lightBlue)
                    )
                }
            }
        }
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome_screen"
    ) {
        // Màn hình 1: Welcome
        composable("welcome_screen") {
            WelcomeScreen(
                onNavigateToList = {
                    navController.navigate("list_screen")
                }
            )
        }

        // Màn hình 2: List (SỬA ĐOẠN NÀY)
        composable("list_screen") {
            ComponentsListScreen(
                onNavigate = { routeName ->
                    // Nhận route ("detail_screen", "textfield_detail" hoặc "row_layout_detail") và chuyển trang
                    navController.navigate(routeName)
                }
            )
        }

        // Màn hình 3: Text Detail
        composable("detail_screen") {
            TextDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Màn hình 4: TextField
        composable("textfield_detail") {
            TextFieldScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Màn hình 5: Row Layout
        composable("row_layout_detail") {
            RowLayoutScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}