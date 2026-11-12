package com.example.bai02.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.bai02.R
import com.example.bai02.ui.screens.auth.LoginScreen
import com.example.bai02.ui.screens.detail.DetailScreen
import com.example.bai02.ui.screens.home.HomeScreen
import com.example.bai02.ui.screens.home.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bai02.ui.screens.onboarding.OnboardingContent
import com.example.bai02.ui.screens.onboarding.OnboardingPage
import com.example.bai02.ui.screens.onboarding.StartScreen
import com.example.bai02.ui.screens.profile.ProfileScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// =========================================================================
// 1. OOP ROUTE DEFINITION (Sealed Hierarchy)
// =========================================================================

/**
 * Lớp đại diện cho các màn hình trong ứng dụng (OOP - Sealed Hierarchy / Polymorphism).
 * Đóng gói định danh từng màn hình, dễ dàng mở rộng và truyền tham số khi cần.
 */
sealed class AppScreen(val route: String) {
    data object Start : AppScreen("start_screen")
    data object Onboarding : AppScreen("onboarding_screen")
    data object Login : AppScreen("login_screen")
    data object Home : AppScreen("home_screen")
    data object Profile : AppScreen("profile_screen")
    data class Detail(val taskId: String, val fromScreenName: String = "Trang chủ") : AppScreen("detail_screen")
}

// =========================================================================
// 2. OOP NAVIGATION CONTROLLER (State & Backstack Manager)
// =========================================================================

/**
 * Lớp quản lý điều hướng và ngăn xếp màn hình (OOP - Navigation Controller Pattern).
 * Đóng gói hành vi chuyển trang (navigate), quay lại (pop), và xóa stack (clear).
 */
class AppNavigator(initialScreen: AppScreen = AppScreen.Start) {
    // Ngăn xếp màn hình (Backstack) lưu trữ các màn hình đã đi qua
    private val backStack = mutableStateListOf<AppScreen>(initialScreen)

    val currentScreen: AppScreen
        get() = backStack.lastOrNull() ?: AppScreen.Start

    val canGoBack: Boolean
        get() = backStack.size > 1

    /**
     * Chuyển đến màn hình mới và đẩy vào ngăn xếp
     */
    fun navigateTo(screen: AppScreen) {
        if (currentScreen != screen) {
            backStack.add(screen)
        }
    }

    /**
     * Quay lại màn hình trước đó
     */
    fun popBackStack(): Boolean {
        return if (canGoBack) {
            backStack.removeAt(backStack.size - 1)
            true
        } else {
            false
        }
    }

    /**
     * Điều hướng đến màn hình mới và dọn sạch ngăn xếp cũ
     * (Thường dùng khi đăng nhập thành công hoặc đăng xuất)
     */
    fun navigateAndClear(screen: AppScreen) {
        backStack.clear()
        backStack.add(screen)
    }
}

// =========================================================================
// 3. DỮ LIỆU ONBOARDING
// =========================================================================
val onboardingPages = listOf(
    OnboardingPage(
        id = 1,
        title = "Mọi việc, gọn trong một chỗ",
        description = "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào.",
        image = R.drawable.ic_container,
        buttonText = "Tiếp tục"
    ),
    OnboardingPage(
        id = 2,
        title = "Chia theo Học tập và Cuộc sống",
        description = "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần làm gì.",
        image = R.drawable.ic_container_1,
        buttonText = "Tiếp tục"
    ),
    OnboardingPage(
        id = 3,
        title = "Nhắc đúng lúc",
        description = "Bật thông báo để UTH Todo nhắc bạn trước hạn chót.",
        image = R.drawable.ic_container_2,
        buttonText = "Bắt đầu ngay"
    )
)

