package com.example.bai01.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bai01.data.model.DayActivity
import com.example.bai01.data.model.ProfileUiState
import com.example.bai01.data.model.UserProfile
import com.example.bai01.data.model.UserStats
import com.example.bai01.data.model.WeeklyActivity
import com.example.bai01.data.repository.FakeProfileRepository
import com.example.bai01.data.repository.ProfileRepository

/**
 * =========================================================================
 * MÀN HÌNH HỒ SƠ CÁ NHÂN (ProfileScreen)
 * Áp dụng kiến trúc OOP & Clean UI Architecture:
 * - Đóng gói dữ liệu người dùng, thống kê và biểu đồ trong ProfileUiState.
 * - Tách biệt Stateful Composable và Stateless Composable (State Hoisting).
 * - Sử dụng ProfileRepository để hỗ trợ mở rộng Firebase/Room sau này.
 * =========================================================================
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    repository: ProfileRepository = remember { FakeProfileRepository() },
    onNavigateBackToSettings: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var uiState by remember { mutableStateOf(repository.getProfileUiState()) }

    ProfileScreenContent(
        uiState = uiState,
        modifier = modifier,
        onBackClick = onNavigateBackToSettings,
        onEditClick = onEditProfileClick,
        onChangeAvatarClick = { /* TODO: Chọn ảnh đại diện từ thư viện */ },
        onSyncClick = {
            uiState = repository.syncUserData()
        },
        onPrivacyClick = { /* TODO: Mở điều khoản quyền riêng tư */ },
        onLogoutClick = onLogoutClick
    )
}

/**
 * Stateless Composable: Chịu trách nhiệm render toàn bộ giao diện từ ProfileUiState
 */
