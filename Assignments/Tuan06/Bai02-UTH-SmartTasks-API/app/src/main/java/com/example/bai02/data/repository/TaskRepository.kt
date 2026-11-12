package com.example.bai02.data.repository

import com.example.bai02.data.model.HomeUiState
import com.example.bai02.data.model.Subtask
import com.example.bai02.data.model.Task
import com.example.bai02.data.model.TaskCategory
import com.example.bai02.data.model.UserProfile
import com.example.bai02.data.model.toDomainTask
import com.example.bai02.data.network.RetrofitClient
import com.example.bai02.data.network.TaskApiService

/**
 * Interface Repository (OOP - Tính trừu tượng Abstraction & Dependency Inversion trong SOLID).
 * Định nghĩa hợp đồng cung cấp dữ liệu cho toàn bộ ứng dụng.
 */
interface TaskRepository {
    fun getInitialHomeUiState(): HomeUiState

    /**
     * Lấy danh sách công việc (bất đồng bộ qua mạng).
     */
    suspend fun getTasks(status: Int? = null, delay: String? = null): Result<List<Task>>

    /**
     * Lấy thông tin chi tiết một công việc theo ID.
     */
    suspend fun getTaskDetail(id: String): Result<Task>

    /**
     * Cập nhật trạng thái hoàn thành (done) của công việc (PATCH /mock/teach/tasks/{id})
     */
    suspend fun updateTaskDone(id: String, isDone: Boolean): Result<Task>

    /**
     * Cập nhật các trường thông tin của công việc (PATCH /mock/teach/tasks/{id})
     */
    suspend fun updateTask(id: String, fields: Map<String, Any?>): Result<Task>

    /**
     * Xóa công việc theo ID (DELETE /mock/teach/tasks/{id})
     */
    suspend fun deleteTask(id: String): Result<Unit>
}

/**
 * 1. Triển khai kết nối API thực tế qua mạng (Retrofit)
 * Áp dụng mô hình Repository Pattern kết hợp In-Memory Cache (Single Source of Truth).
 * Vừa gọi mạng thật (GET, PATCH, DELETE) vừa duy trì trạng thái dữ liệu nhất quán,
 * khắc phục hạn chế máy chủ Mock không lưu trữ database thật.
 */