// =========================================================================
// 4. HÀM ĐIỀU HƯỚNG CHÍNH CỦA ỨNG DỤNG (AppNavigation)
// =========================================================================
@Composable
fun AppNavigation() {
    val navigator = remember { AppNavigator(AppScreen.Start) }
    val homeViewModel: HomeViewModel = viewModel()

    // Xử lý nút Back vật lý của hệ điều hành Android
    BackHandler(enabled = navigator.canGoBack) {
        navigator.popBackStack()
    }

    // Tự động chuyển từ StartScreen sang OnboardingScreen sau 3 giây (3000ms)
    LaunchedEffect(Unit) {
        delay(3000L)
        if (navigator.currentScreen == AppScreen.Start) {
            navigator.navigateAndClear(AppScreen.Onboarding)
        }
    }

    // Hiệu ứng chuyển động mượt mà giữa các màn hình (Slide & Fade)
    AnimatedContent(
        targetState = navigator.currentScreen,
        transitionSpec = {
            (slideInHorizontally(
                animationSpec = tween(350, easing = FastOutSlowInEasing),
                initialOffsetX = { fullWidth -> fullWidth }
            ) + fadeIn(animationSpec = tween(350))) togetherWith (
                slideOutHorizontally(
                    animationSpec = tween(300, easing = FastOutSlowInEasing),
                    targetOffsetX = { fullWidth -> -fullWidth / 4 }
                ) + fadeOut(animationSpec = tween(300))
            )
        },
        label = "AppScreenTransition"
    ) { screen ->
        when (screen) {
            // 1. Màn hình Chào (Splash/Start)
            AppScreen.Start -> {
                StartScreen()
            }

            // 2. Màn hình Hướng dẫn (Onboarding)
            AppScreen.Onboarding -> {
                MainOnboardingScreen(
                    onFinished = {
                        // Hoàn tất Onboarding -> Chuyển sang màn hình Đăng nhập
                        navigator.navigateTo(AppScreen.Login)
                    }
                )
            }

            // 3. Màn hình Đăng nhập (Login)
            AppScreen.Login -> {
                LoginScreen(
                    onLoginSuccess = {
                        // Đăng nhập thành công -> Vào Trang chủ và dọn sạch stack đăng nhập
                        navigator.navigateAndClear(AppScreen.Home)
                    },
                    onGuestLogin = {
                        // Dùng thử không cần tài khoản -> Vào thẳng Trang chủ
                        navigator.navigateAndClear(AppScreen.Home)
                    },
                    onBackClick = {
                        // Bấm quay lại -> Trở lại Onboarding
                        navigator.popBackStack()
                    }
                )
            }

            // 4. Màn hình Trang chủ (Home)
            AppScreen.Home -> {
                HomeScreen(
                    viewModel = homeViewModel,
                    onAddNewTaskClick = {
                        // TODO: Mở dialog hoặc màn hình thêm công việc
                    },
                    onNavigateToProfile = {
                        // Nhấn vào Avatar hoặc Tab Cài đặt -> Mở màn hình Hồ sơ cá nhân
                        navigator.navigateTo(AppScreen.Profile)
                    },
                    onTaskClick = { taskId, fromScreenName ->
                        // Chuyển sang màn hình Chi tiết và lưu vết màn hình nguồn ("Trang chủ" hoặc "Danh sách")
                        navigator.navigateTo(AppScreen.Detail(taskId = taskId, fromScreenName = fromScreenName))
                    }
                )
            }

            // 5. Màn hình Hồ sơ cá nhân (Profile)
            AppScreen.Profile -> {
                ProfileScreen(
                    onNavigateBackToSettings = {
                        // Bấm "< Cài đặt" -> Quay lại Trang chủ
                        navigator.popBackStack()
                    },
                    onEditProfileClick = {
                        // TODO: Mở màn hình chỉnh sửa hồ sơ
                    },
                    onLogoutClick = {
                        // Đăng xuất -> Quay lại màn hình Đăng nhập và dọn sạch stack
                        navigator.navigateAndClear(AppScreen.Login)
                    }
                )
            }

            // 6. Màn hình Chi tiết công việc (Detail)
            is AppScreen.Detail -> {
                DetailScreen(
                    taskId = screen.taskId,
                    fromScreenName = screen.fromScreenName,
                    onNavigateBack = {
                        navigator.popBackStack()
                    },
                    onTaskDeleted = {
                        // Xóa công việc xong -> Quay lại và cập nhật lại danh sách công việc ở Home/List
                        navigator.popBackStack()
                        homeViewModel.fetchTasks()
                    },
                    onTaskUpdated = {
                        // Cập nhật công việc xong -> Làm mới danh sách công việc ở Home/List
                        homeViewModel.fetchTasks()
                    }
                )
            }
        }
    }
}

// =========================================================================
// 5. MÀN HÌNH ONBOARDING PAGER
// =========================================================================
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainOnboardingScreen(
    onFinished: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { position ->
        val currentPageData = onboardingPages[position]

        OnboardingContent(
            page = currentPageData,
            onSkipClicked = {
                // Nhấn "Bỏ qua" -> Chuyển thẳng đến màn hình Đăng nhập
                onFinished()
            },
            onNextClicked = {
                if (pagerState.currentPage < onboardingPages.size - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage + 1,
                            animationSpec = tween(
                                durationMillis = 400,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                } else {
                    // Trang cuối cùng (Bắt đầu ngay) -> Hoàn tất Onboarding
                    onFinished()
                }
            }
        )
    }
}
