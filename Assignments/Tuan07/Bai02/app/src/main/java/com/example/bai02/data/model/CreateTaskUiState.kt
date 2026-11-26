package com.example.bai02.data.model

/**
 * Mức ưu tiên của công việc khi tạo mới.
 * [label] dùng để hiển thị trên UI ("Thấp", "Vừa", "Cao").
 * [apiValue] dùng để gửi lên server qua API ("low", "medium", "high")
 */
enum class TaskPriority(val label: String, val apiValue: String) {
    LOW(label = "Thấp", apiValue = "low"),
    MEDIUM(label = "Vừa", apiValue = "medium"),
    HIGH(label = "Cao", apiValue = "high")
}

/**
 * Các lựa chọn ngày nhanh trên màn hình Tạo công việc.
 */
enum class TaskDateOption(val label: String) {
    TODAY("Hôm nay"),
    TOMORROW("Ngày mai"),
    THIS_WEEK("Tuần này")
}


data class CreateTaskUiState(
    val title: String = "",
    val note: String = "",
    val selectedCategory: TaskCategory = TaskCategory.HOC_TAP,
    val selectedDate: TaskDateOption = TaskDateOption.TODAY,
    val selectedTime: String = "19:00",
    val isRemindEnabled: Boolean = true,
    val selectedPriority: TaskPriority = TaskPriority.MEDIUM,
    val titleError: String? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) {
    /**
     * Form hợp lệ khi tên công việc không rỗng (hoặc chỉ toàn khoảng trắng).
     */
    val isFormValid: Boolean
        get() = title.isNotBlank()

    /**
     * Chỉ cho phép gửi dữ liệu khi form hợp lệ và không có request nào đang chạy.
     */
    val canSubmit: Boolean
        get() = isFormValid && !isSubmitting
}

