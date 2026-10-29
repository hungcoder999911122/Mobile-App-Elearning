_**GHI CHÚ**_
ÔN LẠI BÀI TẬP CŨ: 
+ BÀI 1: Hướng đối tượng, bổ sung sao để đóng gói toàn bộ dữ liệu cần thiết, để khi thêm màn hình
hoặc remove màn hình đều thực hiện dễ dàng, mà hệ thống không bị lỗi ứng dụng
+ BÀI 2: 
+ BÀI 3: Hướng đối tượng, Giải thích tại sao từ màn hình thứ 3 khi nhập dữ liệu, bấm nút thì màn hình
lại quay trở về màn hình đầu tiên

_decide patern_

**Câu hỏi:** 
1. Dùng cái gì để di chuyển qua các màn hình ?
+ Navigation

2. Column và Lazy Column/row khác nhau như thế nào? 
+ Column sử dụng khi cho các dữ liệu tĩnh, và có giới hạn không quá nhiều dữ liễu 
+ Lazy Column sử dụng khi có nhiều dữ liệu và giúp tối ưu tránh làm crash app 

3. OOP
+ Tính đa hình
+ Tính đóng gói: Các thuộc tính phải nằm trong private, Có phương thức get, set được public
+ Tính trừu tượng: Chỉ cung cấp phương thức ra bên ngoài, con chi tiết bên trong không hiển thị chi
 tiết. Những hàm nào muốn sử dụng lại những hàm trừu tượng thì phải kế thừa (abstract , interface)
...

**BÁO CÁO TUẦN 5:**
+ FRAMEWORK FIREBASE
+ CÁC DỊCH VỤ FIREBASE
+ FIREBASE ĐĂNG NHẬP & ĐĂNG KÝ
+ FIREBASE 

**SUPABASE VS FIREBASE**

**FIREBASE LÀ GÌ ?**
Là nền tảng phát triển ứng dụng di động và web cung cấp nhiều dịch vụ như cơ sở dữ liệu, xác thực
người dùng, lưu trữ, phân tích. Giúp lập trình viên phát triển ứng dụng nhanh hơn mà không cần quản
lý server.

**CÁC DỊCH VỤ CỦA FIREBASE**

* REALTIME DATABASE: 
* AUTHENTICATION: 
* CLOUD MESSAGING: Bắn thông báo 
* REMOTE CONFIG: Cầu hình dữ liệu từ xa
* Test Lab: 
* CRASH REPORTING: 
* Dynamic Links: 
* AdMob: Thêm quảng cáo


**REALTIME DATABASE**

Cơ sở dữ liệu NoSQL cho phép lưu trữ dữ liệu dưới dạng json 

**AUTHENTICATION**
Lấy dữ liệu bên thứ ba cuar người dùng đưa vào máy mình bằng 1 thao tác nhanh gọn dễ dàng.

**CLOUD MESSAGING**
* Gửi thông báo miễn phí
* Hỗ trợ thông báo theo thời gian thực
* Gửi dữ liệu tuỳ chỉnh
* Phân nhóm người dùng theo topic

**Device Token**
* Mã định danh thiết bị của firebase sinh ra sử dụng để bắn tin nhắn và cần phải lưu vào 
cơ sở dữ liệu 

1. Anh chị có biết bắn thông báo hay ko ? (cloud messaging)
2. Vậy thế nào là token (sql token) ?

**REMOTE CONFIG**
* Sử dụng mô hình key - value
...

**CRASH REPORTING**
