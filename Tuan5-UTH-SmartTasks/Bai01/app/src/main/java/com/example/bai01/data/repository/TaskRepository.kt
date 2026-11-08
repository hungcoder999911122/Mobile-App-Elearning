package com.example.bai01.data.repository

import com.example.bai01.data.model.HomeUiState
import com.example.bai01.data.model.Task
import com.example.bai01.data.model.TaskCategory
import com.example.bai01.data.model.UserProfile

/**
 * Interface Repository (OOP - Tính trừu tượng Abstraction & Nguyên lý Dependency Inversion trong SOLID).
 * Định nghĩa hợp đồng cung cấp dữ liệu cho màn hình Home.
 * Sau này khi tích hợp Firebase Firestore hay Room Database, bạn chỉ cần tạo class
 * mới implement interface này mà không cần sửa đổi bất kỳ dòng code giao diện nào!
 */
interface TaskRepository {
    fun getInitialHomeUiState(): HomeUiState
}

/**
 * Lớp triển khai giả lập (Fake/Mock Implementation) dùng trong giai đoạn phát triển giao diện.
 */
class FakeTaskRepository : TaskRepository {

    override fun getInitialHomeUiState(): HomeUiState {
        val catHocTap = TaskCategory.HOC_TAP
        val catCuocSong = TaskCategory.CUOC_SONG

        // 7 công việc chuẩn khớp 100% với số liệu trên thiết kế (3/7 việc đã xong = 43%)
        val initialTasks = listOf(
            Task("1", "Nộp bài tập Lập trình di động", catHocTap, "09:00", isCompleted = true),
            Task("2", "Ôn chương 3 – Cấu trúc dữ liệu", catHocTap, "14:00", isCompleted = false),
            Task("3", "Đi chợ mua đồ cho tuần", catCuocSong, "17:30", isCompleted = false),
            Task("4", "Tập thể dục 30 phút", catCuocSong, "06:30", isCompleted = true),
            Task("5", "Làm bài tập trắc nghiệm", catHocTap, "16:00", isCompleted = false),
            Task("6", "Đọc sách 20 trang", catHocTap, "21:00", isCompleted = false),
            Task("7", "Dọn dẹp góc học tập", catHocTap, "22:00", isCompleted = false)
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
}

