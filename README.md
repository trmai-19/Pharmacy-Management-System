# Pharmacy-Management-System
Java-based Windows App for Pharmacy Management and Inventory Control

Dựa vào mã nguồn bạn cung cấp, hệ thống được xây dựng bằng Spring Boot cung cấp rất nhiều API phục vụ cho Hệ thống Quản lý Nhà thuốc (Pharmacy Management System)[cite: 1]. Dưới đây là tổng hợp chi tiết tất cả các API được phân loại theo từng nhóm chức năng cụ thể:

### 1. Nhóm Xác thực & Quản lý Mật khẩu (Auth & Password)
Nhóm này quản lý việc đăng nhập và các thao tác liên quan đến mật khẩu người dùng
*   **`POST /api/login`**: Đăng nhập vào hệ thống bằng số điện thoại và mật khẩu, trả về thông tin user và token JWT.
*   **`POST /api/password/first-login-change`**: Đổi mật khẩu bắt buộc cho lần đăng nhập đầu tiên.
*   **`POST /api/password/setting-change`**: Đổi mật khẩu trong mục cài đặt tài khoản (yêu cầu mật khẩu cũ).
*   **`POST /api/password/forgot`**: Quên mật khẩu, hệ thống sẽ tạo mật khẩu tạm thời và gửi về email người dùng.

### 2. Nhóm Quản trị viên - Tài khoản & Nhân viên (Admin Account & Employee)
Chỉ dành cho quyền ADMIN để quản lý nhân viên và cấp tài khoản.
*   **`POST /api/admin/accounts/create`**: Tạo tài khoản mới cho nhân viên (với vai trò STAFF) và tự động gửi mật khẩu khởi tạo qua email.
*   **`PUT /api/admin/accounts/{id}/status`**: Khóa/Mở khóa trạng thái hoạt động của một tài khoản.
*   **`GET /api/admin/employees`**: Lấy danh sách toàn bộ hồ sơ nhân viên.
*   **`PUT /api/admin/employees/{id}`**: Cập nhật hồ sơ thông tin của nhân viên.
*   **`DELETE /api/admin/employees/{id}`**: Xóa nhân viên (đổi trạng thái nhân viên sang "RESIGNED" - đã nghỉ việc).

### 3. Nhóm Hồ sơ cá nhân (Profile)
*   **`PUT /api/profile/update`**: Cập nhật thông tin hồ sơ cá nhân của người dùng đang đăng nhập (áp dụng cho cả nhân viên hoặc khách hàng).

### 4. Nhóm Sản phẩm & Danh mục (Product & Category)
Bao gồm các API quản lý thông tin thuốc và phân loại dành cho Admin và Staff.
*   **`GET /api/admin/categories`**: Lấy danh sách tất cả các danh mục thuốc (dành cho Admin).
*   **`POST /api/admin/categories`**: Thêm mới một danh mục thuốc.
*   **`PUT /api/admin/categories/{id}`**: Cập nhật thông tin danh mục thuốc.
*   **`DELETE /api/admin/categories/{id}`**: Xóa một danh mục.
*   **`GET /api/categories`**: Lấy danh sách danh mục (rút gọn thông tin) dành cho nhân viên (Staff).
*   **`GET /api/products`**: Lấy danh sách hoặc tìm kiếm các loại thuốc/sản phẩm (có hỗ trợ tham số `?search=`).
*   **`GET /api/products/{id}`**: Lấy thông tin chi tiết của một loại thuốc.
*   **`POST /api/admin/products`**: Thêm mới một loại thuốc/sản phẩm.
*   **`PUT /api/admin/products/{id}`**: Cập nhật thông tin thuốc/sản phẩm.
*   **`DELETE /api/admin/products/{id}`**: Xóa thuốc/sản phẩm.

### 5. Nhóm Khách hàng & Bán hàng (Customer & Sales)
Các API phục vụ bán hàng, quản lý hồ sơ khách hàng, hóa đơn và điểm tích lũy.
*   **`GET /api/sales/customers?sdt=...`**: Tìm kiếm thông tin khách hàng dựa vào số điện thoại.
*   **`POST /api/sales/customers`**: Tạo hồ sơ khách hàng mới một cách chi tiết.
*   **`POST /api/sales/customers/quick-create`**: Tạo nhanh hồ sơ khách hàng (chỉ cần tên và số điện thoại) để phục vụ việc tích điểm ngay tại quầy.
*   **`POST /api/sales/customers/{makh}/upgrade`**: Cấp tài khoản đăng nhập cho một khách hàng vãng lai (mật khẩu được gửi qua email).
*   **`GET /api/sales/customers/{makh}/invoices`**: Lấy lịch sử mua hàng/hóa đơn trong 2 năm gần nhất của một khách hàng.
*   **`GET /api/sales/loyalty/history/{customerId}`**: Lấy lịch sử giao dịch biến động điểm tích lũy của khách hàng.

### 6. Nhóm Kho hàng & Phân tích (Warehouse & Inventory Analysis)
Quản lý nhập kho, nhà cung cấp, lô hàng và các cảnh báo/gợi ý thông minh cho kho.
*   **`GET /api/warehouse/suppliers`**: Lấy danh sách nhà cung cấp.
*   **`POST /api/warehouse/suppliers`**: Thêm mới nhà cung cấp.
*   **`PUT /api/warehouse/suppliers/{id}`**: Cập nhật thông tin nhà cung cấp.
*   **`GET /api/warehouse/import-receipts`**: Lấy danh sách phiếu nhập kho.
*   **`POST /api/warehouse/import-receipts`**: Tạo phiếu nhập kho, tự động tạo mới các lô sản phẩm và cập nhật số lượng tồn kho.
*   **`GET /api/warehouse/batches`**: Lấy danh sách toàn bộ các lô sản phẩm hiện có.
*   **`GET /api/warehouse/inventory`**: Lấy danh sách số lượng hàng tồn theo từng lô/kho.
*   **`GET /api/warehouse/alerts/low-stock`**: Lấy danh sách các sản phẩm đang sắp hết hàng (tồn kho dưới 10).
*   **`GET /api/warehouse/alerts/expiring-soon`**: Lấy danh sách các lô thuốc sắp hết hạn sử dụng (dưới 3 tháng).

### 8. API Kiểm thử hệ thống (Test/Utility)
*   **`GET /api/test-mail?toEmail=...`**: API test gửi email HTML thử nghiệm nghiệm để kiểm tra cấu hình SMTP của hệ thống có hoạt động không.