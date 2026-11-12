package com.example.bai02.ui.screens.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.bai02.data.model.HomeUiState
import com.example.bai02.data.model.Task
import com.example.bai02.data.model.TaskFilterType
import kotlinx.coroutines.delay

/**
 * Màn hình Danh sách công việc (TaskListScreen).
 * Áp dụng State Hoisting: nhận dữ liệu từ HomeUiState và bắn sự kiện về ViewModel.
 */
@Composable
fun TaskListContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onToggleTask: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectFilter: (TaskFilterType) -> Unit,
    onTaskClick: (String) -> Unit = {},
    onRetryClick: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // 1. HEADER: Tiêu đề "Danh sách" và thống kê "X việc chưa xong"
        item {
            TaskListHeader(uncompletedCountText = uiState.uncompletedCountText)
        }

        item { Spacer(modifier = Modifier.height(18.dp)) }

        // 2. THANH TÌM KIẾM
        item {
            TaskSearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange
            )
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }

        // 3. BỘ LỌC DANH MỤC (CHIPS: Tất cả, Học tập, Cuộc sống)
        item {
            TaskFilterChips(
                selectedFilter = uiState.selectedFilter,
                onSelectFilter = onSelectFilter
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // 4. XỬ LÝ 4 TRẠNG THÁI GIAO DIỆN (UI STATES)
        when {
            // TRẠNG THÁI 1: ĐANG TẢI DỮ LIỆU TỪ API (LOADING)
            uiState.isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF2F69FE),
                            strokeWidth = 3.dp
                        )
                    }
                }
            }

            // TRẠNG THÁI 2: LỖI KẾT NỐI / SERVER HTTP 500 (ERROR)
            uiState.errorMessage != null -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                            .background(Color(0xFFFFF0F0), RoundedCornerShape(14.dp))
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Không thể tải danh sách công việc!",
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onRetryClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F69FE)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
            }

            // TRẠNG THÁI 3: DANH SÁCH RỖNG HOẶC KHÔNG TÌM THẤY KẾT QUẢ (EMPTY)
            uiState.filteredTasks.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState.searchQuery.isNotBlank())
                                "Không tìm thấy công việc nào khớp với \"${uiState.searchQuery}\""
                            else
                                "Không có công việc nào trong mục này!",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // TRẠNG THÁI 4: HIỂN THỊ DANH SÁCH THÀNH CÔNG (SUCCESS)
            else -> {
                // --- NHÓM 1: CẦN LÀM ---
                if (uiState.todoTasks.isNotEmpty()) {
                    item {
                        TaskSectionTitle(title = "CẦN LÀM")
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(items = uiState.todoTasks, key = { it.id }) { task ->
                        TaskListItemCard(
                            task = task,
                            onToggleComplete = { onToggleTask(task.id) },
                            onTaskClick = { onTaskClick(task.id) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // --- NHÓM 2: ĐÃ XONG ---
                if (uiState.completedTasks.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        TaskSectionTitle(title = "ĐÃ XONG")
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(items = uiState.completedTasks, key = { it.id }) { task ->
                        TaskListItemCard(
                            task = task,
                            onToggleComplete = { onToggleTask(task.id) },
                            onTaskClick = { onTaskClick(task.id) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        // Khoảng đệm phía cuối danh sách né BottomBar
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// =========================================================================
// CÁC THÀNH PHẦN CON (SUB-COMPONENTS)
// =========================================================================

/**
 * 1. Header: Tiêu đề to "Danh sách" và "4 việc chưa xong"
 */
@Composable
fun TaskListHeader(uncompletedCountText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Danh sách",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Text(
            text = uncompletedCountText,
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )
    }
}

/**
 * 2. Thanh tìm kiếm công việc (Search Bar)
 */
@Composable
fun TaskSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Tìm công việc",
                color = Color(0xFF94A3B8),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Tìm kiếm",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Xóa từ khóa",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF2F69FE),
            unfocusedBorderColor = Color(0xFFE2E8F0)
        )
    )
}

/**
 * 3. Hàng Chips lọc danh mục: Tất cả, Học tập, Cuộc sống
 */
@Composable
fun TaskFilterChips(
    selectedFilter: TaskFilterType,
    onSelectFilter: (TaskFilterType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskFilterType.entries.forEach { filter ->
            val isSelected = selectedFilter == filter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Color(0xFF0F172A) else Color.White)
                    .then(
                        if (!isSelected) {
                            Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                        } else Modifier
                    )
                    .clickable { onSelectFilter(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter.label,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) Color.White else Color(0xFF475569)
                )
            }
        }
    }
}

/**
 * 4. Tiêu đề nhóm ("CẦN LÀM", "ĐÃ XONG")
 */
@Composable
fun TaskSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        letterSpacing = 0.5.sp
    )
}

/**
 * 5. Thẻ hiển thị một công việc trong danh sách
 */
@Composable
fun TaskListItemCard(
    task: Task,
    onToggleComplete: () -> Unit,
    onTaskClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTaskClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút Check tròn (Tích xanh nếu hoàn thành, vòng rỗng xám nếu chưa)
            Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                contentDescription = if (task.isCompleted) "Đã hoàn thành" else "Chưa hoàn thành",
                tint = if (task.isCompleted) Color(0xFF2F69FE) else Color(0xFFCBD5E1),
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable { onToggleComplete() }
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Tên công việc (gạch ngang nếu hoàn thành)
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isCompleted) Color(0xFF94A3B8) else Color(0xFF1E293B),
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Chỉ hiển thị tag danh mục và giờ khi việc chưa hoàn thành (hoặc tùy chọn hiển thị cả 2)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Tag phân loại (Học tập / Cuộc sống)
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

                    // Ngày & Giờ thực hiện
                    Text(
                        text = task.formattedDateTime,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Nhãn "Gấp" màu đỏ bên phải (nếu task.isUrgent == true và chưa hoàn thành)
            if (task.isUrgent && !task.isCompleted) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFEBEB))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Gấp",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}

