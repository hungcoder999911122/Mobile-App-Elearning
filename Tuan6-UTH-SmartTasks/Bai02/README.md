# 📁 Cấu trúc thư mục dự án

```text
com.example.bai01/
│
├── navigation/                     # Quản lý luồng điều hướng
│   ├── AppNavigation.kt            # NavHost chính & Controller quản lý Backstack
│   └── ...                         # AppScreen sealed class
│
├── ui/                             # Tầng giao diện người dùng
│   │
│   ├── theme/                      # Giao diện và phong cách ứng dụng
│   │   ├── Color.kt                # Khai báo màu sắc
│   │   ├── Theme.kt                # Cấu hình theme
│   │   └── Type.kt                 # Cấu hình kiểu chữ
│   │
│   └── screens/                    # Các màn hình ứng dụng
│       │
│       ├── onboarding/             # Khởi động và giới thiệu
│       │   ├── StartScreen.kt
│       │   └── OnboardingScreen.kt
│       │
│       ├── auth/                   # Xác thực người dùng
│       │   └── loginScreen.kt
│       │
│       ├── home/                   # Màn hình chính
│       │   └── homeScreens.kt
│       │
│       └── profile/                # Trang cá nhân và cài đặt
│           └── profileScreen.kt
│
├── data/                           # Tầng quản lý dữ liệu (Clean Architecture & OOP)
│   ├── model/                      # Các mô hình dữ liệu (Domain Entities & State)
│   │   ├── UserProfile.kt          # Đóng gói thông tin user, tính toán initials
│   │   ├── Task.kt                 # Entity công việc với hành vi toggleCompleted()
│   │   ├── TaskCategory.kt         # Danh mục công việc & giao diện màu sắc
│   │   ├── HomeTab.kt              # Enum định danh các tab điều hướng
│   │   ├── HomeUiState.kt          # State holder màn hình Home (tính % tiến độ tự động)
│   │   ├── UserStats.kt            # Chỉ số thống kê (Streak, On-time) & Biểu đồ tuần
│   │   └── ProfileUiState.kt       # State holder màn hình Profile
│   │
│   └── repository/                 # Xử lý logic truy cập dữ liệu (Abstraction & Data Source)
│       ├── AuthRepository.kt       # Quản lý xác thực Firebase Auth
│       ├── TaskRepository.kt       # Quản lý dữ liệu công việc
│       ├── ProfileRepository.kt    # Quản lý thông tin hồ sơ
│       └── RemoteConfigManager.kt  # Quản lý cấu hình từ xa Firebase Remote Config
│
├── service/                        # Dịch vụ chạy ngầm
│   └── MyFirebaseMessagingService.kt # Xử lý thông báo đẩy Firebase Cloud Messaging
│
└── MainActivity.kt                 # Activity khởi chạy ứng dụng & cấu hình Lifecycle
```

## 📝 Mô tả các thư mục chính

| Thư mục | Chức năng |
|---|---|
| `navigation/` | Quản lý điều hướng giữa các màn hình với hiệu ứng chuyển cảnh mượt mà và xử lý nút Back vật lý (`BackHandler`). |
| `ui/theme/` | Khai báo bảng màu sắc, kiểu chữ (`Type.kt`) và hệ thống Material Theme. |
| `ui/screens/` | Chứa các màn hình Jetpack Compose, tuân thủ nguyên tắc **State Hoisting** (tách biệt Stateful và Stateless). |
| `data/model/` | Định nghĩa các lớp dữ liệu, đóng gói cả thuộc tính và hành vi nghiệp vụ theo chuẩn **Lập trình hướng đối tượng (OOP)**. |
| `data/repository/` | Đóng vai trò là cầu nối (Abstraction) giữa tầng giao diện UI và các dịch vụ cơ sở dữ liệu/Firebase. |
| `service/` | Chứa Service chạy ngầm tiếp nhận sự kiện đám mây (FCM push notification). |
| `MainActivity.kt` | Điểm khởi chạy giao diện ứng dụng Android, xin quyền thông báo và khởi tạo Firebase. |

---

# 🔥 TÍCH HỢP CÁC DỊCH VỤ FIREBASE (FIREBASE INTEGRATION GUIDE)

