package com.example.bai02.ui.screens.create

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bai02.data.model.CreateTaskUiState
import com.example.bai02.data.model.TaskCategory
import com.example.bai02.data.model.TaskDateOption
import com.example.bai02.data.model.TaskPriority

private val PrimaryBlue = Color(0xFF2F69FE)
private val SelectedBlueBg = Color(0xFFEDF2FE)
private val ScreenBg = Color(0xFFF6F7FA)
private val CardBorderColor = Color(0xFFEAEEF4)
private val DividerColor = Color(0xFFF0F2F6)
private val TextDark = Color(0xFF1A1D26)
private val TextMuted = Color(0xFF6E7787)
private val PlaceholderGray = Color(0xFFB0B7C3)
private val ToggleGreen = Color(0xFF34C759)

/**
 * Stateful Composable kết nối [CreateTaskViewModel] với giao diện tạo công việc mới.
 */
@Composable
fun CreateTaskScreen(
    viewModel: CreateTaskViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onTaskCreated: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.resetForm()
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onTaskCreated()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            viewModel.dismissError()
        }
    }

    CreateTaskScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCancelClick = onNavigateBack,
        onTitleChange = viewModel::onTitleChange,
        onNoteChange = viewModel::onNoteChange,
        onCategorySelected = viewModel::onCategorySelected,
        onDateSelected = viewModel::onDateSelected,
        onTimeSelected = viewModel::onTimeSelected,
        onRemindToggle = viewModel::onRemindToggle,
        onPrioritySelected = viewModel::onPrioritySelected,
        onSubmitClick = viewModel::submitTask
    )
}

/**
 * Stateless Composable chịu trách nhiệm render giao diện "Việc mới" bám sát mẫu Tuần 04.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreenContent(
    uiState: CreateTaskUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onCancelClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onCategorySelected: (TaskCategory) -> Unit,
    onDateSelected: (TaskDateOption) -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onRemindToggle: (Boolean) -> Unit,
    onPrioritySelected: (TaskPriority) -> Unit,
    onSubmitClick: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        val (initialHour, initialMinute) = remember(uiState.selectedTime) {
            val parts = uiState.selectedTime.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 19
            val minute = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 0
            hour to minute
        }
        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeSelected(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text("Xong", color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Hủy", color = TextMuted)
                }
            },
            title = {
                Text(
                    text = "Chọn giờ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Scaffold(
        containerColor = ScreenBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CreateTaskTopBar(
                canSubmit = uiState.canSubmit,
                onCancelClick = onCancelClick,
                onSaveClick = onSubmitClick
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ScreenBg)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Button(
                    onClick = onSubmitClick,
                    enabled = uiState.canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE5E9F0),
                        disabledContentColor = Color(0xFF9AA3B2)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Đang lưu...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "Lưu công việc",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Card nhập Tên công việc & Ghi chú
            TaskInputCard(
                title = uiState.title,
                titleError = uiState.titleError,
                note = uiState.note,
                onTitleChange = onTitleChange,
                onNoteChange = onNoteChange
            )

            // 2. Chọn Nhóm (Học tập / Cuộc sống)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel(text = "Nhóm")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TaskCategory.getDefaultCategories().forEach { category ->
                        SelectableOptionButton(
                            label = category.name,
                            isSelected = uiState.selectedCategory.id == category.id,
                            onClick = { onCategorySelected(category) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 3. Chọn Ngày (Hôm nay / Ngày mai / Tuần này)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel(text = "Ngày")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TaskDateOption.entries.forEach { dateOption ->
                        SelectableOptionButton(
                            label = dateOption.label,
                            isSelected = uiState.selectedDate == dateOption,
                            onClick = { onDateSelected(dateOption) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 4. Card Giờ & Nhắc tôi
            TimeAndRemindCard(
                selectedTime = uiState.selectedTime,
                isRemindEnabled = uiState.isRemindEnabled,
                onTimeClick = { showTimePicker = true },
                onRemindToggle = onRemindToggle
            )

            // 5. Chọn Mức ưu tiên (Thấp / Vừa / Cao)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel(text = "Mức ưu tiên")
                PrioritySegmentedControl(
                    selectedPriority = uiState.selectedPriority,
                    onPrioritySelected = onPrioritySelected
                )
            }
        }
    }
}

@Composable
private fun CreateTaskTopBar(
    canSubmit: Boolean,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = onCancelClick) {
            Text(
                text = "Hủy",
                fontSize = 15.sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "Việc mới",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        TextButton(
            onClick = onSaveClick,
            enabled = canSubmit
        ) {
            Text(
                text = "Lưu",
                fontSize = 15.sp,
                color = if (canSubmit) PrimaryBlue else PlaceholderGray,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TaskInputCard(
    title: String,
    titleError: String?,
    note: String,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Trường Tên công việc
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text(
                    text = "Tên công việc",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (title.isEmpty()) {
                        Text(
                            text = "Ví dụ: Ôn chương 4 – Giải thuật",
                            fontSize = 15.sp,
                            color = PlaceholderGray
                        )
                    }
                    BasicTextField(
                        value = title,
                        onValueChange = onTitleChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            color = TextDark,
                            fontWeight = FontWeight.Medium
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        cursorBrush = SolidColor(PrimaryBlue),
                        maxLines = 2
                    )
                }
                if (!titleError.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = titleError,
                        fontSize = 12.sp,
                        color = Color(0xFFE53935)
                    )
                }
            }

            HorizontalDivider(color = DividerColor, thickness = 1.dp)

            // Trường Ghi chú (không bắt buộc)
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text(
                    text = "Ghi chú",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 44.dp)
                ) {
                    if (note.isEmpty()) {
                        Text(
                            text = "Thêm ghi chú (không bắt buộc)",
                            fontSize = 14.sp,
                            color = PlaceholderGray
                        )
                    }
                    BasicTextField(
                        value = note,
                        onValueChange = onNoteChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = TextDark
                        ),
                        cursorBrush = SolidColor(PrimaryBlue),
                        maxLines = 4
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF4A5568)
    )
}

@Composable
private fun SelectableOptionButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .semantics { selected = isSelected }
            .clickable(role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) SelectedBlueBg else Color.White,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) PrimaryBlue else CardBorderColor
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) PrimaryBlue else TextDark
            )
        }
    }
}

@Composable
private fun TimeAndRemindCard(
    selectedTime: String,
    isRemindEnabled: Boolean,
    onTimeClick: () -> Unit,
    onRemindToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Hàng chọn Giờ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onTimeClick)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Giờ thực hiện",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Giờ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F4F9)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = selectedTime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = DividerColor, thickness = 1.dp)

            // Hàng bật/tắt Nhắc tôi (Trước 15 phút)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Nhắc tôi",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Nhắc tôi",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )
                        Text(
                            text = "Trước 15 phút",
                            fontSize = 12.sp,
                            color = Color(0xFF8A94A6)
                        )
                    }
                }

                Switch(
                    checked = isRemindEnabled,
                    onCheckedChange = onRemindToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ToggleGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD9DEE7),
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
private fun PrioritySegmentedControl(
    selectedPriority: TaskPriority,
    onPrioritySelected: (TaskPriority) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFEDF0F5)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TaskPriority.entries.forEach { priority ->
                val isSelected = priority == selectedPriority
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .semantics { selected = isSelected }
                        .clickable(role = Role.RadioButton) { onPrioritySelected(priority) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color.White else Color.Transparent,
                    shadowElevation = if (isSelected) 1.dp else 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = priority.label,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) TextDark else TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
