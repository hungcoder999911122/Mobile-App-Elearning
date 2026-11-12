_**GHI CHÚ**_
ÔN LẠI BÀI TẬP CŨ: 
+ BÀI 1: Hướng đối tượng, bổ sung sao để đóng gói toàn bộ dữ liệu cần thiết, để khi thêm màn hình hoặc remove màn hình đều thực hiện dễ dàng, mà hệ thống không bị lỗi ứng dụng (Áp dụng State Hoisting & Sealed Hierarchy).
+ BÀI 2: Quản lý layout co giãn linh hoạt (Responsive layout) với Modifier.weight(1f) và xử lý insets an toàn (navigationBarsPadding, statusBarsPadding).
+ BÀI 3: Hướng đối tượng, Giải thích tại sao từ màn hình thứ 3 khi nhập dữ liệu, bấm nút thì màn hình lại quay trở về màn hình đầu tiên (Do cơ chế quản lý ngăn xếp Backstack / Navigator.navigateAndClear).

_Design Pattern áp dụng:_ State Pattern, Repository Pattern, Singleton Pattern.

**Câu hỏi:** 
1. Dùng cái gì để di chuyển qua các màn hình?
+ Navigation Controller / AppNavigator quản lý Backstack và AnimatedContent.

2. Column và LazyColumn/LazyRow khác nhau như thế nào? 
+ `Column/Row`: Sử dụng cho các dữ liệu tĩnh, số lượng phần tử ít và cố định; vẽ toàn bộ phần tử lên bộ nhớ cùng một lúc.
+ `LazyColumn/LazyRow`: Sử dụng cho danh sách dữ liệu động, số lượng phần tử lớn; chỉ vẽ các phần tử đang hiển thị trên màn hình (tương tự RecyclerView), giúp tối ưu hiệu năng và tránh làm crash app do tràn bộ nhớ (OOM).

3. OOP (Lập trình hướng đối tượng):
+ **Tính đóng gói (Encapsulation)**: Đóng gói thuộc tính và phương thức xử lý nội tại trong Entity (Ví dụ: `Task.toggleCompleted()`, `UserProfile.initials`), che giấu chi tiết bằng private và cung cấp interface công khai.
+ **Tính trừu tượng (Abstraction)**: Chỉ cung cấp hợp đồng nghiệp vụ ra bên ngoài thông qua Interface (Ví dụ: `AuthRepository`, `TaskRepository`), tách biệt tầng giao diện UI khỏi chi tiết triển khai của Firebase/Database.
+ **Tính kế thừa & Đa hình (Polymorphism)**: Sử dụng Sealed Class (`AppScreen`) để định nghĩa danh sách màn hình, giúp type-safe và mở rộng tham số linh hoạt.

---

**BÁO CÁO TUẦN 5:**
+ FRAMEWORK FIREBASE
+ CÁC DỊCH VỤ FIREBASE ĐÃ TÍCH HỢP
+ FIREBASE AUTHENTICATION (ĐĂNG NHẬP & XÁC THỰC)
+ FIREBASE CLOUD MESSAGING (FCM)
+ FIREBASE CRASHLYTICS (BÁO CÁO SỰ CỐ)
+ FIREBASE REMOTE CONFIG (CẤU HÌNH TỪ XA)
+ FIREBASE ANALYTICS (PHÂN TÍCH HÀNH VI)

---

### SUPABASE VS FIREBASE

| Tiêu chí | Firebase (Google) | Supabase (Open-Source) |
|---|---|---|
| **Cơ sở dữ liệu** | NoSQL (Firestore, Realtime Database) | SQL Quan hệ (PostgreSQL) |
| **Xác thực (Auth)** | Hỗ trợ cực mạnh Google, Phone, Ẩn danh, Email | Hỗ trợ Email, OAuth bên thứ ba |
| **Hệ sinh thái** | Đầy đủ Analytics, Crashlytics, FCM, Remote Config | Tập trung chính vào Database, Auth, Storage, Edge Functions |
| **Mã nguồn** | Đóng (Độc quyền Google Cloud) | Mã nguồn mở (Có thể tự host trên server riêng) |

---

### FIREBASE LÀ GÌ?
Là nền tảng phát triển ứng dụng di động và web (Backend-as-a-Service - BaaS) do Google cung cấp. Firebase cung cấp đầy đủ các dịch vụ như cơ sở dữ liệu, xác thực người dùng, phân tích, thông báo đẩy, theo dõi lỗi, giúp lập trình viên tập trung xây dựng giao diện và nghiệp vụ app mà không cần tự xây dựng hay bảo trì server backend.

