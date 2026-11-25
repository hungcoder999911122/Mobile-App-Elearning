package com.example.bai02.ui.screens.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bai02.data.model.DetailUiState
import com.example.bai02.data.model.Subtask
import com.example.bai02.data.model.Task
import kotlinx.coroutines.delay

/**
 * =========================================================================
 * MÀN HÌNH CHI TIẾT CÔNG VIỆC (DetailScreen)
 * Áp dụng State Hoisting & Kiến trúc Clean UI
 * =========================================================================
 */
@Composable
fun DetailScreen(
    taskId: String,
    fromScreenName: String = "Trang chủ",
    viewModel: DetailViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onTaskDeleted: () -> Unit = {},
    onTaskUpdated: () -> Unit = {}
) {
    // 1. Tự động tải dữ liệu khi mở màn hình
    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    val uiState by viewModel.uiState.collectAsState()

    // 2. Trạng thái thông báo Toast nổi "Tuyệt, đã xong một việc!"
    var toastTrigger by remember { mutableIntStateOf(0) }
    var showCompletionToast by remember { mutableStateOf(false) }

    LaunchedEffect(toastTrigger) {
        if (toastTrigger > 0) {
            showCompletionToast = true
            delay(2000L)
            showCompletionToast = false
        }
    }

    // 3. Dialog xác nhận xóa công việc
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // 4. Dialog thêm việc nhỏ (Subtask)
    var showAddSubtaskDialog by remember { mutableStateOf(false) }
    var newSubtaskTitle by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFFF8F9FD)
        ) { paddingValues ->
            when {
                // TRẠNG THÁI 1: LOADING
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF2F69FE),
                            strokeWidth = 3.dp
                        )
                    }
                }

                // TRẠNG THÁI 2: ERROR
                uiState.errorMessage != null && uiState.task == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .background(Color(0xFFFFF0F0), RoundedCornerShape(16.dp))
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "Lỗi tải thông tin chi tiết!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F),
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.errorMessage ?: "",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadTask(taskId) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F69FE))
                            ) {
                                Text("Thử lại")
                            }
                        }
                    }
                }

                // TRẠNG THÁI 3: SUCCESS (HIỂN THỊ DỮ LIỆU)
                uiState.task != null -> {
                    val task = uiState.task!!

                    DetailScreenContent(
                        task = task,
                        fromScreenName = fromScreenName,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        onBackClick = onNavigateBack,
                        onEditClick = {
                            // TODO: Mở màn hình / dialog chỉnh sửa task
                        },
                        onToggleTaskDone = {
                            viewModel.toggleTaskDone(
                                onCompletedCallback = {
                                    toastTrigger++
                                }
                            )
                            onTaskUpdated()
                        },
                        onToggleSubtask = { subtaskId ->
                            viewModel.toggleSubtask(subtaskId)
                            onTaskUpdated()
                        },
                        onAddSubtaskClick = {
                            newSubtaskTitle = ""
                            showAddSubtaskDialog = true
                        },
                        onEditSubtask = { subtaskId, newTitle ->
                            viewModel.editSubtask(subtaskId, newTitle)
                            onTaskUpdated()
                        },
                        onDeleteSubtask = { subtaskId ->
                            viewModel.deleteSubtask(subtaskId)
                            onTaskUpdated()
                        },
                        onDeleteTaskClick = {
                            showDeleteConfirmDialog = true
                        }
                    )
                }
            }
        }

        // =========================================================================
        // VIÊN THUỐC THÔNG BÁO NỔI PHÍA TRÊN: "Tuyệt, đã xong một việc!"
        // =========================================================================
        AnimatedVisibility(
            visible = showCompletionToast,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(250)),
            modifier = Modifier
                .statusBarsPadding()
                .align(Alignment.TopCenter)
                .offset(y = 12.dp)
                .zIndex(99f)
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        spotColor = Color(0x66000000)
                    )
                    .clip(CircleShape)
                    .background(Color(0xFF22262B))
                    .padding(horizontal = 22.dp, vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tuyệt, đã xong một việc!",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // =========================================================================
        // DIALOG XÁC NHẬN XÓA CÔNG VIỆC
        // =========================================================================
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = {
                    Text(
                        text = "Xóa công việc",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(
                        text = "Bạn có chắc chắn muốn xóa công việc này không? Thao tác này không thể hoàn tác.",
                        fontSize = 14.sp,
                        color = Color(0xFF475569)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirmDialog = false
                            viewModel.deleteTask {
                                onTaskDeleted()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Xóa", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDeleteConfirmDialog = false },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Hủy")
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // =========================================================================
        // DIALOG THÊM VIỆC NHỎ (SUBTASK)
        // =========================================================================
        if (showAddSubtaskDialog) {
            AlertDialog(
                onDismissRequest = { showAddSubtaskDialog = false },
                title = {
                    Text(
                        text = "Thêm việc nhỏ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    OutlinedTextField(
                        value = newSubtaskTitle,
                        onValueChange = { newSubtaskTitle = it },
                        placeholder = { Text("Nhập tên việc nhỏ...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newSubtaskTitle.isNotBlank()) {
                                viewModel.addSubtask(newSubtaskTitle)
                                showAddSubtaskDialog = false
                                onTaskUpdated()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F69FE)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Thêm", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showAddSubtaskDialog = false },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Hủy")
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

/**
 * Giao diện chi tiết dạng Stateless Composable
 */
@Composable
fun DetailScreenContent(
    task: Task,
    fromScreenName: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onToggleTaskDone: () -> Unit,
    onToggleSubtask: (String) -> Unit,
    onAddSubtaskClick: () -> Unit,
    onEditSubtask: (String, String) -> Unit,
    onDeleteSubtask: (String) -> Unit,
    onDeleteTaskClick: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // 1. TOP HEADER: "< Trang chủ / < Danh sách" bên trái và nút "Sửa" bên phải
        item {
            DetailTopHeader(
                fromScreenName = fromScreenName,
                onBackClick = onBackClick,
                onEditClick = onEditClick
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // 2. TAGS: [Học tập] [Ưu tiên vừa] và [Đã xong] (nếu hoàn thành)
        item {
            DetailBadgesSection(task = task)
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }

        // 3. TIÊU ĐỀ CÔNG VIỆC LỚN
        item {
            Text(
                text = task.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (task.isCompleted) Color(0xFF64748B) else Color(0xFF1E293B),
                lineHeight = 32.sp
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // 4. THẺ THỜI GIAN: Hạn chót & Nhắc nhở
        item {
            DetailScheduleCard(task = task)
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // 5. GHI CHÚ
        item {
            DetailNoteSection(note = task.note)
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // 6. DANH SÁCH VIỆC NHỎ (SUBTASKS)
        item {
            DetailSubtasksSection(
                task = task,
                onToggleSubtask = onToggleSubtask,
                onAddSubtaskClick = onAddSubtaskClick,
                onEditSubtask = onEditSubtask,
                onDeleteSubtask = onDeleteSubtask
            )
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        // 7. CÁC NÚT HÀNH ĐỘNG: Đánh dấu hoàn thành / Đánh dấu chưa xong & Xóa công việc
        item {
            DetailActionButtons(
                isCompleted = task.isCompleted,
                onToggleDone = onToggleTaskDone,
                onDeleteClick = onDeleteTaskClick
            )
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

/**
 * 1. Header trên cùng
 */
@Composable
fun DetailTopHeader(
    fromScreenName: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút quay lại
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onBackClick() }
                .padding(vertical = 4.dp, horizontal = 4.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Quay lại",
                tint = Color(0xFF2F69FE),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = fromScreenName,
                color = Color(0xFF2F69FE),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Nút Sửa
        Text(
            text = "Sửa",
            color = Color(0xFF2F69FE),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onEditClick() }
                .padding(vertical = 4.dp, horizontal = 8.dp)
        )
    }
}

/**
 * 2. Hàng nhãn phân loại (Badges)
 */
@Composable
fun DetailBadgesSection(task: Task) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Badge 1: Danh mục (Học tập / Cuộc sống)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(task.category.tagBgColor)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = task.category.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = task.category.tagTextColor
            )
        }

        // Badge 2: Mức ưu tiên
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF1F5F9))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = task.priorityText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF475569)
            )
        }

        // Badge 3: Nhãn "Đã xong" (khi task.isCompleted == true như hình tham chiếu)
        if (task.isCompleted) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFDCFCE7))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Đã xong",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A)
                )
            }
        }
    }
}

/**
 * 3. Card Hạn chót & Nhắc nhở
 */
@Composable
fun DetailScheduleCard(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Dòng 1: Hạn chót
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = "Hạn chót",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Hạn chót",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = task.formattedDateTime,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Dòng 2: Nhắc nhở
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Nhắc nhở",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Nhắc nhở",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = task.remindText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
            }
        }
    }
}

