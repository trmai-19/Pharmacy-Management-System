# Pharmacy-Management-System
# HƯỚNG DẪN CÀI ĐẶT VÀ VẬN HÀNH HỆ THỐNG

## 1. YÊU CẦU VÀ CÀI ĐẶT CHUNG

### 1.1. Yêu cầu môi trường và công cụ

Để triển khai toàn bộ hệ thống Quản lý Nhà thuốc bao gồm Ứng dụng dành cho nhân viên và Nền tảng Web dành cho khách hàng, máy tính cần được cài đặt sẵn các phần mềm sau:

* Java Development Kit: Phiên bản 21.
* Node.js: Phiên bản 16.x trở lên kèm theo npm.
* Apache Maven: Công cụ quản lý dự án và thư viện Java.
* Docker hoặc Docker Desktop: Môi trường ảo hóa để chạy cơ sở dữ liệu.
* Công cụ lập trình: Visual Studio Code. Yêu cầu cài đặt thêm gói mở rộng Extension Pack for Java để hỗ trợ chạy mã nguồn Spring Boot và JavaFX.
* Công cụ quản lý Cơ sở dữ liệu: DBeaver, DataGrip hoặc SQL Developer.

### 1.2. Khởi tạo Cơ sở dữ liệu Oracle

Cả hai nền tảng App và Web đều sử dụng chung một cơ sở dữ liệu Oracle được triển khai thông qua Docker.

* Mở ứng dụng Terminal hoặc Command Prompt tại thư mục gốc của dự án.
* Khởi chạy vùng chứa cơ sở dữ liệu bằng lệnh: `docker-compose up -d`
* Sử dụng công cụ quản lý cơ sở dữ liệu để tạo kết nối mới với các thông số:
* Host: `localhost`
* Port: `1521`
* Service Name: `XEPDB1`
* Username: `nha_thuoc`
* Password: `123456`


* Sau khi kết nối thành công, người dùng cần mở thư mục `oracle` trong mã nguồn và chạy lần lượt các kịch bản SQL theo thứ tự quy định để khởi tạo cấu trúc bảng, các ràng buộc và nạp dữ liệu mẫu ban đầu.

---

## 2. HƯỚNG DẪN CHẠY ỨNG DỤNG NHÂN VIÊN (APP)

### 2.1. Khởi chạy Backend API

* Mở thư mục chứa mã nguồn Backend của Ứng dụng Nhân viên bằng phần mềm Visual Studio Code.
* Chờ hệ thống tự động đồng bộ Maven để tải về các thư viện cần thiết.
* Mở tệp `application.properties` trong thư mục cấu hình để thiết lập các thông số bắt buộc:
* Cấu hình Cơ sở dữ liệu: Đảm bảo thông tin kết nối trùng khớp với thiết lập ở phần 1.2.
* Cấu hình Email: Bắt buộc thay thế tài khoản tại mục `spring.mail.username` và Mật khẩu ứng dụng tại mục `spring.mail.password` bằng thông tin cá nhân của bạn để hệ thống có thể gửi thư.
* Cấu hình Gemini AI: Bắt buộc thay thế đoạn `{GEMINI_API_KEY}` tại mục `gemini.api.key` bằng API Key thật của bạn. Hệ thống sẽ báo lỗi không thể sử dụng trợ lý ảo nếu thiếu cấu hình này.


* Mở lớp khởi tạo chứa phương thức `main` và nhấn Run. Hệ thống sẽ khởi động và lắng nghe các yêu cầu tại cổng `8080`.

### 2.2. Khởi chạy Frontend JavaFX

* Mở thư mục chứa mã nguồn Frontend App trên một cửa sổ Visual Studio Code mới.
* Chờ quá trình tải các thành phần phụ thuộc của JavaFX hoàn tất.
* Khởi chạy giao diện ứng dụng bằng một trong hai phương pháp:
* Phương pháp 1: Mở Terminal tích hợp của VS Code tại thư mục hiện tại và chạy lệnh: `mvn javafx:run`
* Phương pháp 2: Mở tệp chứa lớp khởi tạo giao diện của dự án và nhấn Run tại phương thức `main`.



---

## 3. HƯỚNG DẪN CHẠY NỀN TẢNG KHÁCH HÀNG (WEB)

### 3.1. Khởi chạy Backend API (cusapi)

* Mở thư mục chứa mã nguồn Backend của Web bằng Visual Studio Code.
* Chờ hệ thống đồng bộ Maven.
* Mở tệp `application.properties` để kiểm tra và cập nhật các cấu hình thiết yếu:
* Thay thế thông tin máy chủ gửi thư điện tử bao gồm email và App Password cá nhân tương tự như hướng dẫn ở phần 2.1.
* Cập nhật khóa bí mật JWT nếu triển khai thực tế.


* Mở lớp khởi tạo chứa phương thức `main` và nhấn Run. Dịch vụ API dành riêng cho khách hàng sẽ khởi chạy độc lập và lắng nghe tại cổng `8081`.

### 3.2. Khởi chạy Frontend ReactJS

* Mở thư mục chứa mã nguồn Frontend Web bằng phần mềm Visual Studio Code.
* Mở Terminal tích hợp trong VS Code và thực thi lệnh sau để tải các gói thư viện Node.js cần thiết: `npm install`
* Sau khi quá trình cài đặt hoàn tất, khởi động máy chủ phát triển giao diện bằng lệnh: `npm run dev`
* Truy cập vào nền tảng Web thông qua trình duyệt tại đường dẫn được hiển thị trên Terminal, mặc định là `http://localhost:5173`.