Dự án **UTH Todo** được tích hợp **5 dịch vụ Firebase cốt lõi** phục vụ cho bài tập môn học và định hướng áp dụng vào các dự án phần mềm chuyên nghiệp:

1. **Firebase Analytics**: Thu thập, theo dõi sự kiện và hành vi tương tác của người dùng.
2. **Firebase Authentication**: Quản lý định danh, đăng nhập ẩn danh (Anonymous) và đăng nhập Google.
3. **Firebase Cloud Messaging (FCM)**: Gửi và nhận thông báo đẩy (Push Notifications) từ máy chủ về thiết bị.
4. **Firebase Crashlytics (Crash Reporting)**: Tự động ghi lại báo cáo sự cố khi ứng dụng bị văng (Crash).
5. **Firebase Remote Config**: Cập nhật câu chữ, giao diện từ xa trên web mà không cần cập nhật mã nguồn.

---

## 1. Firebase Analytics (Phân tích dữ liệu người dùng)

### 📌 Mục đích
Ghi nhận số lượng người dùng mở ứng dụng, theo dõi các sự kiện quan trọng (mở app, đăng nhập thành công) để đánh giá mức độ tương tác của người dùng.

### 📁 Các file liên quan
* `app/build.gradle.kts` (Khai báo `libs.firebase.analytics`)
* `MainActivity.kt` (Ghi nhận sự kiện `APP_OPEN`)
* `data/repository/AuthRepository.kt` (Gắn `setUserId` và sự kiện `LOGIN`)

### 💻 Cách thức tích hợp
```kotlin
// Khởi tạo và ghi sự kiện trong MainActivity.kt
val analytics = Firebase.analytics
analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN) {
    param("screen_source", "main_activity_created")
}

// Gắn User ID khi người dùng đăng nhập thành công
user?.let {
    analytics.setUserId(it.uid)
    analytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
        param(FirebaseAnalytics.Param.METHOD, "anonymous")
    }
}
```

### 🧪 Cách kiểm tra (Verification)
1. Bật chế độ xem thời gian thực (DebugView) bằng câu lệnh trong Android Studio Terminal:
   ```bash
   adb shell setprop debug.firebase.analytics.app com.example.bai01
   ```
2. Mở **Firebase Console** $\rightarrow$ **Analytics** $\rightarrow$ **DebugView**. Thao tác trên app và quan sát sự kiện `app_open`, `login` nảy theo từng giây.

---

## 2. Firebase Authentication (Xác thực người dùng)

### 📌 Mục đích
Cung cấp cơ chế đăng nhập an toàn, quản lý phiên làm việc của người dùng với 2 hình thức:
* **Dùng thử không cần tài khoản (Anonymous Auth)**: Cho phép trải nghiệm nhanh mà không cần nhập mật khẩu.
* **Tiếp tục với Google (Google Sign-In)**: Xác thực tài khoản Google liên kết.

### 📁 Các file liên quan
* `data/model/UserProfile.kt` (Entity lưu thông tin user, tính toán avatar initials)
* `data/repository/AuthRepository.kt` (Đóng gói nghiệp vụ gọi `FirebaseAuth`)
* `ui/screens/auth/loginScreen.kt` (Giao diện xử lý nút bấm, thông báo Toast thành công/thất bại)

### 💻 Kiến trúc OOP & Code mẫu
```kotlin
// AuthRepository.kt: Đóng gói đăng nhập ẩn danh
override fun signInAnonymously(onSuccess: (UserProfile) -> Unit, onError: (String) -> Unit) {
    auth.signOut() // Xóa cache cũ để đảm bảo gửi request mới
    auth.signInAnonymously()
        .addOnSuccessListener { result ->
            val user = result.user
            val profile = UserProfile(id = user?.uid ?: "guest_id", fullName = "Minh Anh")
            onSuccess(profile)
        }
        .addOnFailureListener { exception ->
            onError(exception.localizedMessage ?: "Đăng nhập thất bại!")
        }
}
```

### 🧪 Cách kiểm tra (Verification)
* **Kiểm tra đăng nhập thành công:** 
  1. Kết nối mạng $\rightarrow$ Bấm *"Dùng thử không cần tài khoản"*.
  2. Xuất hiện Toast: *"Đăng nhập thành công! Chào Minh Anh"* $\rightarrow$ Chuyển vào trang chủ.
  3. Lên **Firebase Console $\rightarrow$ Authentication $\rightarrow$ Tab Users**: Xuất hiện ngay 1 dòng chứa User UID mới.
