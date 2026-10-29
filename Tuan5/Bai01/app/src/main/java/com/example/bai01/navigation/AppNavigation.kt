package com.example.bai01.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.bai01.R
import com.example.bai01.screen.OnboardingContent
import com.example.bai01.screen.OnboardingPage
import com.example.bai01.screen.StartScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val onboardingPages = listOf(
    OnboardingPage(
        id = 1,
        title = "Mọi việc, gọn trong một chỗ",
        description = "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào.",
        image = R.drawable.ic_container, // Ảnh màn hình 2
        buttonText = "Tiếp tục"
    ),
    OnboardingPage(
        id = 2,
        title = "Chia theo Học tập và Cuộc sống",
        description = "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần làm gì.",
        image = R.drawable.ic_container_1, // Ảnh màn hình 3 (bạn thay bằng ảnh thực tế)
        buttonText = "Tiếp tục"
    ),
    OnboardingPage(
        id = 3,
        title = "Nhắc đúng lúc",
        description = "Bật thông báo để UTH Todo nhắc bạn trước hạn chót.",
        image = R.drawable.ic_container_2, // Ảnh màn hình 4 (bạn thay bằng ảnh thực tế)
        buttonText = "Bắt đầu ngay" // Riêng màn hình cuối đổi chữ nút bấm
    )
)
// 2. Hàm điều hướng chính của ứng dụng
@Composable
fun AppNavigation() {
    // Biến trạng thái quản lý: true = đang hiện StartScreen, false = chuyển sang Onboarding
    var showStartScreen by remember { mutableStateOf(true) }

    // Hiệu ứng đếm ngược 3 giây
    LaunchedEffect(Unit) {
        delay(5000L) // Chờ 3 giây (3000 mili-giây)
        showStartScreen = false // Đổi trạng thái để chuyển màn hình
    }

    if (showStartScreen) {
        // Hiển thị màn hình 1 (StartScreen) trong 3 giây đầu
        StartScreen()
    } else {
        // Sau 3 giây tự động chuyển sang chuỗi màn hình Onboarding
        MainOnboardingScreen(
            onFinished = {
                // Xử lý khi hoàn tất onboarding hoặc bấm bỏ qua
                // (Sau này bạn sẽ viết lệnh chuyển sang màn hình chính/Home ở đây)
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainOnboardingScreen(
    onFinished: () -> Unit // Sự kiện khi bấm "Bắt đầu ngay" ở màn cuối
) {
    // Quản lý trạng thái trang hiện tại (tổng cộng 3 trang)
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { position ->
        val currentPageData = onboardingPages[position]

        // TÁI SỬ DỤNG HÀM ONBOARDING CONTENT CHO TỪNG TRANG
        OnboardingContent(
            page = currentPageData,
            onSkipClicked = {
                // Bấm bỏ qua thì nhảy thẳng vào màn hình chính của App
                onFinished()
            },
            onNextClicked = {
                if (pagerState.currentPage < onboardingPages.size - 1) {
                    coroutineScope.launch {
                        // === THAY ĐỔI TẠI ĐÂY ĐỂ HIỆU ỨNG MƯỢT HƠN ===
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage + 1,
                            animationSpec = tween(
                                durationMillis = 500, // Thời gian chạy (500 mili-giây)
                                easing = FastOutSlowInEasing // Hiệu ứng quán tính: bắt đầu mượt và giảm tốc từ từ khi gần đến trang mới
                            )
                        )
                    }
                } else {
                    onFinished()
                }
            }
        )
    }
}