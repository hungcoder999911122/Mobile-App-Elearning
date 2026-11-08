package com.example.bai02.data.model

/**
 * Lớp đại diện cho một công việc cụ thể (OOP - Entity với hành vi).
 * Thay vì chỉ là dữ liệu thụ động, Task tự quản lý hành vi thay đổi trạng thái của chính nó.
 */
data class Task(
    val id: String,
    val title: String,
    val category: TaskCategory,
    val time: String,
    val isCompleted: Boolean = false
) {
    /**
     * Phương thức đóng gói hành vi (Encapsulation):
     * Trả về một bản sao mới với trạng thái hoàn thành được đảo ngược (Immutable Object Pattern).
     */
    fun toggleCompleted(): Task {
        return this.copy(isCompleted = !isCompleted)
    }

    /**
     * Kiểm tra xem công việc này có thuộc về danh mục cụ thể hay không.
     */
    fun belongsTo(categoryId: String): Boolean {
        return this.category.id == categoryId
    }
}