/**
 * 4. Phần Ghi chú
 */
@Composable
fun DetailNoteSection(note: String?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Ghi chú",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (!note.isNullOrBlank()) note else "Không có ghi chú.",
                    fontSize = 14.sp,
                    color = if (!note.isNullOrBlank()) Color(0xFF1E293B) else Color(0xFF94A3B8),
                    lineHeight = 22.sp
                )
            }
        }
    }
}

/**
 * 5. Phần Danh sách việc nhỏ (Subtasks) và Thanh tiến độ
 */
@Composable
fun DetailSubtasksSection(
    task: Task,
    onToggleSubtask: (String) -> Unit,
    onAddSubtaskClick: () -> Unit,
    onEditSubtask: (String, String) -> Unit,
    onDeleteSubtask: (String) -> Unit
) {
    // Lưu ID của subtask đang được mở ở chế độ chỉnh sửa (inline editing)
    var editingSubtaskId by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Tiêu đề & tỉ lệ 1/2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Việc nhỏ",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = task.subtasksProgressText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Thanh tiến độ LinearProgressIndicator bo tròn
        LinearProgressIndicator(
            progress = { task.subtasksProgressRatio },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(CircleShape),
            color = Color(0xFF2F69FE),
            trackColor = Color(0xFFE2E8F0),
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Card bao quanh danh sách subtasks
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                // Từng dòng subtask
                task.subtasks.forEach { subtask ->
                    DetailSubtaskItemRow(
                        subtask = subtask,
                        isEditing = (editingSubtaskId == subtask.id),
                        onToggle = { onToggleSubtask(subtask.id) },
                        onStartEdit = { editingSubtaskId = subtask.id },
                        onSaveEdit = { newTitle ->
                            onEditSubtask(subtask.id, newTitle)
                            editingSubtaskId = null
                        },
                        onDelete = { onDeleteSubtask(subtask.id) }
                    )
                }

                // Dòng Thêm việc nhỏ
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddSubtaskClick() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm việc nhỏ",
                        tint = Color(0xFF2F69FE),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Thêm việc nhỏ",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Thêm",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2F69FE)
                    )
                }
            }
        }
    }
}

