# 📱 THỰC HÀNH 1: XÂY DỰNG CHỨC NĂNG THÔNG BÁO VỚI MVVM

## 1. Giới thiệu

Bài thực hành 1 thuộc nội dung học tập và thực hành lập trình Android với **Jetpack Compose**, tập trung tìm hiểu và áp dụng mô hình kiến trúc **MVVM (Model - View - ViewModel)** vào quá trình xây dựng giao diện người dùng trong ứng dụng quản lý công việc **UTH-SmartTasks**.

Chức năng **Thông báo** cho phép hiển thị danh sách thông báo mẫu và tự động chuyển sang trạng thái trống (**Empty View**) sau **10 giây** kể từ khi người dùng mở tab Thông báo.

### Thông tin bài tập

| Nội dung | Chi tiết |
| :--- | :--- |
| **Tên bài tập** | Thực hành 1 - Chức năng Thông báo (Issue #2) |
| **Nền tảng** | Android |
| **Ngôn ngữ** | Kotlin |
| **Công nghệ giao diện** | Jetpack Compose & Material 3 |
| **Kiến trúc** | MVVM (`StateFlow` + `ViewModel`) |
| **Nhánh thực hiện** | `feature/issue-1-notification-mvvm` |
| **Thời gian dự kiến** | 30–40 phút |
| **Nội dung chính** | Quản lý danh sách thông báo bằng ViewModel và hiển thị Empty View |

---

## 2. Mục tiêu bài thực hành

Sau khi hoàn thành bài tập, người học nắm vững và vận dụng được:
- Tổng quan về mô hình kiến trúc **MVVM** trong Android.
- Phân biệt rõ vai trò và trách nhiệm giữa **Model**, **View** và **ViewModel**.
- Quản lý trạng thái danh sách bất biến (`NotificationUiState`) bằng `StateFlow` trong `ViewModel`.
- Kết nối dữ liệu từ `ViewModel` đến Jetpack Compose thông qua `collectAsState()`.
- Xử lý bất đồng bộ với Coroutine (`viewModelScope` + `delay`) để cập nhật trạng thái tự động sau 10 giây.
- Xây dựng và chuyển đổi mượt mà giữa trạng thái danh sách có dữ liệu và giao diện **Empty View**.

---

## 3. Mô tả yêu cầu bài tập

### 3.1. Yêu cầu chính

1. **Khởi tạo danh sách thông báo trong ViewModel:**
   - `NotificationViewModel` khởi tạo sẵn danh sách 4 thông báo mẫu (`NotificationItem.getSampleNotifications()`).
   - Mỗi thông báo gồm: mã định danh (`id`), phân loại (`type`), tiêu đề (`title`), nội dung (`message`), thời gian (`timeText`) và trạng thái chưa đọc (`isUnread`).
2. **Tự động làm rỗng danh sách sau 10 giây:**
   - Ứng dụng đi qua luồng `StartScreen` → `Onboarding` → `Login` → `HomeScreen` (mặc định ở tab **Trang chủ**).
   - Khi người dùng mở tab **Thông báo** lần đầu, View kích hoạt `viewModel.onNotificationTabOpened()`.
   - `NotificationViewModel` đếm ngược **10 giây** (`10_000L`) bằng Coroutine trong `viewModelScope`, sau đó cập nhật danh sách thông báo về rỗng (`emptyList()`).
3. **Hiển thị Empty View tự động:**
   - Khi danh sách có dữ liệu: hiển thị danh sách thông báo cùng nút **"Đánh dấu đã đọc"**.
   - Khi danh sách rỗng: ẩn nút **"Đánh dấu đã đọc"** và tự động chuyển sang giao diện **Empty View**.

### 3.2. Luồng hoạt động thực tế

1. Người dùng vào `HomeScreen` và chọn tab **Thông báo** (hoặc nhấn biểu tượng chuông ở Header Trang chủ).
2. `NotificationContent` quan sát `uiState` từ `NotificationViewModel` và gọi `viewModel.onNotificationTabOpened()`.
3. Giao diện hiển thị danh sách 4 thông báo mẫu.
4. Sau 10 giây, `NotificationViewModel` cập nhật `notifications = emptyList()`.
5. `collectAsState()` nhận biết thay đổi trạng thái và tự động chuyển giao diện sang `NotificationEmptyView` với hiệu ứng `AnimatedContent`.

---

## 4. Thiết kế giao diện & Tương tác

### 4.1. Trạng thái có thông báo (List View)
- **Header:** Tiêu đề **"Thông báo"** và nút **"Đánh dấu đã đọc"** (gọi `markAllAsRead()`).
- **Danh sách 4 thông báo mẫu:**
  - **Sắp đến hạn** (`13:45`, chưa đọc — chấm xanh dương): *"Ôn chương 3 – Cấu trúc dữ liệu lúc 14:00."*
  - **Đã quá hạn** (`10:10`, chưa đọc — chấm xanh dương): *"Bài đọc tham khảo môn Mạng máy tính chưa xong."*
  - **Làm tốt lắm!** (`11:30`, đã đọc): *"Bạn đã xong 2 việc trong sáng nay."*
  - **Nhắc việc** (`Hôm qua`, đã đọc): *"Họp nhóm đồ án trong tuần này."*
- Nhấn vào từng dòng thông báo sẽ đánh dấu thông báo đó là đã đọc (`markAsRead(id)`) và đồng bộ trạng thái badge thông báo trên `TopHeaderSection` của Trang chủ.

### 4.2. Trạng thái không có thông báo (Empty View)
Khi danh sách thông báo rỗng, giao diện hiển thị:
- Biểu tượng chuông thông báo trong vòng tròn nền xanh nhạt kèm badge dấu tick xanh lá.
- Tiêu đề: **"Chưa có thông báo nào"**.
- Mô tả: *"Khi có việc sắp đến hạn, quá hạn hoặc lời nhắc, bạn sẽ thấy chúng ở đây."*
- Nút chính: **"+ Thêm việc cần làm"** (gắn callback `onAddNewTaskClick`).
- Liên kết phụ: **"Cài đặt nhắc việc"** (điều hướng sang màn hình Hồ sơ / Cài đặt).

---

## 5. Kiến trúc MVVM trong bài thực hành

### 5.1. Model
- `NotificationItem.kt`: Định nghĩa `enum class NotificationType` (`UPCOMING_DEADLINE`, `OVERDUE`, `GREAT_JOB`, `REMINDER`), `data class NotificationItem` và dữ liệu mẫu `getSampleNotifications()`.
- `NotificationUiState.kt`: Đóng gói danh sách `notifications`, các thuộc tính dẫn xuất (`isEmpty`, `hasUnread`) và các hàm biến đổi bất biến (`markAllAsRead()`, `markAsRead(notificationId)`).

### 5.2. View
- `NotificationScreen.kt`:
  - `NotificationContent`: Stateful Composable kết nối với `NotificationViewModel`, kích hoạt `onNotificationTabOpened()` qua `LaunchedEffect(Unit)` và thu thập `uiState`.
  - `NotificationScreenContent`: Stateless Composable nhận `NotificationUiState` và callback sự kiện để render `NotificationListView` hoặc `NotificationEmptyView`.
- View chỉ làm nhiệm vụ hiển thị và gửi sự kiện lên ViewModel, không tự quản lý hay thay đổi danh sách thông báo.

### 5.3. ViewModel
- `NotificationViewModel.kt`:
  - Giữ `_uiState: MutableStateFlow<NotificationUiState>` và phát `uiState: StateFlow<NotificationUiState>` chỉ đọc ra UI.
  - Quản lý Coroutine đếm ngược 10 giây trong `viewModelScope` (có kiểm tra `autoClearJob` để không chạy lại bộ đếm khi chuyển tab qua lại).
  - Xử lý logic nghiệp vụ: `onNotificationTabOpened()`, `markAllAsRead()`, `markAsRead(notificationId)`.

### 5.4. Luồng tương tác giữa các thành phần

```text
Người dùng mở tab Thông báo
        │
        ▼
NotificationContent ──(LaunchedEffect)──▶ NotificationViewModel.onNotificationTabOpened()
        │                                          │
        │ collectAsState()                         │ viewModelScope.launch { delay(10_000L) }
        ▼                                          ▼
Hiển thị NotificationListView              Sau 10 giây: cập nhật notifications = emptyList()
        │                                          │
        └────────────── StateFlow phát state mới ◀─┘
                                │
                                ▼
                  Tự động chuyển sang NotificationEmptyView
```

---

## 6. Tổ chức mã nguồn thực tế

```text
app/
└── src/
    ├── main/java/com/example/bai02/
    │   ├── data/model/
    │   │   ├── NotificationItem.kt          # Model thông báo & dữ liệu mẫu
    │   │   └── NotificationUiState.kt       # UI State & logic markAllAsRead / markAsRead
    │   └── ui/screens/
    │       ├── notifications/
    │       │   ├── NotificationViewModel.kt # ViewModel quản lý state & timer 10 giây
    │       │   └── NotificationScreen.kt    # Giao diện List View & Empty View
    │       └── home/
    │           └── homeScreens.kt           # Tích hợp tab HomeTab.NOTIFICATIONS & badge Header
    └── test/java/com/example/bai02/
        └── NotificationUnitTest.kt          # Unit test kiểm tra NotificationUiState & dữ liệu mẫu
```

---

## 7. Quy trình thực hiện

1. **Tạo Model & UI State:** Xây dựng `NotificationItem.kt` và `NotificationUiState.kt` trong `data/model/`.
2. **Xây dựng ViewModel:** Tạo `NotificationViewModel.kt` với `StateFlow` và bộ đếm `delay(10_000L)` trong `onNotificationTabOpened()`.
3. **Xây dựng Giao diện:** Tạo `NotificationScreen.kt` bám sát mockup với `NotificationListView`, `NotificationRow` và `NotificationEmptyView`.
4. **Tích hợp điều hướng:** Gắn nhánh `HomeTab.NOTIFICATIONS` vào `HomeScreenContent` và kết nối nút chuông ở `TopHeaderSection` trong `homeScreens.kt`.
5. **Viết & chạy Unit Test:** Tạo `NotificationUnitTest.kt` và kiểm tra biên dịch/test bằng Gradle.

---

## 8. Tiêu chí hoàn thành

- [x] Tạo Model đại diện cho thông báo (`NotificationItem`, `NotificationUiState`).
- [x] Xây dựng `NotificationViewModel` quản lý danh sách thông báo qua `StateFlow`.
- [x] Hiển thị danh sách 4 thông báo mẫu khi mở tab Thông báo.
- [x] Sau 10 giây, `ViewModel` tự động cập nhật danh sách về rỗng.
- [x] Giao diện tự động chuyển sang `NotificationEmptyView` mà không cần thao tác thủ công.
- [x] Tách biệt rõ trách nhiệm giữa View và ViewModel theo chuẩn MVVM.
- [x] Biên dịch và chạy Unit Test thành công (`:app:testDebugUnitTest`).

---

## 9. Kiểm thử ứng dụng

### 9.1. Kiểm thử thủ công (Manual Test Cases)

| Test Case | Thao tác | Kết quả mong đợi |
| :--- | :--- | :--- |
| **TC01** | Mở ứng dụng → vào `HomeScreen` → chọn tab **Thông báo** (hoặc nhấn icon chuông) | Màn hình Thông báo xuất hiện |
| **TC02** | Quan sát ngay khi mở tab | Hiển thị 4 thông báo mẫu và nút **"Đánh dấu đã đọc"** |
| **TC03** | Nhấn **"Đánh dấu đã đọc"** hoặc nhấn vào 1 thông báo chưa đọc | Chấm xanh chưa đọc biến mất |
| **TC04** | Chờ đủ 10 giây kể từ lúc mở tab Thông báo | ViewModel cập nhật danh sách về rỗng |
| **TC05** | Quan sát giao diện sau 10 giây | Tự động chuyển sang **Empty View** (*"Chưa có thông báo nào"*), ẩn nút *"Đánh dấu đã đọc"* |
| **TC06** | Nhấn **"Cài đặt nhắc việc"** trên Empty View | Điều hướng sang màn hình Hồ sơ / Cài đặt |

### 9.2. Kiểm thử tự động (Unit Test)
Chạy lệnh tại thư mục `Assignments/Tuan07/Bai01`:

```bash
./gradlew :app:testDebugUnitTest
```

`NotificationUnitTest.kt` kiểm tra:
- Danh sách mẫu ban đầu có 4 phần tử và `hasUnread == true`.
- `markAllAsRead()` cập nhật toàn bộ thông báo thành đã đọc (`hasUnread == false`).
- `markAsRead(id)` chỉ cập nhật đúng thông báo được chọn.
- Trạng thái danh sách rỗng trả về `isEmpty == true` và `hasUnread == false`.

---

## 10. Công nghệ sử dụng

| Công nghệ | Mục đích |
| :--- | :--- |
| **Kotlin** | Ngôn ngữ lập trình chính |
| **Jetpack Compose & Material 3** | Xây dựng giao diện khai báo hiện đại |
| **ViewModel & StateFlow** | Quản lý trạng thái giao diện theo vòng đời (MVVM) |
| **Kotlin Coroutines (`viewModelScope`)** | Xử lý đếm thời gian 10 giây bất đồng bộ |
| **JUnit 4** | Kiểm thử đơn vị (Unit Test) |
| **Android Studio** | Môi trường phát triển & kiểm thử |

---

## 11. Hướng phát triển tiếp theo

- Lưu trạng thái thông báo đã đọc/xóa vào cơ sở dữ liệu cục bộ (`Room` hoặc `DataStore`).
- Kết nối dữ liệu thông báo thời gian thực từ **Firebase Cloud Messaging (FCM)** qua tầng `Repository`.
- Bổ sung Unit Test cho `NotificationViewModel` với `kotlinx-coroutines-test` (`TestDispatcher`).

---

## 12. Tài liệu tham khảo

- [Android Developers - Guide to app architecture](https://developer.android.com/topic/architecture)
- [Android Developers - ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Android Developers - Jetpack Compose](https://developer.android.com/compose)
- [Android Developers - State in Compose](https://developer.android.com/develop/ui/compose/state)

---
- **Tác giả:** [Tên sinh viên]
- **Môn học:** Lập trình Android
- **Trường:** Đại học Giao thông vận tải TP. Hồ Chí Minh (UTH)
- **Bài thực hành:** 01 - MVVM Notification Screen (`Issue #2`)
