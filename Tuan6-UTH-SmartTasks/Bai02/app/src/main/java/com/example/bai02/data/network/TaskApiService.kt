package com.example.bai02.data.network

import com.example.bai02.data.model.TaskDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface định nghĩa các Endpoints HTTP giao tiếp với Server.
 * Retrofit sẽ tự động sinh code thực thi cho interface này.
 */
interface TaskApiService {

    /**
     * Lấy danh sách công việc từ server
     * Endpoint: https://share.tryapps.store/mock/teach/tasks
     *
     * @param status Dùng để mô phỏng test lỗi server (ví dụ: 500) hoặc thành công (200)
     * @param delay Dùng để mô phỏng độ trễ mạng (ví dụ: "3s", "1s")
     */
    @GET("mock/teach/tasks")
    suspend fun getTasks(
        @Query("status") status: Int? = null,
        @Query("delay") delay: String? = null
    ): List<TaskDto>
}