@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onChangeAvatarClick: () -> Unit,
    onSyncClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F9FD),
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            ProfileTopAppBar(
                onBackClick = onBackClick,
                onEditClick = onEditClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 1. Avatar lớn, Tên, Email và Badge Liên kết Google
            item {
                ProfileHeaderSection(
                    user = uiState.user,
                    onChangeAvatarClick = onChangeAvatarClick
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // 2. 3 Thẻ thống kê: Việc đã xong, Chuỗi liên tiếp, Đúng hạn
            item {
                StatsRowSection(stats = uiState.stats)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 3. Card Biểu đồ hoạt động: "Tuần này" (18/22 việc)
            item {
                WeeklyActivityCard(weeklyActivity = uiState.weeklyActivity)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 4. Nhóm cài đặt: Đồng bộ dữ liệu & Quyền riêng tư
            item {
                SettingsActionCard(
                    lastSyncTime = uiState.lastSyncTimeText,
                    onSyncClick = onSyncClick,
                    onPrivacyClick = onPrivacyClick
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // 5. Nút Đăng xuất
            item {
                LogoutButton(onLogoutClick = onLogoutClick)
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

// =========================================================================
// CÁC THÀNH PHẦN GIAO DIỆN CON (COMPONENTS)
// =========================================================================

/**
 * 1. Thanh tiêu đề trên cùng (Top App Bar): Nút "< Cài đặt", Tiêu đề "Hồ sơ", Nút "Sửa"
 */
@Composable
fun ProfileTopAppBar(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút quay lại: "< Cài đặt"
        Row(
            modifier = Modifier
                .clickable { onBackClick() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "Quay lại Cài đặt",
                tint = Color(0xFF2F69FE),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Cài đặt",
                color = Color(0xFF2F69FE),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Tiêu đề chính giữa: "Hồ sơ"
        Text(
            text = "Hồ sơ",
            color = Color(0xFF111827),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        // Nút "Sửa" bên phải
        Text(
            text = "Sửa",
            color = Color(0xFF2F69FE),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable { onEditClick() }
                .padding(vertical = 4.dp, horizontal = 4.dp)
        )
    }
}

/**
 * 2. Khối Avatar, Thông tin cá nhân và Nhãn liên kết Google
 */
@Composable
fun ProfileHeaderSection(
    user: UserProfile,
    onChangeAvatarClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Avatar tròn lớn kèm nút Camera nhỏ ở góc dưới phải
        Box(
            modifier = Modifier.size(96.dp),
            contentAlignment = Alignment.Center
        ) {
            // Vòng tròn Avatar chính
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E7FE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.initials,
                    color = Color(0xFF2F69FE),
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            }

            // Nút Camera nhỏ ở góc dưới bên phải
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2F69FE))
                    .border(2.dp, Color.White, CircleShape)
                    .clickable { onChangeAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Đổi ảnh đại diện",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tên người dùng
        Text(
            text = user.fullName,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Email người dùng
        Text(
            text = user.email,
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Nhãn trạng thái liên kết tài khoản Google
        if (user.isGoogleLinked) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE6F4EA))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Đã liên kết tài khoản Google",
                        color = Color(0xFF16A34A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * 3. Hàng 3 thẻ thống kê: "Việc đã xong", "Chuỗi liên tiếp", "Đúng hạn"
 */
@Composable
fun StatsRowSection(stats: UserStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatItemCard(
            value = stats.completedTasksText,
            label = "Việc đã xong",
            modifier = Modifier.weight(1f)
        )
        StatItemCard(
            value = stats.streakText,
            label = "Chuỗi liên tiếp",
            modifier = Modifier.weight(1f)
        )
        StatItemCard(
            value = stats.onTimeText,
            label = "Đúng hạn",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatItemCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 4. Card Biểu đồ hoạt động "Tuần này"
 */
@Composable
fun WeeklyActivityCard(weeklyActivity: WeeklyActivity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Tiêu đề card và tóm tắt số lượng (18/22 việc)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tuần này",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = weeklyActivity.summaryText,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Biểu đồ cột 7 ngày trong tuần
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxCount = weeklyActivity.maxCountInWeek

                weeklyActivity.days.forEach { day ->
                    DayBarItem(
                        day = day,
                        barHeightFraction = day.calculateBarFraction(maxCount)
                    )
                }
            }
        }
    }
}

@Composable
fun DayBarItem(
    day: DayActivity,
    barHeightFraction: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        // Cột màu đại diện cho số lượng việc
        val barColor = when {
            day.isCurrentDay -> Color(0xFF2F69FE)   // Ngày hôm nay (T3): Xanh đậm nổi bật
            day.isPastDay -> Color(0xFFB4C6FC)      // Ngày đã qua: Xanh nhạt
            else -> Color(0xFFE2E8F0)               // Các ngày còn lại: Xám nhạt
        }

        Box(
            modifier = Modifier
                .width(16.dp)
                .fillMaxHeight(barHeightFraction.coerceIn(0.18f, 0.95f))
                .clip(RoundedCornerShape(6.dp))
                .background(barColor)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Nhãn ngày bên dưới (T2, T3, T4...)
        Text(
            text = day.dayLabel,
            fontSize = 11.sp,
            fontWeight = if (day.isCurrentDay) FontWeight.Bold else FontWeight.Normal,
            color = if (day.isCurrentDay) Color(0xFF111827) else Color(0xFF94A3B8)
        )
    }
}

/**
 * 5. Card Cài đặt: Đồng bộ dữ liệu & Quyền riêng tư
 */
@Composable
fun SettingsActionCard(
    lastSyncTime: String,
    onSyncClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Mục 1: Đồng bộ dữ liệu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSyncClick() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Đồng bộ dữ liệu",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = lastSyncTime,
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            HorizontalDivider(
                color = Color(0xFFF1F5F9),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Mục 2: Quyền riêng tư
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPrivacyClick() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Quyền riêng tư",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * 6. Nút Đăng xuất
 */
@Composable
fun LogoutButton(onLogoutClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLogoutClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Đăng xuất",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Đăng xuất",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDC2626)
            )
        }
    }
}

// =========================================================================
// PREVIEW
// =========================================================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}

