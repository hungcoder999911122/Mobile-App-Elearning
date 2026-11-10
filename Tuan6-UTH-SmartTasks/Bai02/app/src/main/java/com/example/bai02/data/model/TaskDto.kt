package com.example.bai02.data.model

import com.google.gson.annotations.SerializedName

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
    val subtasks: List<Any>? = null
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
    val isUrgent = this.priority?.trim()?.lowercase() in listOf("high", "urgent", "gấp", "gap")

    return Task(
        id = this.id.toString(),
        title = this.title,
        category = domainCategory,
        time = this.time,
        date = this.date,
        isUrgent = isUrgent,
        isCompleted = this.done
    )
}