---

### CÁC DỊCH VỤ CỦA FIREBASE

* **AUTHENTICATION**: Dịch vụ xác thực danh tính người dùng an toàn qua email, ẩn danh, Google, Facebook...
* **CLOUD MESSAGING (FCM)**: Dịch vụ gửi thông báo đẩy (Push Notifications) hoàn toàn miễn phí từ máy chủ tới hàng triệu thiết bị.
* **CRASH REPORTING (Crashlytics)**: Tự động ghi lại và phân tích chi tiết các sự cố làm văng app (Crash log) theo thời gian thực.
* **REMOTE CONFIG**: Cập nhật văn bản, màu sắc hoặc cấu hình ứng dụng từ xa bằng mô hình Key - Value mà không cần xuất bản bản cập nhật mới.
* **ANALYTICS**: Đo lường, thống kê hành vi và tần suất sử dụng ứng dụng của người dùng.
* **REALTIME DATABASE**: Cơ sở dữ liệu NoSQL lưu trữ dưới dạng cây JSON, hỗ trợ đồng bộ dữ liệu thời gian thực.
* **TEST LAB**: Chạy thử nghiệm tự động ứng dụng trên kho thiết bị thật của Google trên đám mây.
* **ADMOB**: Tích hợp các banner và video quảng cáo để kiếm doanh thu cho ứng dụng.
* **DYNAMIC LINKS**: *(Đã khai tử/Deprecated năm 2025)* - Chuyển tiếp người dùng qua đường dẫn URL thông minh.

---

### CHI TIẾT CÁC KHÁI NIỆM TRỌNG TÂM

#### 1. AUTHENTICATION
* Quản lý phiên làm việc của người dùng (`currentUser`).
* Hỗ trợ lưu trữ tài khoản người dùng trên tab **Authentication $\rightarrow$ Users**.
* Phân tách rõ ràng giữa trường hợp đăng nhập thành công và đăng nhập thất bại (khi mất mạng hoặc bị vô hiệu hóa).

#### 2. CLOUD MESSAGING (FCM)
* **Gửi thông báo miễn phí** theo thời gian thực.
* **Phân nhóm người dùng** theo ứng dụng (`com.example.bai01`) hoặc theo chủ đề (Topics: `subscribeToTopic`).
* **Device Token (FCM Token)**: Là mã định danh duy nhất của từng thiết bị do Firebase sinh ra. Máy chủ sử dụng Token này để gửi chính xác thông báo đến thiết bị đó.

**Trả lời câu hỏi ôn tập:**
1. *Anh/chị có biết bắn thông báo hay không? (Cloud Messaging)*:
   $\rightarrow$ Có. Bằng cách thiết lập `FirebaseMessagingService`, đăng ký quyền `POST_NOTIFICATIONS` và sử dụng giao diện Firebase Console (hoặc backend API) để gửi tin nhắn đến thiết bị thông qua FCM Token hoặc gửi chiến dịch Broadcast.
2. *Thế nào là Token (FCM Token)?*:
   $\rightarrow$ FCM Token là một chuỗi ký tự định danh duy nhất mà dịch vụ FCM cấp cho mỗi bản cài đặt ứng dụng trên một thiết bị cụ thể. Khi muốn gửi tin nhắn riêng cho một người dùng nào đó, máy chủ sẽ dùng Token của người đó để định tuyến tin nhắn đến đúng máy.

#### 3. REMOTE CONFIG
* Hoạt động theo mô hình **Key - Value (Khóa - Giá trị)**.
* Key do lập trình viên tự đặt trong code (ví dụ `banner_message`), Value được thay đổi linh hoạt trên web Firebase Console.
* Đặt `minimumFetchIntervalInSeconds = 0L` trong môi trường debug để app nhận dữ liệu thay đổi ngay lập tức.

#### 4. CRASH REPORTING (CRASHLYTICS)
* Bắt mọi sự cố ngoại lệ chưa được xử lý (`Unhandled Exception` / `RuntimeException`).
* Tự động lưu stack trace vào bộ nhớ đệm và gửi lên Firebase Console ở lần khởi động kế tiếp.
* Báo cáo chi tiết: Tên thiết bị, phiên bản hệ điều hành Android, tên file và số dòng code chính xác gây sập app.
