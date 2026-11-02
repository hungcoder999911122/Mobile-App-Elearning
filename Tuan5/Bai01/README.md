# 📁 Cấu trúc thư mục dự án

```text
com.example.bai01/
│
├── navigation/                     # Quản lý luồng điều hướng
│   ├── AppNavigation.kt            # NavHost chính
│   └── ScreenRoutes.kt             # Định nghĩa các route màn hình
│
├── ui/                             # Tầng giao diện người dùng
│   │
│   ├── theme/                      # Giao diện và phong cách ứng dụng
│   │   ├── Color.kt                # Khai báo màu sắc
│   │   ├── Theme.kt                # Cấu hình theme
│   │   └── Type.kt                 # Cấu hình kiểu chữ
│   │
│   ├── components/                 # Các Composable dùng chung
│   │   ├── CustomButton.kt         # Nút bấm tùy chỉnh
│   │   ├── CustomTextField.kt      # Ô nhập liệu tùy chỉnh
│   │   └── LoadingDialog.kt        # Hộp thoại trạng thái tải
│   │
│   └── screens/                    # Các màn hình ứng dụng
│       │
│       ├── onboarding/             # Khởi động và giới thiệu
│       │   ├── StartScreen.kt
│       │   └── OnboardingScreen.kt
│       │
│       ├── auth/                   # Xác thực người dùng
│       │   ├── LoginScreen.kt
│       │   ├── RegisterScreen.kt
│       │   └── ForgotPasswordScreen.kt
│       │
│       ├── home/                   # Màn hình chính
│       │   ├── HomeScreen.kt
│       │   └── components/         # Thành phần riêng của Home
│       │       └── TaskItem.kt
│       │
│       └── profile/                # Trang cá nhân và cài đặt
│           └── ProfileScreen.kt
│
├── data/                           # Tầng quản lý dữ liệu
│   ├── model/                      # Các mô hình dữ liệu
│   │   ├── User.kt
│   │   └── Task.kt
│   │
│   └── repository/                 # Xử lý truy cập dữ liệu
│       └── ...
│
└── MainActivity.kt                 # Activity khởi chạy ứng dụng
```

## 📝 Mô tả các thư mục chính

| Thư mục | Chức năng |
|---|---|
| `navigation/` | Quản lý điều hướng giữa các màn hình. |
| `ui/theme/` | Khai báo màu sắc, kiểu chữ và giao diện chung. |
| `ui/components/` | Chứa các thành phần giao diện có thể tái sử dụng. |
| `ui/screens/` | Chứa các màn hình, được phân chia theo từng tính năng. |
| `data/model/` | Định nghĩa các lớp dữ liệu như `User`, `Task`. |
| `data/repository/` | Quản lý việc lấy và xử lý dữ liệu từ API hoặc cơ sở dữ liệu. |
| `MainActivity.kt` | Điểm khởi chạy giao diện ứng dụng Android. |

> **Lưu ý:** Đây là cấu trúc dự kiến. Các file và thư mục sẽ được tạo khi triển khai từng chức năng. `data/` có thể được bổ sung khi ứng dụng cần quản lý dữ liệu phức tạp hơn.