class NetworkTaskRepository(
    private val apiService: TaskApiService = RetrofitClient.taskApiService
) : TaskRepository {

    companion object {
        // Bộ nhớ đệm danh sách công việc dùng chung giữa các màn hình trong ứng dụng
        private val cachedTasks = mutableListOf<Task>()
        private var isInitialized = false
    }

    override fun getInitialHomeUiState(): HomeUiState {
        return HomeUiState(
            user = UserProfile(fullName = "UTH", greetingName = "UTH!", customInitials = "MA"),
            dateText = "Thứ Ba, 20 tháng 10",
            hasUnreadNotifications = true,
            categories = TaskCategory.getDefaultCategories(),
            tasks = cachedTasks.toList()
        )
    }

    override suspend fun getTasks(status: Int?, delay: String?): Result<List<Task>> {
        return try {
            // Nếu chưa khởi tạo hoặc khi có yêu cầu status/delay cụ thể thì gọi mạng tải về
            if (!isInitialized || status != null) {
                val dtoList = apiService.getTasks(status = status, delay = delay)
                val domainTasks = dtoList.map { it.toDomainTask() }
                cachedTasks.clear()
                cachedTasks.addAll(domainTasks)
                isInitialized = true
            }
            Result.success(cachedTasks.toList())
        } catch (e: Exception) {
            // Nếu mất mạng nhưng trong cache đã có dữ liệu trước đó, vẫn trả về cache
            if (cachedTasks.isNotEmpty()) {
                Result.success(cachedTasks.toList())
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getTaskDetail(id: String): Result<Task> {
        return try {
            // 1. Gửi request GET /mock/teach/tasks/{id} thật lên server
            val dto = try {
                apiService.getTaskDetail(id)
            } catch (e: Exception) {
                null
            }

            // 2. Tra cứu trong cache để giữ trạng thái mới nhất do người dùng thay đổi
            val cached = cachedTasks.find { it.id == id }

            val task = if (cached != null) {
                if (dto != null) {
                    val fromDto = dto.toDomainTask()
                    cached.copy(
                        note = if (!cached.note.isNullOrBlank()) cached.note else fromDto.note,
                        subtasks = if (cached.subtasks.isNotEmpty()) cached.subtasks else fromDto.subtasks
                    )
                } else {
                    cached
                }
            } else if (dto != null) {
                val fromDto = dto.toDomainTask()
                cachedTasks.add(fromDto)
                fromDto
            } else {
                throw Exception("Không tìm thấy công việc với ID: $id")
            }

            Result.success(task)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTaskDone(id: String, isDone: Boolean): Result<Task> {
        return updateTask(id, mapOf("done" to isDone))
    }

    override suspend fun updateTask(id: String, fields: Map<String, Any?>): Result<Task> {
        return try {
            // 1. GỬI REQUEST HTTP PATCH THẬT LÊN SERVER
            try {
                apiService.updateTask(id, fields)
            } catch (e: Exception) {
                // Log lỗi mạng nếu có nhưng tiếp tục đồng bộ cache
            }

            // 2. CẬP NHẬT CHÍNH XÁC VÀO LOCAL CACHE
            val index = cachedTasks.indexOfFirst { it.id == id }
            if (index != -1) {
                var task = cachedTasks[index]
                if (fields.containsKey("done")) {
                    task = task.copy(isCompleted = fields["done"] as? Boolean ?: task.isCompleted)
                }
                if (fields.containsKey("title")) {
                    task = task.copy(title = fields["title"] as? String ?: task.title)
                }
                if (fields.containsKey("note")) {
                    task = task.copy(note = fields["note"] as? String)
                }
                if (fields.containsKey("subtasks")) {
                    @Suppress("UNCHECKED_CAST")
                    val subDtoList = fields["subtasks"] as? List<com.example.bai02.data.model.SubtaskDto>
                    if (subDtoList != null) {
                        task = task.copy(subtasks = subDtoList.mapIndexed { idx, s ->
                            com.example.bai02.data.model.Subtask(
                                id = s.id?.toString() ?: idx.toString(),
                                title = s.title ?: "",
                                isCompleted = s.done == true
                            )
                        })
                    }
                }
                cachedTasks[index] = task
                Result.success(task)
            } else {
                Result.failure(Exception("Không tìm thấy công việc trong cache với ID: $id"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(id: String): Result<Unit> {
        return try {
            // 1. GỬI REQUEST HTTP DELETE THẬT LÊN SERVER
            try {
                apiService.deleteTask(id)
            } catch (e: Exception) {
                // Tiếp tục xóa ở cache
            }

            // 2. XÓA KHỎI CACHE ĐỂ ĐỒNG BỘ TOÀN BỘ ỨNG DỤNG
            cachedTasks.removeAll { it.id == id }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * 2. Triển khai giả lập (Fake/Mock) dùng khi cần test giao diện độc lập không cần mạng
 */
class FakeTaskRepository : TaskRepository {

    private val fakeTasks = mutableListOf(
        Task(
            id = "1",
            title = "Ôn chương 3 – Cấu trúc dữ liệu",
            category = TaskCategory.HOC_TAP,
            time = "14:00",
            date = "Hôm nay",
            isCompleted = false,
            note = "Tập trung phần cây nhị phân và bảng băm.",
            priorityText = "Ưu tiên vừa",
            remindText = "Trước 15 phút",
            subtasks = listOf(
                Subtask("101", "Đọc lại slide bài giảng", isCompleted = true),
                Subtask("102", "Làm 10 bài tập cuối chương", isCompleted = false)
            )
        ),
        Task(
            id = "2",
            title = "Đi chợ mua đồ cho tuần",
            category = TaskCategory.CUOC_SONG,
            time = "17:30",
            date = "Hôm nay",
            isCompleted = false,
            note = "Mua rau củ, trái cây và gia vị.",
            subtasks = listOf(
                Subtask("201", "Mua thịt và trứng", isCompleted = false),
                Subtask("202", "Mua sữa tươi", isCompleted = true)
            )
        ),
        Task("3", "Gọi điện về nhà", TaskCategory.CUOC_SONG, "21:00", date = "Ngày mai", isCompleted = false),
        Task("4", "Họp nhóm đồ án", TaskCategory.HOC_TAP, "20:00", date = "Tuần này", isUrgent = true, isCompleted = false),
        Task("5", "Tập thể dục 30 phút", TaskCategory.CUOC_SONG, "06:30", date = "Hôm nay", isCompleted = true),
        Task("6", "Nộp bài tập Lập trình di động", TaskCategory.HOC_TAP, "09:00", date = "Hôm nay", isCompleted = true)
    )

    override fun getInitialHomeUiState(): HomeUiState {
        return HomeUiState(
            user = UserProfile(
                fullName = "UTH",
                greetingName = "UTH!",
                customInitials = "MA"
            ),
            dateText = "Thứ Ba, 20 tháng 10",
            hasUnreadNotifications = true,
            categories = listOf(TaskCategory.HOC_TAP, TaskCategory.CUOC_SONG),
            tasks = fakeTasks.toList()
        )
    }

    override suspend fun getTasks(status: Int?, delay: String?): Result<List<Task>> {
        return Result.success(fakeTasks.toList())
    }

    override suspend fun getTaskDetail(id: String): Result<Task> {
        val task = fakeTasks.find { it.id == id }
        return if (task != null) {
            Result.success(task)
        } else {
            Result.failure(Exception("Không tìm thấy công việc với ID: $id"))
        }
    }

    override suspend fun updateTaskDone(id: String, isDone: Boolean): Result<Task> {
        val index = fakeTasks.indexOfFirst { it.id == id }
        return if (index != -1) {
            val updated = fakeTasks[index].copy(isCompleted = isDone)
            fakeTasks[index] = updated
            Result.success(updated)
        } else {
            Result.failure(Exception("Không tìm thấy công việc với ID: $id"))
        }
    }

    override suspend fun updateTask(id: String, fields: Map<String, Any?>): Result<Task> {
        val index = fakeTasks.indexOfFirst { it.id == id }
        return if (index != -1) {
            var task = fakeTasks[index]
            if (fields.containsKey("done")) {
                task = task.copy(isCompleted = fields["done"] as? Boolean ?: task.isCompleted)
            }
            if (fields.containsKey("title")) {
                task = task.copy(title = fields["title"] as? String ?: task.title)
            }
            if (fields.containsKey("note")) {
                task = task.copy(note = fields["note"] as? String)
            }
            fakeTasks[index] = task
            Result.success(task)
        } else {
            Result.failure(Exception("Không tìm thấy công việc với ID: $id"))
        }
    }

    override suspend fun deleteTask(id: String): Result<Unit> {
        val removed = fakeTasks.removeAll { it.id == id }
        return if (removed) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Không tìm thấy công việc để xóa với ID: $id"))
        }
    }
}

