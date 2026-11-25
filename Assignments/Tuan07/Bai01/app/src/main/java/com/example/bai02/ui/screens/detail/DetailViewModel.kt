package com.example.bai02.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bai02.data.model.DetailUiState
import com.example.bai02.data.model.Subtask
import com.example.bai02.data.model.SubtaskDto
import com.example.bai02.data.repository.NetworkTaskRepository
import com.example.bai02.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý logic và trạng thái của màn hình Chi tiết công việc (DetailScreen).
 */
class DetailViewModel(
    private val repository: TaskRepository = NetworkTaskRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState(isLoading = true))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var currentTaskId: String? = null

    /**
     * Tải thông tin chi tiết của công việc từ API (GET /mock/teach/tasks/{id})
     */
    fun loadTask(taskId: String) {
        currentTaskId = taskId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.getTaskDetail(taskId)
            result.onSuccess { task ->
                _uiState.update {
                    it.copy(
                        task = task,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.localizedMessage ?: "Không thể tải thông tin công việc!"
                    )
                }
            }
        }
    }

    /**
     * Đảo trạng thái hoàn thành của công việc (PATCH /mock/teach/tasks/{id} { "done": true/false })
     * @param onCompletedCallback Gọi khi công việc vừa được chuyển từ chưa xong sang đã xong để kích hoạt Toast
     */
    fun toggleTaskDone(onCompletedCallback: () -> Unit = {}) {
        val currentTask = _uiState.value.task ?: return
        val newDoneState = !currentTask.isCompleted

        if (newDoneState) {
            // Vừa hoàn thành công việc -> Bắn sự kiện Toast thông báo
            onCompletedCallback()
        }

        // Cập nhật UI lạc quan (Optimistic UI Update) để phản hồi tức thì cho người dùng
        val optimisticTask = currentTask.toggleCompleted()
        _uiState.update { it.copy(task = optimisticTask, isUpdating = true) }

        viewModelScope.launch {
            val result = repository.updateTaskDone(currentTask.id, newDoneState)
            result.onSuccess { updatedTask ->
                _uiState.update { it.copy(task = updatedTask, isUpdating = false) }
            }.onFailure { exception ->
                // Nếu gọi API thất bại, rollback lại trạng thái cũ
                _uiState.update {
                    it.copy(
                        task = currentTask,
                        isUpdating = false,
                        errorMessage = "Cập nhật trạng thái thất bại: ${exception.localizedMessage}"
                    )
                }
            }
        }
    }

    /**
     * Đảo trạng thái của một việc nhỏ (Subtask)
     */
    fun toggleSubtask(subtaskId: String) {
        val currentTask = _uiState.value.task ?: return
        val updatedTask = currentTask.toggleSubtask(subtaskId)
        _uiState.update { it.copy(task = updatedTask) }

        // Đồng bộ lên API nếu hỗ trợ
        viewModelScope.launch {
            val subtaskDtos = updatedTask.subtasks.map {
                SubtaskDto(
                    id = it.id.toIntOrNull() ?: it.id,
                    title = it.title,
                    done = it.isCompleted
                )
            }
            repository.updateTask(currentTask.id, mapOf("subtasks" to subtaskDtos))
        }
    }

    /**
     * Thêm một việc nhỏ mới
     */
    fun addSubtask(title: String) {
        if (title.isBlank()) return
        val currentTask = _uiState.value.task ?: return

        val newId = System.currentTimeMillis().toString().takeLast(4)
        val newSubtask = Subtask(id = newId, title = title.trim(), isCompleted = false)
        val updatedSubtasks = currentTask.subtasks + newSubtask
        val updatedTask = currentTask.copy(subtasks = updatedSubtasks)

        _uiState.update { it.copy(task = updatedTask) }

        viewModelScope.launch {
            val subtaskDtos = updatedSubtasks.map {
                SubtaskDto(
                    id = it.id.toIntOrNull() ?: it.id,
                    title = it.title,
                    done = it.isCompleted
                )
            }
            repository.updateTask(currentTask.id, mapOf("subtasks" to subtaskDtos))
        }
    }

    /**
     * Chỉnh sửa tên của một việc nhỏ (Subtask) và đồng bộ qua API PATCH
     */
    fun editSubtask(subtaskId: String, newTitle: String) {
        if (newTitle.isBlank()) return
        val currentTask = _uiState.value.task ?: return

        val updatedSubtasks = currentTask.subtasks.map { subtask ->
            if (subtask.id == subtaskId) subtask.copy(title = newTitle.trim()) else subtask
        }
        val updatedTask = currentTask.copy(subtasks = updatedSubtasks)

        _uiState.update { it.copy(task = updatedTask) }

        viewModelScope.launch {
            val subtaskDtos = updatedSubtasks.map {
                SubtaskDto(
                    id = it.id.toIntOrNull() ?: it.id,
                    title = it.title,
                    done = it.isCompleted
                )
            }
            repository.updateTask(currentTask.id, mapOf("subtasks" to subtaskDtos))
        }
    }

    /**
     * Xóa một việc nhỏ
     */
    fun deleteSubtask(subtaskId: String) {
        val currentTask = _uiState.value.task ?: return
        val updatedSubtasks = currentTask.subtasks.filter { it.id != subtaskId }
        val updatedTask = currentTask.copy(subtasks = updatedSubtasks)

        _uiState.update { it.copy(task = updatedTask) }

        viewModelScope.launch {
            val subtaskDtos = updatedSubtasks.map {
                SubtaskDto(
                    id = it.id.toIntOrNull() ?: it.id,
                    title = it.title,
                    done = it.isCompleted
                )
            }
            repository.updateTask(currentTask.id, mapOf("subtasks" to subtaskDtos))
        }
    }

    /**
     * Xóa toàn bộ công việc (DELETE /mock/teach/tasks/{id})
     */
    fun deleteTask(onSuccess: () -> Unit) {
        val currentTask = _uiState.value.task ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            val result = repository.deleteTask(currentTask.id)
            result.onSuccess {
                _uiState.update { it.copy(isDeleting = false, isDeleted = true) }
                onSuccess()
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        errorMessage = "Xóa công việc thất bại: ${exception.localizedMessage}"
                    )
                }
            }
        }
    }
}

