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

    @SerializedName("des")
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
    // Nhận diện danh mục từ chuỗi string trả về từ server
    val domainCategory = when (this.category.trim().lowercase()) {
        "study", "học tập" -> TaskCategory.HOC_TAP
        else -> TaskCategory.CUOC_SONG
    }

    return Task(
        id = this.id.toString(),
        title = this.title,
        category = domainCategory,
        time = this.time,
        isCompleted = this.done
    )
}