/**
 * Từng dòng Subtask: Hỗ trợ cả hiển thị thường và chế độ Chỉnh sửa trực tiếp (Inline Editing)
 * Bám sát 100% hình ảnh tham chiếu được cung cấp
 */
@Composable
fun DetailSubtaskItemRow(
    subtask: Subtask,
    isEditing: Boolean,
    onToggle: () -> Unit,
    onStartEdit: () -> Unit,
    onSaveEdit: (String) -> Unit,
    onDelete: () -> Unit
) {
    var editText by remember(subtask.title, isEditing) { mutableStateOf(subtask.title) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (!isEditing) Modifier.clickable { onToggle() } else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hộp Checkbox vuông/bo góc xanh nếu xong, xám rỗng nếu chưa
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (subtask.isCompleted) Color(0xFF2F69FE) else Color.Transparent)
                .then(
                    if (!subtask.isCompleted) {
                        Modifier.border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                    } else Modifier
                )
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            if (subtask.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Đã xong",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        if (isEditing) {
            // =========================================================================
            // CHẾ ĐỘ SỬA: KHUNG NHẬP TÊN VIỆC NHỎ KÈM NÚT "Lưu" (ĐÚNG THEO ẢNH BỔ SUNG)
            // =========================================================================
            BasicTextField(
                value = editText,
                onValueChange = { editText = it },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.Normal
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (editText.isNotBlank()) {
                            onSaveEdit(editText)
                        }
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Nút "Lưu" màu xanh
            Text(
                text = "Lưu",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2F69FE),
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                        if (editText.isNotBlank()) {
                            onSaveEdit(editText)
                        }
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        } else {
            // =========================================================================
            // CHẾ ĐỘ HIỂN THỊ THƯỜNG: TÊN VIỆC NHỎ, NÚT BÚT CHÌ VÀ THÙNG RÁC
            // =========================================================================
            Text(
                text = subtask.title,
                fontSize = 14.sp,
                color = if (subtask.isCompleted) Color(0xFF94A3B8) else Color(0xFF1E293B),
                textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Nút sửa (Bút chì)
            IconButton(
                onClick = onStartEdit,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Sửa việc nhỏ",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(17.dp)
                )
            }

            // Nút xóa (Thùng rác đỏ)
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa việc nhỏ",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

/**
 * 6. Các nút hành động chính: "Đánh dấu hoàn thành / Đánh dấu chưa xong" và "Xóa công việc"
 */
@Composable
fun DetailActionButtons(
    isCompleted: Boolean,
    onToggleDone: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // NÚT CHÍNH: ĐỔI MÀU & NỘI DUNG THEO TRẠNG THÁI (NHƯ ẢNH 1 VÀ ẢNH 2)
        if (!isCompleted) {
            // Khi chưa hoàn thành (Ảnh 1): Nút màu xanh dương
            Button(
                onClick = onToggleDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F69FE))
            ) {
                Text(
                    text = "Đánh dấu hoàn thành",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            // Khi đã hoàn thành (Ảnh 2): Nút màu trắng có viền xám nhạt
            Button(
                onClick = onToggleDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text(
                    text = "Đánh dấu chưa xong",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Nút xóa công việc: TextButton màu đỏ
        TextButton(
            onClick = onDeleteClick,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = "Xóa công việc",
                color = Color(0xFFEF4444),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