* **Kiểm tra đăng nhập thất bại:**
  1. Bật **Chế độ máy bay (Airplane Mode)** trên máy ảo $\rightarrow$ Bấm *"Dùng thử"*.
  2. Báo lỗi ngay lập tức: *"Đăng nhập thất bại: Không có kết nối mạng!"* $\rightarrow$ Không cho vào trang chủ.
  2. Lên Firebase Console tắt tính năng Anonymous $\rightarrow$ Bấm nút $\rightarrow$ Báo lỗi nhà cung cấp bị vô hiệu hóa.

---

## 3. Firebase Cloud Messaging - FCM (Thông báo đẩy)

### 📌 Mục đích
Cho phép máy chủ Firebase gửi tin nhắn/thông báo nhắc nhở công việc đến thiết bị người dùng mọi lúc, kể cả khi ứng dụng đang đóng.

### 📁 Các file liên quan
* `app/src/main/AndroidManifest.xml` (Khai báo quyền `POST_NOTIFICATIONS` và Service)
* `service/MyFirebaseMessagingService.kt` (Service chạy ngầm tiếp nhận tin nhắn từ Firebase)
* `MainActivity.kt` (Xin quyền Runtime Permission trên Android 13+ và lấy FCM Token)

### 💻 Cơ chế hoạt động
1. **Xin quyền Runtime (Android 13+):**
   ```kotlin
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
       ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
   }
   ```
2. **Lấy mã Token của thiết bị:**
   ```kotlin
   FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
       if (task.isSuccessful) Log.d("FCM_DEBUG", "Token: ${task.result}")
   }
   ```
3. **Tiếp nhận và hiển thị thông báo (`MyFirebaseMessagingService.kt`):**
   * Sử dụng `NotificationChannel` (bắt buộc từ Android 8.0 trở lên).
   * Dùng `NotificationCompat.Builder` hiển thị thông báo có chuông và rung.

### 🧪 Cách kiểm tra (Verification)
1. **Test thiết bị đơn lẻ:** Copy mã Token từ Logcat (`FCM_DEBUG`) $\rightarrow$ Vào Firebase Console $\rightarrow$ Messaging $\rightarrow$ Bấm *Send test message* $\rightarrow$ Dán Token và gửi.
2. **Chiến dịch phát sóng (Broadcast Campaign):** Vào Firebase Console $\rightarrow$ New Campaign $\rightarrow$ Target chọn `com.example.bai01` $\rightarrow$ Bấm **Publish**. Toàn bộ máy đang cài ứng dụng đều sẽ nhận được thông báo.

---

## 4. Firebase Crashlytics (Báo cáo sự cố ứng dụng)

### 📌 Mục đích
Tự động ghi nhận thông tin sự cố khi ứng dụng bị sập (Crash / Unhandled Exceptions). Cung cấp chi tiết tên thiết bị, phiên bản Android, tên hàm và chính xác dòng code gây lỗi.

### 📁 Các file liên quan
* `build.gradle.kts` (Project-level: `libs.plugins.firebase.crashlytics`)
* `app/build.gradle.kts` (App-level: Thêm plugin Crashlytics & thư viện `libs.firebase.crashlytics`)
* `ui/screens/profile/profileScreen.kt` (Nút bấm thử nghiệm gây văng app)

### 💻 Cơ chế hoạt động & Code mẫu
Khi ứng dụng bị văng bởi một `RuntimeException`, thư viện lưu lại vết lỗi (Stack trace) vào bộ nhớ máy. Khi người dùng mở lại app ở lần kế tiếp, thư viện sẽ tự động gửi tệp log sự cố này lên Firebase Console.

```kotlin
// Nút thử nghiệm trong ProfileScreen.kt
Button(
    onClick = {
        throw RuntimeException("Test Crashlytics thành công từ UTH Todo!")
    }
) {
    Text("⚠️ Thử nghiệm Crashlytics")
}
```

