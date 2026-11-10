package com.example.bai02.data.network

import com.example.bai02.data.model.TaskDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface định nghĩa các Endpoints HTTP giao tiếp với Server.
 * Retrofit sẽ tự động sinh code thực thi cho interface này.
 */
interface TaskApiService {

    /**
     * Lấy danh sách công việc từ server
     * Endpoint: GET https://share.tryapps.store/mock/teach/tasks
     */
    @GET("mock/teach/tasks")
    suspend fun getTasks(
        @Query("status") status: Int? = null,
        @Query("delay") delay: String? = null
    ): List<TaskDto>

    /**
     * Lấy thông tin chi tiết một công việc theo ID
     * Endpoint: GET https://share.tryapps.store/mock/teach/tasks/{id}
     */
    @GET("mock/teach/tasks/{id}")
    suspend fun getTaskDetail(
        @Path("id") id: String,
        @Query("delay") delay: String? = null
    ): TaskDto

    /**
     * Cập nhật một phần thông tin công việc theo ID
     * Endpoint: PATCH https://share.tryapps.store/mock/teach/tasks/{id}
     */
    @PATCH("mock/teach/tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): TaskDto

    /**
     * Xóa công việc theo ID
     * Endpoint: DELETE https://share.tryapps.store/mock/teach/tasks/{id}
     */
    @DELETE("mock/teach/tasks/{id}")
    suspend fun deleteTask(
        @Path("id") id: String
    ): Response<Unit>
}
