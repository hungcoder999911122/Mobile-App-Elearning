package com.example.bai02.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO đại diện cho một việc nhỏ (subtask) trong JSON response
 */
data class SubtaskDto(
    @SerializedName("id")
    val id: Any? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("done")
    val done: Boolean? = null
)

/**
 * 1. DTO (Data Transfer Object): Đại diện cho dữ liệu JSON nhận từ API /mock/teach/tasks.
 */
data class TaskDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName(value = "des", alternate = ["note"])
    val des: String? = null,

    @SerializedName("category")
    val category: String,

    @SerializedName("date")
    val date: String? = null,

    @SerializedName("time")
    val time: String,

    @SerializedName("priority")
    val priority: String? = null,

    @SerializedName("remind")
    val remind: Boolean? = null,

    @SerializedName("done")
    val done: Boolean,

    @SerializedName("subtasks")
    val subtasks: List<SubtaskDto>? = null
)

/**
 * Request body cho phương thức PATCH /mock/teach/tasks/{id}
 */
data class UpdateTaskRequest(
    @SerializedName("done")
    val done: Boolean? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("note")
    val note: String? = null,

    @SerializedName("priority")
    val priority: String? = null,

    @SerializedName("subtasks")
    val subtasks: List<SubtaskDto>? = null
)

/**
 * 2. HÀM MAPPER (Extension Function):
 * Chuyển đổi một đối tượng TaskDto nhận từ API thành đối tượng Task dùng cho giao diện Compose.
 */
fun TaskDto.toDomainTask(): Task {
    // 1. Nhận diện danh mục từ chuỗi string trả về từ server
    val domainCategory = when (this.category.trim().lowercase()) {
        "study", "học tập" -> TaskCategory.HOC_TAP
        else -> TaskCategory.CUOC_SONG
    }

    // 2. Nhận diện công việc gấp (priority = high/urgent/gấp)
    val priorityLower = this.priority?.trim()?.lowercase() ?: ""
    val isUrgent = priorityLower in listOf("high", "urgent", "gấp", "gap")

    val priorityDisplay = when {
        priorityLower in listOf("high", "urgent", "gấp", "gap") -> "Ưu tiên cao"
        priorityLower in listOf("low", "thấp", "thap") -> "Ưu tiên thấp"
        else -> "Ưu tiên vừa"
    }

    val remindDisplay = if (this.remind == false) "Không nhắc" else "Trước 15 phút"

    val domainSubtasks = this.subtasks?.mapIndexed { index, subDto ->
        Subtask(
            id = subDto.id?.toString() ?: (index + 1).toString(),
            title = subDto.title ?: "Việc nhỏ ${index + 1}",
            isCompleted = subDto.done == true
        )
    } ?: emptyList()

    return Task(
        id = this.id.toString(),
        title = this.title,
        category = domainCategory,
        time = this.time,
        date = this.date,
        isUrgent = isUrgent,
        isCompleted = this.done,
        note = this.des,
        priorityText = priorityDisplay,
        remindText = remindDisplay,
        subtasks = domainSubtasks
    )
}


