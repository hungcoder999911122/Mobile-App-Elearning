# 📱 UTH SmartTasks – Màn hình Thêm Công Việc (MVVM & API)

## 1. Giới thiệu

Dự án tiếp tục phát triển ứng dụng **UTH SmartTasks** bằng **Jetpack Compose**, triển khai màn hình **Thêm công việc mới (`Việc mới`)** theo kiến trúc **MVVM (Model - View - ViewModel)** kết hợp giao tiếp **REST API** qua Retrofit.

| Nội dung | Chi tiết |
| :--- | :--- |
| **Issue** | `#3` – `[Feature-10] Implement Create Task Screen using MVVM and API` |
| **Nền tảng / Ngôn ngữ** | Android / Kotlin |
| **UI & Kiến trúc** | Jetpack Compose, Material 3, MVVM (`StateFlow` + `ViewModel` + `Repository`) |
| **API Endpoint** | `POST https://share.tryapps.store/mock/teach/tasks` |
| **Nhánh thực hiện** | `feature/issue-2-notification-mvvm` |

---

## 2. Tính năng & Giao diện (Theo mẫu Tuần 04)

- **Thanh tiêu đề:** Nút **Hủy** (quay lại không lưu), tiêu đề **Việc mới**, nút **Lưu** (chỉ kích hoạt khi dữ liệu hợp lệ).
- **Thông tin công việc:**
  - **Tên công việc** (bắt buộc, hiển thị thông báo lỗi nếu bỏ trống).
  - **Ghi chú** (không bắt buộc).
- **Phân loại & Lịch trình:**
  - **Nhóm:** `Học tập` (mặc định) / `Cuộc sống`.
  - **Ngày:** `Hôm nay` (mặc định) / `Ngày mai` / `Tuần này`.
  - **Giờ:** Mặc định `19:00`, hỗ trợ chọn giờ qua Material 3 `TimePicker`.
  - **Nhắc tôi:** Công tắc bật/tắt (mặc định bật, nhắc `Trước 15 phút`).
  - **Mức ưu tiên:** Segmented control `Thấp` | `Vừa` (mặc định) | `Cao`.
- **Xử lý gửi dữ liệu:** Nút **Lưu công việc** hiển thị trạng thái loading khi đang gửi, chặn bấm trùng (`isSubmitting`), và hiển thị `Snackbar` nếu API trả về lỗi.

---

## 3. Kiến trúc MVVM & Luồng dữ liệu

```text
CreateTaskScreen (View)
   │  Sự kiện nhập liệu / chọn tùy chọn / bấm Lưu
   ▼
CreateTaskViewModel (ViewModel)
   │  Quản lý StateFlow<CreateTaskUiState>, validate, chống double-submit
   ▼
NetworkTaskRepository (Repository)
   │  Gọi POST /mock/teach/tasks & đồng bộ vào cachedTasks (ID duy nhất)
   ▼
TaskApiService (Retrofit) ──▶ https://share.tryapps.store/mock/teach/tasks
```

> **Lưu ý về Mock Server:** Vì server mock không lưu trữ database thật, sau khi `POST` thành công (`response.isSuccessful`), `NetworkTaskRepository` tạo `Task` từ chính dữ liệu người dùng nhập với ID duy nhất và thêm vào `cachedTasks` để `HomeScreen` và `TaskListScreen` hiển thị ngay công việc mới.

---

## 4. Tổ chức mã nguồn chính

```text
app/src/
├── main/java/com/example/bai02/
│   ├── data/
│   │   ├── model/
│   │   │   ├── CreateTaskUiState.kt      # UI State, TaskPriority, TaskDateOption
│   │   │   └── TaskDto.kt                # CreateTaskRequest & mapper toDomainTask()
│   │   ├── network/
│   │   │   └── TaskApiService.kt         # Endpoint @POST("mock/teach/tasks")
│   │   └── repository/
│   │       └── TaskRepository.kt         # createTask() cho Network & Fake Repository
│   ├── navigation/
│   │   └── AppNavigation.kt              # Route AppScreen.CreateTask & làm mới Home
│   └── ui/screens/create/
│       ├── CreateTaskViewModel.kt        # Xử lý logic form, validation & gọi API
│       └── CreateTaskScreen.kt           # Giao diện Việc mới & TimePickerDialog
└── test/java/com/example/bai02/
    └── CreateTaskUnitTest.kt             # Unit test UI state, repository & mapper
```

---

## 5. Kiểm thử

### Kiểm thử tự động (Unit Test)
Chạy lệnh tại thư mục `Assignments/Tuan07/Bai02`:

```bash
./gradlew :app:testDebugUnitTest --no-configuration-cache
```

`CreateTaskUnitTest.kt` kiểm tra:
- Giá trị mặc định của `CreateTaskUiState` khớp thiết kế Tuần 04.
- Logic `isFormValid` và `canSubmit` khi tên rỗng hoặc đang `isSubmitting`.
- `FakeTaskRepository.createTask()` và `TaskDto.toDomainTask()` ánh xạ đúng các trường dữ liệu.

### Kiểm thử thủ công
1. Mở ứng dụng $\rightarrow$ vào **Trang chủ** $\rightarrow$ nhấn nút **+** để mở màn hình **Việc mới**.
2. Để trống tên công việc: nút **Lưu** / **Lưu công việc** bị vô hiệu hóa.
3. Nhập tên công việc, chọn nhóm/ngày/giờ/nhắc nhở/ưu tiên $\rightarrow$ nhấn **Lưu công việc**.
4. Xác nhận quay lại **Trang chủ** và công việc mới hiển thị ngay trong danh sách.
