package com.example.bai01.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bai01.data.model.HomeTab
import com.example.bai01.data.model.HomeUiState
import com.example.bai01.data.model.Task
import com.example.bai01.data.model.TaskCategory
import com.example.bai01.data.model.UserProfile
import com.example.bai01.data.repository.FakeTaskRepository
import com.example.bai01.data.repository.TaskRepository

/**
 * =========================================================================
 * MÀN HÌNH CHÍNH (HomeScreen)
 * Áp dụng mô hình OOP & Clean UI Architecture:
 * 1. Tách biệt hoàn toàn Model và State khỏi tầng hiển thị Composable.
 * 2. Sử dụng State Hoisting: UI chỉ hiển thị dữ liệu và bắn sự kiện ra ngoài.
 * 3. Hỗ trợ Dependency Injection thông qua Repository Interface.
 * =========================================================================
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    repository: TaskRepository = remember { FakeTaskRepository() },
    onAddNewTaskClick: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    // Quản lý trạng thái màn hình bằng một đối tượng HomeUiState duy nhất (OOP State Pattern)
    var uiState by remember { mutableStateOf(repository.getInitialHomeUiState()) }

    HomeScreenContent(
        uiState = uiState,
        modifier = modifier,
        onToggleTask = { taskId ->
            // Đóng gói hành vi trong HomeUiState (OOP Encapsulation)
            uiState = uiState.toggleTask(taskId)
        },
        onSelectTab = { selectedTab ->
            if (selectedTab == HomeTab.SETTINGS) {
                onNavigateToProfile()
            } else {
                uiState = uiState.selectTab(selectedTab)
            }
        },
        onAddNewTaskClick = onAddNewTaskClick,
        onNavigateToProfile = onNavigateToProfile
    )
}

/**
 * Stateless Composable: Chịu trách nhiệm render thuần túy giao diện dựa trên HomeUiState.
 * Rất dễ viết Unit Test, UI Test và Preview.
 */
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onToggleTask: (String) -> Unit,
    onSelectTab: (HomeTab) -> Unit,
    onAddNewTaskClick: () -> Unit,
    onNavigateToProfile: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F9FD),
        bottomBar = {
            CustomBottomBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = onSelectTab,
                onAddClick = onAddNewTaskClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 1. Header (Avatar MA + Lời chào người dùng + Thông báo có badge)
            item {
                TopHeaderSection(
                    user = uiState.user,
                    dateText = uiState.dateText,
                    hasUnreadNotifications = uiState.hasUnreadNotifications,
                    onAvatarClick = onNavigateToProfile
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // 2. Card Tiến độ hôm nay (Circular Donut Chart tính toán tự động bằng OOP)
            item {
                ProgressBannerCard(uiState = uiState)
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // 3. Danh mục công việc (Số việc còn lại tự động tính qua hàm getRemainingTasksCount)
            item {
                CategoriesSection(
                    categories = uiState.categories,
                    getRemainingCount = { category -> uiState.getRemainingTasksCount(category) }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // 4. Tiêu đề danh sách: "Việc hôm nay" - "Xem tất cả"
            item {
                TaskListHeaderSection(
                    onSeeAllClick = { /* TODO: Điều hướng sang màn hình xem tất cả */ }
                )
            }

            item { Spacer(modifier = Modifier.height(14.dp)) }

            // 5. Danh sách các Task Item
            items(items = uiState.tasks, key = { it.id }) { task ->
                TaskCardItem(
                    task = task,
                    onToggleComplete = { onToggleTask(task.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Khoảng đệm phía cuối danh sách để nội dung không bị cấn thanh điều hướng
            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

// =========================================================================
// CÁC THÀNH PHẦN GIAO DIỆN CON (COMPONENTS)
// =========================================================================

/**
 * 1. Tiêu đề trên cùng (Header): Nhận đối tượng UserProfile để hiển thị avatar và lời chào
 */
@Composable
fun TopHeaderSection(
    user: UserProfile,
    dateText: String,
    hasUnreadNotifications: Boolean,
    onAvatarClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar hình tròn với Initials tính toán từ UserProfile
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E7FE))
                    .clickable { onAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.initials,
                    color = Color(0xFF2F69FE),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = dateText,
                    color = Color(0xFF6B7280),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = user.greetingText,
                    color = Color(0xFF111827),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Nút chuông thông báo có chấm đỏ (Badge)
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onNotificationClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Thông báo",
                tint = Color(0xFF1E293B),
                modifier = Modifier.size(24.dp)
            )
            if (hasUnreadNotifications) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-10).dp, y = 10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                )
            }
        }
    }
}

/**
 * 2. Banner Tiến độ: Dữ liệu (tỉ lệ, phần trăm, chuỗi hiển thị) được tính toán tự động từ HomeUiState
 */
@Composable
fun ProgressBannerCard(uiState: HomeUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0x332F69FE)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2F69FE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tiến độ hôm nay",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Chuỗi tiến độ tính toán từ OOP: "3/7 việc"
                Text(
                    text = uiState.progressDisplayText,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    // Đọc giá trị từ Firebase Remote Config gửi xuống
                    text = com.example.bai01.data.repository.RemoteConfigManager.getBannerMessage(),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Biểu đồ hình tròn Donut hiển thị % tiến độ tính toán từ OOP
            Box(
                modifier = Modifier.size(68.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { uiState.progressRatio },
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.28f),
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = "${uiState.progressPercentage}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * 3. Danh mục công việc: Hiển thị danh sách các Category dưới dạng cột cân đối
 */
@Composable
fun CategoriesSection(
    categories: List<TaskCategory>,
    getRemainingCount: (TaskCategory) -> Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        categories.forEach { category ->
            CategoryCardItem(
                category = category,
                remainingCount = getRemainingCount(category),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CategoryCardItem(
    category: TaskCategory,
    remainingCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Icon trong hộp vuông bo tròn
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(category.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = category.iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Số lượng việc còn lại được tính toán động (OOP)
            Text(
                text = "$remainingCount việc còn lại",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 4. Header phần danh sách công việc
 */
@Composable
fun TaskListHeaderSection(onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Việc hôm nay",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Text(
            text = "Xem tất cả",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2F69FE),
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}

/**
 * 5. Từng dòng công việc (Task Item)
 */
@Composable
fun TaskCardItem(
    task: Task,
    onToggleComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleComplete() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút Check hoàn thành (Tròn xanh hoặc Tròn xám rỗng)
            Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                contentDescription = if (task.isCompleted) "Đã hoàn thành" else "Chưa hoàn thành",
                tint = if (task.isCompleted) Color(0xFF2F69FE) else Color(0xFFCBD5E1),
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onToggleComplete() }
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isCompleted) Color(0xFF94A3B8) else Color(0xFF1E293B),
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Tag phân loại
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(task.category.tagBgColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = task.category.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = task.category.tagTextColor
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Giờ thực hiện
                    Text(
                        text = task.time,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Mũi tên góc phải
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 6. Thanh Bottom Navigation (Sử dụng Enum HomeTab thay vì số nguyên Primitive)
 * Đảm bảo tự động chừa khoảng trống an toàn né thanh điều hướng hệ thống (navigationBarsPadding)
 * và co giãn linh hoạt đều 20% cho 5 vị trí trên mọi loại thiết bị màn hình.
 */
@Composable
fun CustomBottomBar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White) // Nền trắng tràn xuống tận cùng mép dưới màn hình
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding() // Tự động né thanh điều hướng 3 nút hoặc cử chỉ của Android
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 1: Trang chủ (chiếm 20% chiều rộng)
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        BottomNavItem(
                            tab = HomeTab.HOME,
                            isSelected = selectedTab == HomeTab.HOME,
                            onClick = { onTabSelected(HomeTab.HOME) }
                        )
                    }

                    // Tab 2: Danh sách (chiếm 20% chiều rộng)
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        BottomNavItem(
                            tab = HomeTab.TASKS,
                            isSelected = selectedTab == HomeTab.TASKS,
                            onClick = { onTabSelected(HomeTab.TASKS) }
                        )
                    }

                    // Khoảng trống ở giữa dành cho nút tròn FAB (+) (chiếm 20% chiều rộng)
                    Spacer(modifier = Modifier.weight(1f))

                    // Tab 3: Thông báo (chiếm 20% chiều rộng)
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        BottomNavItem(
                            tab = HomeTab.NOTIFICATIONS,
                            isSelected = selectedTab == HomeTab.NOTIFICATIONS,
                            onClick = { onTabSelected(HomeTab.NOTIFICATIONS) }
                        )
                    }

                    // Tab 4: Cài đặt (chiếm 20% chiều rộng)
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        BottomNavItem(
                            tab = HomeTab.SETTINGS,
                            isSelected = selectedTab == HomeTab.SETTINGS,
                            onClick = { onTabSelected(HomeTab.SETTINGS) }
                        )
                    }
                }
            }
        }

        // Nút tròn (+) nổi ở chính giữa thanh điều hướng
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .size(54.dp)
                .shadow(elevation = 10.dp, shape = CircleShape, spotColor = Color(0x662F69FE))
                .clip(CircleShape)
                .background(Color(0xFF2F69FE))
                .clickable { onAddClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Thêm công việc",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun BottomNavItem(
    tab: HomeTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = if (isSelected) Color(0xFF2F69FE) else Color(0xFF94A3B8),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = tab.label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF2F69FE) else Color(0xFF94A3B8),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// =========================================================================
// PREVIEW
// =========================================================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenDesignPreview() {
    HomeScreen()
}
