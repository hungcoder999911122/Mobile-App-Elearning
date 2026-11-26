package com.example.bai02.ui.screens.notifications

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bai02.data.model.NotificationItem
import com.example.bai02.data.model.NotificationType
import com.example.bai02.data.model.NotificationUiState

private val NotificationPrimaryBlue = Color(0xFF2F69FE)
private val NotificationTextPrimary = Color(0xFF111827)
private val NotificationTextSecondary = Color(0xFF6B7280)
private val NotificationTextMuted = Color(0xFF94A3B8)

/**
 * Màn hình Thông báo (Stateful Composable) kết nối với NotificationViewModel.
 */
@Composable
fun NotificationContent(
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = viewModel(),
    onAddNewTaskClick: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Bắt đầu đếm ngược 10 giây khi tab Thông báo được mở
    LaunchedEffect(Unit) {
        viewModel.onNotificationTabOpened()
    }

    NotificationScreenContent(
        uiState = uiState,
        modifier = modifier,
        onMarkAllAsRead = viewModel::markAllAsRead,
        onNotificationClick = viewModel::markAsRead,
        onAddNewTaskClick = onAddNewTaskClick,
        onNavigateToSettings = onNavigateToSettings
    )
}

/**
 * Stateless Composable hiển thị giao diện Thông báo dựa trên NotificationUiState.
 */
@Composable
fun NotificationScreenContent(
    uiState: NotificationUiState,
    modifier: Modifier = Modifier,
    onMarkAllAsRead: () -> Unit = {},
    onNotificationClick: (String) -> Unit = {},
    onAddNewTaskClick: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header: "Thông báo" + "Đánh dấu đã đọc" (chỉ hiện khi có thông báo)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Thông báo",
                color = NotificationTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            if (!uiState.isEmpty) {
                Text(
                    text = "Đánh dấu đã đọc",
                    color = if (uiState.hasUnread) NotificationPrimaryBlue else NotificationTextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = uiState.hasUnread) { onMarkAllAsRead() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Chuyển đổi mượt mà giữa danh sách thông báo và Empty View khi danh sách về rỗng
        AnimatedContent(
            targetState = uiState.isEmpty,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(250))
            },
            label = "NotificationListOrEmptyTransition",
            modifier = Modifier.weight(1f)
        ) { isEmpty ->
            if (isEmpty) {
                NotificationEmptyView(
                    onAddNewTaskClick = onAddNewTaskClick,
                    onNavigateToSettings = onNavigateToSettings
                )
            } else {
                NotificationListView(
                    notifications = uiState.notifications,
                    onNotificationClick = onNotificationClick
                )
            }
        }
    }
}

@Composable
private fun NotificationListView(
    notifications: List<NotificationItem>,
    onNotificationClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFEFF2F7)),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    notifications.forEachIndexed { index, item ->
                        NotificationRow(
                            item = item,
                            onClick = { onNotificationClick(item.id) }
                        )
                        if (index < notifications.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 1.dp,
                                color = Color(0xFFF1F5F9)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    item: NotificationItem,
    onClick: () -> Unit
) {
    val style = item.type.toVisualStyle()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics(mergeDescendants = true) {}
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon theo loại thông báo
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(style.containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = style.icon,
                contentDescription = null,
                tint = style.iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    color = NotificationTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.timeText,
                        color = NotificationTextMuted,
                        fontSize = 12.sp
                    )
                    if (item.isUnread) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(NotificationPrimaryBlue)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.message,
                color = NotificationTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun NotificationEmptyView(
    onAddNewTaskClick: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Biểu tượng chuông kèm badge tick xanh lá
        Box(
            modifier = Modifier.size(104.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDF3FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = NotificationPrimaryBlue,
                    modifier = Modifier.size(42.dp)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Chưa có thông báo nào",
            color = NotificationTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Khi có việc sắp đến hạn, quá hạn hoặc lời nhắc, bạn sẽ thấy chúng ở đây.",
            color = NotificationTextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddNewTaskClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = NotificationPrimaryBlue,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Thêm việc cần làm",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        TextButton(onClick = onNavigateToSettings) {
            Text(
                text = "Cài đặt nhắc việc",
                color = NotificationPrimaryBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private data class NotificationVisualStyle(
    val icon: ImageVector,
    val iconTint: Color,
    val containerColor: Color
)

private fun NotificationType.toVisualStyle(): NotificationVisualStyle = when (this) {
    NotificationType.UPCOMING_DEADLINE -> NotificationVisualStyle(
        icon = Icons.Outlined.Alarm,
        iconTint = NotificationPrimaryBlue,
        containerColor = Color(0xFFE8F0FE)
    )
    NotificationType.OVERDUE -> NotificationVisualStyle(
        icon = Icons.Outlined.WarningAmber,
        iconTint = Color(0xFFEF4444),
        containerColor = Color(0xFFFEECEC)
    )
    NotificationType.GREAT_JOB -> NotificationVisualStyle(
        icon = Icons.Outlined.Check,
        iconTint = Color(0xFF22C55E),
        containerColor = Color(0xFFE8F8EE)
    )
    NotificationType.REMINDER -> NotificationVisualStyle(
        icon = Icons.Outlined.Alarm,
        iconTint = Color(0xFF8B5CF6),
        containerColor = Color(0xFFF3E8FF)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FD)
@Composable
private fun NotificationListPreview() {
    NotificationScreenContent(
        uiState = NotificationUiState(notifications = NotificationItem.getSampleNotifications())
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FD)
@Composable
private fun NotificationEmptyPreview() {
    NotificationScreenContent(
        uiState = NotificationUiState(notifications = emptyList())
    )
}

