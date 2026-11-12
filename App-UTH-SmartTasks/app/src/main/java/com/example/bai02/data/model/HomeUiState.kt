package com.example.bai02.data.model

/**
 * Lớp đại diện cho toàn bộ trạng thái của HomeScreen (OOP - State Holder Pattern).
 * Đóng gói dữ liệu và cung cấp các hàm logic nghiệp vụ (Business Logic) tự động,
 * đảm bảo tính nhất quán của dữ liệu (Data Consistency).
 */
data class HomeUiState(
    val user: UserProfile = UserProfile(customInitials = "MA", fullName = "UTH", greetingName = "UTH!"),
    val dateText: String = "Thứ Ba, 20 tháng 10",
    val hasUnreadNotifications: Boolean = true,
    val categories: List<TaskCategory> = TaskCategory.getDefaultCategories(),
    val tasks: List<Task> = emptyList(),
    val selectedTab: HomeTab = HomeTab.HOME,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedFilter: TaskFilterType = TaskFilterType.ALL
) {
    /**
     * Tổng số lượng công việc (nếu danh sách mẫu có ít hơn 7 thì có thể linh hoạt tính toán).
     */
    val totalTasksCount: Int
        get() = tasks.size

    /**
     * Số lượng công việc đã hoàn thành.
     */
    val completedTasksCount: Int
        get() = tasks.count { it.isCompleted }

    /**
     * Tỉ lệ tiến độ từ 0.0f đến 1.0f cho CircularProgressIndicator.
     */
    val progressRatio: Float
        get() = if (totalTasksCount > 0) completedTasksCount.toFloat() / totalTasksCount else 0f

    /**
     * Phần trăm hoàn thành dạng số nguyên (VD: 43).
     */
    val progressPercentage: Int
        get() = (progressRatio * 100).toInt()

    /**
     * Chuỗi hiển thị tiến độ (VD: "3/7 việc").
     */
    val progressDisplayText: String
        get() = "$completedTasksCount/$totalTasksCount việc"

    /**
     * Tính toán số lượng công việc CÒN LẠI (chưa hoàn thành) của một danh mục cụ thể.
     * Khi người dùng tích chọn 1 việc, số lượng còn lại sẽ tự động giảm đi đúng chuẩn OOP!
     */
    fun getRemainingTasksCount(category: TaskCategory): Int {
        return tasks.count { it.belongsTo(category.id) && !it.isCompleted }
    }

    /**
     * Danh sách công việc sau khi đã lọc qua Bộ lọc danh mục và Từ khóa tìm kiếm.
     */
    val filteredTasks: List<Task>
        get() {
            return tasks.filter { task ->
                // 1. Kiểm tra khớp danh mục
                val matchCategory = when (selectedFilter) {
                    TaskFilterType.ALL -> true
                    TaskFilterType.STUDY -> task.belongsTo(TaskCategory.HOC_TAP.id)
                    TaskFilterType.LIFE -> task.belongsTo(TaskCategory.CUOC_SONG.id)
                }

                // 2. Kiểm tra khớp từ khóa tìm kiếm (không phân biệt chữ hoa, chữ thường)
                val matchQuery = if (searchQuery.isBlank()) {
                    true
                } else {
                    task.title.contains(searchQuery.trim(), ignoreCase = true)
                }

                matchCategory && matchQuery
            }
        }

    /**
     * Danh sách công việc CẦN LÀM (chưa hoàn thành) sau khi đã lọc.
     */
    val todoTasks: List<Task>
        get() = filteredTasks.filter { !it.isCompleted }

    /**
     * Danh sách công việc ĐÃ XONG (đã hoàn thành) sau khi đã lọc.
     */
    val completedTasks: List<Task>
        get() = filteredTasks.filter { it.isCompleted }

    /**
     * Chuỗi thống kê số lượng việc chưa xong hiển thị trên Header (ví dụ: "4 việc chưa xong").
     */
    val uncompletedCountText: String
        get() {
            val count = tasks.count { !it.isCompleted }
            return "$count việc chưa xong"
        }

    /**
     * Đảo trạng thái hoàn thành của một công việc theo id.
     * Trả về một đối tượng HomeUiState mới với danh sách đã cập nhật.
     */
    fun toggleTask(taskId: String): HomeUiState {
        val updatedTasks = tasks.map { task ->
            if (task.id == taskId) task.toggleCompleted() else task
        }
        return this.copy(tasks = updatedTasks)
    }

    /**
     * Đổi tab điều hướng.
     */
    fun selectTab(tab: HomeTab): HomeUiState {
        return this.copy(selectedTab = tab)
    }

    /**
     * Cập nhật từ khóa tìm kiếm.
     */
    fun updateSearchQuery(query: String): HomeUiState {
        return this.copy(searchQuery = query)
    }

    /**
     * Cập nhật bộ lọc danh mục.
     */
    fun updateFilter(filter: TaskFilterType): HomeUiState {
        return this.copy(selectedFilter = filter)
    }
}

