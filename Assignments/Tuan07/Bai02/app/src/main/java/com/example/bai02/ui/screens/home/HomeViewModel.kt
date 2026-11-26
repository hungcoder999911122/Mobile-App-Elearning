package com.example.bai02.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bai02.data.model.HomeTab
import com.example.bai02.data.model.HomeUiState
import com.example.bai02.data.model.TaskFilterType
import com.example.bai02.data.repository.NetworkTaskRepository
import com.example.bai02.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý trạng thái dữ liệu và logic nghiệp vụ cho màn hình Home.
 * Kế thừa từ ViewModel() giúp dữ liệu sống sót qua các sự kiện cấu hình (Configuration Changes).
 */
class HomeViewModel(
    private val repository: TaskRepository = NetworkTaskRepository()
) : ViewModel() {

    // 1. Quản lý trạng thái nội bộ với MutableStateFlow (Đóng gói Encapsulation)
    private val _uiState = MutableStateFlow(repository.getInitialHomeUiState())

    // 2. StateFlow chỉ đọc công khai cho UI Composable quan sát (Observer Pattern)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Tự động tải danh sách công việc từ API ngay khi ViewModel được khởi tạo
        fetchTasks()
    }

    /**
     * Gọi API lấy danh sách công việc từ server
     *
     * @param status Mô phỏng mã HTTP trả về (ví dụ: 200, 500)
     * @param delay Mô phỏng độ trễ (ví dụ: "3s", "none")
     */
    fun fetchTasks(status: Int? = null, delay: String? = null) {
        viewModelScope.launch {
            // Bước 1: Bật trạng thái Loading và xóa thông báo lỗi cũ
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Bước 2: Gọi Repository (chạy bất đồng bộ qua Coroutines)
            val result = repository.getTasks(status = status, delay = delay)

            // Bước 3: Cập nhật UI State dựa trên kết quả trả về
            result.onSuccess { fetchedTasks ->
                _uiState.update { currentState ->
                    currentState.copy(
                        tasks = fetchedTasks,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { exception ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = exception.localizedMessage ?: "Có lỗi xảy ra khi kết nối máy chủ"
                    )
                }
            }
        }
    }

    /**
     * Xử lý khi người dùng tích chọn hoàn thành công việc
     */
    fun onToggleTask(taskId: String) {
        val currentTask = _uiState.value.tasks.find { it.id == taskId }
        val newDone = if (currentTask != null) !currentTask.isCompleted else true

        // Cập nhật UI ngay lập tức
        _uiState.update { it.toggleTask(taskId) }

        // Gọi PATCH lên server và đồng bộ repository
        viewModelScope.launch {
            repository.updateTaskDone(taskId, newDone)
        }
    }

    /**
     * Xử lý chuyển tab dưới đáy màn hình
     */
    fun onSelectTab(tab: HomeTab) {
        _uiState.update { it.selectTab(tab) }
    }

    /**
     * Xử lý khi người dùng nhập từ khóa tìm kiếm
     */
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.updateSearchQuery(query) }
    }

    /**
     * Xử lý khi người dùng chọn chip lọc danh mục
     */
    fun onSelectFilter(filter: TaskFilterType) {
        _uiState.update { it.updateFilter(filter) }
    }
}