### 🧪 Cách kiểm tra (Verification)
1. Chạy app $\rightarrow$ Mở màn hình **Hồ sơ (Profile)** $\rightarrow$ Bấm nút *"⚠️ Thử nghiệm Crashlytics"*.
2. Ứng dụng sẽ bị văng ra màn hình chính ngay lập tức.
3. **Mở lại ứng dụng một lần nữa** để SDK gửi tệp báo cáo lên đám mây.
4. Mở **Firebase Console $\rightarrow$ Crashlytics** $\rightarrow$ F5 trang web: Giao diện chuyển thành bảng báo cáo lỗi chi tiết hiển thị `Fatal Exception: java.lang.RuntimeException: Test Crashlytics thành công từ UTH Todo!`.

---

## 5. Firebase Remote Config (Cấu hình ứng dụng từ xa)

### 📌 Mục đích
Thay đổi nội dung văn bản, giao diện hoặc bật/tắt tính năng trong app từ trang web quản trị mà không cần sửa code hay cập nhật phiên bản mới lên cửa hàng ứng dụng.

### 📁 Các file liên quan
* `data/repository/RemoteConfigManager.kt` (Lớp Singleton quản lý tải cấu hình và giá trị mặc định)
* `MainActivity.kt` (Gọi `RemoteConfigManager.init()` khi mở app)
* `ui/screens/home/homeScreens.kt` (Hiển thị thông điệp từ biến cấu hình)

### 💻 Cơ chế Key - Value & Code mẫu
* **Key (Khóa):** Do lập trình viên tự đặt tên, ví dụ: `banner_message`.
* **Value (Giá trị):** Nội dung chuỗi văn bản điều khiển từ web Firebase.

```kotlin
// RemoteConfigManager.kt
object RemoteConfigManager {
    private val remoteConfig = Firebase.remoteConfig
    const val KEY_BANNER_MESSAGE = "banner_message"

    fun init() {
        // Đặt thời gian làm mới = 0s để phục vụ test/debug thấy kết quả ngay
        val settings = remoteConfigSettings { minimumFetchIntervalInSeconds = 0L }
        remoteConfig.setConfigSettingsAsync(settings)
        // Giá trị mặc định khi mất mạng
        remoteConfig.setDefaultsAsync(mapOf(KEY_BANNER_MESSAGE to "Bắt đầu từ việc nhỏ nhất nhé."))
        remoteConfig.fetchAndActivate()
    }

    fun getBannerMessage(): String = remoteConfig.getString(KEY_BANNER_MESSAGE)
}
```

### 🧪 Cách kiểm tra (Verification)
1. Mở **Firebase Console $\rightarrow$ Remote Config** $\rightarrow$ Thêm thông số:
   * **Parameter name:** `banner_message`
   * **Default value:** `"Mùa thi đến rồi, UTHers cố lên nhé!"`
2. Bấm **Save** $\rightarrow$ Bấm **Publish changes (Xuất bản thay đổi)**.
3. Mở lại ứng dụng trên máy ảo: Dòng chữ trên thẻ màu xanh (Tiến độ hôm nay) tự động đổi thành câu mới mà không cần chạm vào mã nguồn!

---

## 📦 Danh sách dependencies trong `gradle/libs.versions.toml`

```toml
[versions]
firebaseAnalytics = "22.1.2"
firebaseAuth = "23.2.0"
firebaseMessaging = "24.1.0"
firebaseCrashlyticsPlugin = "3.0.3"
firebaseCrashlytics = "19.4.1"
firebaseConfig = "22.1.0"

[libraries]
firebase-analytics = { group = "com.google.firebase", name = "firebase-analytics", version.ref = "firebaseAnalytics" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth", version.ref = "firebaseAuth" }
firebase-messaging = { group = "com.google.firebase", name = "firebase-messaging", version.ref = "firebaseMessaging" }
firebase-crashlytics = { group = "com.google.firebase", name = "firebase-crashlytics", version.ref = "firebaseCrashlytics" }
firebase-config = { group = "com.google.firebase", name = "firebase-config", version.ref = "firebaseConfig" }

[plugins]
google-gms-google-services = { id = "com.google.gms.google-services", version.ref = "googleGmsGoogleServices" }
firebase-crashlytics = { id = "com.google.firebase.crashlytics", version.ref = "firebaseCrashlyticsPlugin" }
```
