package com.example.bai02.ui.screens.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bai02.data.model.CreateTaskRequest
import com.example.bai02.data.model.CreateTaskUiState
import com.example.bai02.data.model.TaskCategory
import com.example.bai02.data.model.TaskDateOption
import com.example.bai02.data.model.TaskPriority
import com.example.bai02.data.repository.NetworkTaskRepository
import com.example.bai02.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * ViewModel quản lý trạng thái form, validation và gọi API tạo công việc mới
 * cho màn hình CreateTaskScreen (kiến trúc MVVM).
 */
class CreateTaskViewModel(
    private val repository: TaskRepository = NetworkTaskRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.update { current ->
            current.copy(
                title = newTitle,
                titleError = if (newTitle.isNotBlank()) null else current.titleError,
                errorMessage = null
            )
        }
    }

    fun onNoteChange(newNote: String) {
        _uiState.update { it.copy(note = newNote, errorMessage = null) }
    }

    fun onCategorySelected(category: TaskCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onDateSelected(date: TaskDateOption) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        val formattedTime = String.format(Locale.US, "%02d:%02d", hour, minute)
        _uiState.update { it.copy(selectedTime = formattedTime) }
    }

    fun onRemindToggle(enabled: Boolean) {
        _uiState.update { it.copy(isRemindEnabled = enabled) }
    }

    fun onPrioritySelected(priority: TaskPriority) {
        _uiState.update { it.copy(selectedPriority = priority) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetForm() {
        _uiState.value = CreateTaskUiState()
    }

    /**
     * Kiểm tra dữ liệu hợp lệ và gửi yêu cầu tạo công việc lên API (POST /mock/teach/tasks).
     */
    fun submitTask() {
        val currentState = _uiState.value

        // 1. Chống gửi trùng khi request trước đó đang xử lý
        if (currentState.isSubmitting) return

        // 2. Kiểm tra tên công việc không được để trống
        val trimmedTitle = currentState.title.trim()
        if (trimmedTitle.isEmpty()) {
            _uiState.update {
                it.copy(titleError = "Vui lòng nhập tên công việc")
            }
            return
        }

        val trimmedNote = currentState.note.trim().takeIf { it.isNotEmpty() }
        val request = CreateTaskRequest(
            title = trimmedTitle,
            des = trimmedNote,
            category = currentState.selectedCategory.name,
            date = currentState.selectedDate.label,
            time = currentState.selectedTime,
            priority = currentState.selectedPriority.apiValue,
            remind = currentState.isRemindEnabled,
            done = false
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    titleError = null,
                    errorMessage = null
                )
            }

            val result = repository.createTask(request)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = exception.localizedMessage
                            ?: "Không thể tạo công việc. Vui lòng thử lại!"
                    )
                }
            }
        }
    }
}