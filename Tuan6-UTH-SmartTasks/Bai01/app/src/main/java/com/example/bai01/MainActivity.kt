package com.example.bai01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bai01.data.model.Product
import com.example.bai01.ui.ProductUiState
import com.example.bai01.ui.ProductViewModel
import com.example.bai01.ui.theme.Bai01Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Bai01Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProductDetailApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ProductDetailApp(
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (val state = uiState) {
            is ProductUiState.Loading -> {
                // Trạng thái đang tải dữ liệu từ API
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF0288D1))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Đang tải dữ liệu sản phẩm từ API...",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
            is ProductUiState.Error -> {
                // Trạng thái lỗi mạng / không gọi được API
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.message,
                        color = Color(0xFFD32F2F),
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchProduct() }) {
                        Text(text = "Thử lại (Retry)")
                    }
                }
            }
            is ProductUiState.Success -> {
                // Trạng thái lấy dữ liệu thành công -> Hiển thị màn hình chi tiết
                ProductDetailContent(product = state.product)
            }
        }
    }
}

@Composable
fun ProductDetailContent(
    product: Product,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- PHẦN 1: HEADER (Nút Back & Tiêu đề Product detail) ---
        ProductDetailHeader()

        Spacer(modifier = Modifier.height(16.dp))

        // --- PHẦN 2: ẢNH SẢN PHẨM (Coil AsyncImage) ---
        ProductImageCard(imageUrl = product.image)

        Spacer(modifier = Modifier.height(20.dp))

        // --- PHẦN 3: TÊN VÀ GIÁ SẢN PHẨM ---
        Text(
            text = product.getDisplayName(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Giá: ${product.getFormattedPrice()}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD32F2F) // Màu đỏ nổi bật
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- PHẦN 4: KHUNG MÔ TẢ CHI TIẾT ---
        ProductDescriptionBox(description = product.getDisplayDescription())

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProductDetailHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Nút Back tròn xanh nhạt ở góc trái
        IconButton(
            onClick = { /* Xử lý sự kiện quay lại */ },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(36.dp)
                .background(color = Color(0xFF4FC3F7), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Tiêu đề căn giữa
        Text(
            text = "Product detail",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0288D1)
        )
    }
}

@Composable
fun ProductImageCard(imageUrl: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE65100)) // Màu cam nền nổi bật
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (!imageUrl.isNullOrBlank()) {
                // Sử dụng Coil để load ảnh từ URL mạng
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Product Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = "👟 [Hình ảnh giày]",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ProductDescriptionBox(description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7F7F7))
            .padding(16.dp)
    ) {
        Text(
            text = description,
            fontSize = 12.sp,
            color = Color(0xFF555555),
            lineHeight = 18.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductDetailPreview() {
    Bai01Theme {
        ProductDetailContent(
            product = Product(
                id = 1,
                name = "Giày Nike Nam Nữ Chính Hãng - Nike Air Force 1 '07 LV8 - Màu Trắng | JapanSport HF2898-100",
                price = "4.000.000đ",
                description = "Với giày chạy bộ, từng gram đều quan trọng. Đó là lý do tại sao đế giữa LIGHTSTRIKE PRO mới nhẹ hơn so với phiên bản trước. Một foam đế giữa siêu nhẹ và thoải mái này có lớp đệm đàn hồi được thiết kế để hạn chế tiêu hao năng lượng. Trong các mẫu giày tập luyện, công nghệ này được thiết kế nhằm hỗ trợ cơ bắp của vận động viên để họ có thể phục hồi nhanh hơn giữa các cuộc đua."
            )
        )
    }
}
