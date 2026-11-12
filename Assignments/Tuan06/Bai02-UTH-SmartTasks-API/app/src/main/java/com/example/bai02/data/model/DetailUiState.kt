package com.example.bai02.data.model

/**
 * State holder đại diện cho trạng thái của màn hình DetailScreen (OOP - State Pattern).
 */
data class DetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val isDeleted: Boolean = false,
    val showCompletionToast: Boolean = false,
    val isAddSubtaskDialogOpen: Boolean = false
) {
    /**
     * Kiểm tra xem task hiện tại đã hoàn thành hay chưa
     */
    val isTaskCompleted: Boolean
        get() = task?.isCompleted == true
}

