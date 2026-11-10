package com.example.bai02.data.repository

import com.example.bai02.data.model.HomeUiState
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
     * @return Result chứa danh sách Task nếu thành công, hoặc chứa Exception nếu thất bại.
     */
    suspend fun getTasks(status: Int? = null, delay: String? = null): Result<List<Task>>
}

/**
 * 1. Triển khai kết nối API thực tế qua mạng (Retrofit)
 */
class NetworkTaskRepository(
    private val apiService: TaskApiService = RetrofitClient.taskApiService
) : TaskRepository {

    override fun getInitialHomeUiState(): HomeUiState {
        return HomeUiState(
            user = UserProfile(fullName = "UTH", greetingName = "UTH!", customInitials = "MA"),
            dateText = "Thứ Ba, 20 tháng 10",
            hasUnreadNotifications = true,
            categories = TaskCategory.getDefaultCategories(),
            tasks = emptyList() // Trạng thái ban đầu để rỗng, chờ API tải về
        )
    }

    override suspend fun getTasks(status: Int?, delay: String?): Result<List<Task>> {
        return try {
            // 1. Gọi mạng qua Retrofit
            val dtoList = apiService.getTasks(status = status, delay = delay)
            // 2. Chuyển đổi DTO sang Domain Model Task của ứng dụng
            val domainTasks = dtoList.map { it.toDomainTask() }
            // 3. Trả về kết quả thành công
            Result.success(domainTasks)
        } catch (e: Exception) {
            // 4. Bắt toàn bộ lỗi (mất mạng, HTTP 500, lỗi parse JSON...) và trả về failure
            Result.failure(e)
        }
    }
}

/**
 * 2. Triển khai giả lập (Fake/Mock) dùng khi cần test giao diện độc lập không cần mạng
 */
class FakeTaskRepository : TaskRepository {

    override fun getInitialHomeUiState(): HomeUiState {
        val catHocTap = TaskCategory.HOC_TAP
        val catCuocSong = TaskCategory.CUOC_SONG

        val initialTasks = listOf(
            Task("1", "Ôn chương 3 – Cấu trúc dữ liệu", catHocTap, "14:00", date = "Hôm nay", isCompleted = false),
            Task("2", "Đi chợ mua đồ cho tuần", catCuocSong, "17:30", date = "Hôm nay", isCompleted = false),
            Task("3", "Gọi điện về nhà", catCuocSong, "21:00", date = "Ngày mai", isCompleted = false),
            Task("4", "Họp nhóm đồ án", catHocTap, "20:00", date = "Tuần này", isUrgent = true, isCompleted = false),
            Task("5", "Tập thể dục 30 phút", catCuocSong, "06:30", date = "Hôm nay", isCompleted = true),
            Task("6", "Nộp bài tập Lập trình di động", catHocTap, "09:00", date = "Hôm nay", isCompleted = true)
        )

        return HomeUiState(
            user = UserProfile(
                fullName = "UTH",
                greetingName = "UTH!",
                customInitials = "MA"
            ),
            dateText = "Thứ Ba, 20 tháng 10",
            hasUnreadNotifications = true,
            categories = listOf(catHocTap, catCuocSong),
            tasks = initialTasks
        )
    }

    override suspend fun getTasks(status: Int?, delay: String?): Result<List<Task>> {
        return Result.success(getInitialHomeUiState().tasks)
    }
}
