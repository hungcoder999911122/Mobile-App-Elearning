package com.example.bai02

import com.example.bai02.data.model.CreateTaskRequest
import com.example.bai02.data.model.CreateTaskUiState
import com.example.bai02.data.model.TaskCategory
import com.example.bai02.data.model.TaskDateOption
import com.example.bai02.data.model.TaskDto
import com.example.bai02.data.model.TaskPriority
import com.example.bai02.data.model.toDomainTask
import com.example.bai02.data.repository.FakeTaskRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateTaskUnitTest {

    @Test
    fun initialUiState_matchesWeek04DefaultSelections() {
        val state = CreateTaskUiState()

        assertEquals("", state.title)
        assertEquals("", state.note)
        assertEquals(TaskCategory.HOC_TAP, state.selectedCategory)
        assertEquals(TaskDateOption.TODAY, state.selectedDate)
        assertEquals("19:00", state.selectedTime)
        assertTrue(state.isRemindEnabled)
        assertEquals(TaskPriority.MEDIUM, state.selectedPriority)
        assertNull(state.titleError)
        assertFalse(state.isSubmitting)
        assertFalse(state.isSuccess)
        assertFalse(state.isFormValid)
        assertFalse(state.canSubmit)
    }

    @Test
    fun uiState_canSubmitIsTrueOnlyWhenTitleNotBlankAndNotSubmitting() {
        val blankTitleState = CreateTaskUiState(title = "   ")
        assertFalse(blankTitleState.isFormValid)
        assertFalse(blankTitleState.canSubmit)

        val validState = CreateTaskUiState(title = "Ôn chương 4 – Giải thuật")
        assertTrue(validState.isFormValid)
        assertTrue(validState.canSubmit)

        val submittingState = validState.copy(isSubmitting = true)
        assertTrue(submittingState.isFormValid)
        assertFalse(submittingState.canSubmit)
    }

    @Test
    fun fakeRepository_createTask_addsTaskAndMapsFieldsCorrectly() = runBlocking {
        val repository = FakeTaskRepository()
        val request = CreateTaskRequest(
            title = "Ôn chương 4 – Giải thuật",
            des = "Làm 5 bài tập quy hoạch động",
            category = TaskCategory.HOC_TAP.name,
            date = TaskDateOption.TOMORROW.label,
            time = "20:30",
            priority = TaskPriority.HIGH.apiValue,
            remind = true,
            done = false
        )

        val result = repository.createTask(request)

        assertTrue(result.isSuccess)
        val createdTask = result.getOrThrow()
        assertEquals("Ôn chương 4 – Giải thuật", createdTask.title)
        assertEquals("Làm 5 bài tập quy hoạch động", createdTask.note)
        assertEquals(TaskCategory.HOC_TAP, createdTask.category)
        assertEquals("Ngày mai", createdTask.date)
        assertEquals("20:30", createdTask.time)
        assertTrue(createdTask.isUrgent)
        assertEquals("Ưu tiên cao", createdTask.priorityText)
        assertEquals("Trước 15 phút", createdTask.remindText)
        assertFalse(createdTask.isCompleted)
    }

    @Test
    fun taskDtoToDomainTask_mapsCreatedPayloadConsistently() {
        val dto = TaskDto(
            id = 99,
            title = "Đọc sách",
            des = null,
            category = "Cuộc sống",
            date = "Tuần này",
            time = "19:00",
            priority = "low",
            remind = false,
            done = false
        )

        val domain = dto.toDomainTask()

        assertEquals("99", domain.id)
        assertEquals(TaskCategory.CUOC_SONG, domain.category)
        assertEquals("Ưu tiên thấp", domain.priorityText)
        assertEquals("Không nhắc", domain.remindText)
        assertFalse(domain.isUrgent)
    }
}

