package com.example.bai02.data.model

/**
 * Lớp đại diện cho một việc nhỏ (Subtask) thuộc về một Task.
 */
data class Subtask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
) {
    fun toggleCompleted(): Subtask {
        return this.copy(isCompleted = !isCompleted)
    }
}

/**
 * Lớp đại diện cho một công việc cụ thể (OOP - Entity với hành vi).
 * Thay vì chỉ là dữ liệu thụ động, Task tự quản lý hành vi thay đổi trạng thái của chính nó.
 */
data class Task(
    val id: String,
    val title: String,
    val category: TaskCategory,
    val time: String,
    val date: String? = null,
    val isUrgent: Boolean = false,
    val isCompleted: Boolean = false,
    val note: String? = null,
    val priorityText: String = "Ưu tiên vừa",
    val remindText: String = "Trước 15 phút",
    val subtasks: List<Subtask> = emptyList()
) {
    /**
     * Thuộc tính tiện ích ghép ngày và giờ hiển thị trên giao diện thẻ công việc.
     * Ví dụ: "Hôm nay, 14:00" hoặc "2026-10-28, 09:00" hoặc chỉ "14:00" nếu không có ngày.
     */
    val formattedDateTime: String
        get() {
            return if (!date.isNullOrBlank()) {
                "$date, $time"
            } else {
                time
            }
        }

    /**
     * Tỉ lệ hoàn thành của các việc nhỏ (0.0f đến 1.0f).
     * Tránh lỗi chia cho 0 nếu danh sách việc nhỏ rỗng.
     */
    val subtasksProgressRatio: Float
        get() = if (subtasks.isNotEmpty()) {
            subtasks.count { it.isCompleted }.toFloat() / subtasks.size
        } else 0f

    /**
     * Chuỗi hiển thị tỉ lệ việc nhỏ, ví dụ: "1/2"
     */
    val subtasksProgressText: String
        get() = "${subtasks.count { it.isCompleted }}/${subtasks.size}"

    /**
     * Phương thức đóng gói hành vi (Encapsulation):
     * Trả về một bản sao mới với trạng thái hoàn thành được đảo ngược (Immutable Object Pattern).
     */
    fun toggleCompleted(): Task {
        return this.copy(isCompleted = !isCompleted)
    }

    /**
     * Đảo trạng thái của một subtask con theo id
     */
    fun toggleSubtask(subtaskId: String): Task {
        val updated = subtasks.map { subtask ->
            if (subtask.id == subtaskId) subtask.toggleCompleted() else subtask
        }
        return this.copy(subtasks = updated)
    }

    /**
     * Kiểm tra xem công việc này có thuộc về danh mục cụ thể hay không.
     */
    fun belongsTo(categoryId: String): Boolean {
        return this.category.id == categoryId
    }
}


