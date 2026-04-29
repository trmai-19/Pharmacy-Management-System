# Quy tắc code Backend (API)
Đọc kỹ trước khi bắt tay vào code!!!

---

## 1. Cấu Trúc Thư Mục (Package Structure)
Code phần nào phải được đặt đúng vào thư mục tương ứng trong `src/main/java/com/pharmacy/backend`:

* **`model`**: Chứa các class ánh xạ với bảng trong Database (VD: `Product.java`, `Customer.java`). 
*=> Đã code đủ 18 model tương ứng với 18 bảng (không có 2 bảng log)*

* **`repository`**: Chứa các Interface tương tác với Database (`extends JpaRepository`)
    * Tuyệt đối **không** viết logic tính toán ở đây. CHỈ TƯƠNG TÁC VỚI DATABASE.
    * Tên file: `[Name]Repository.java` (VD: `ProductRepository.java`).

* **`service`**: Chứa toàn bộ code logic nghiệp vụ. Xử lý tính toán, kiểm tra điều kiện, gọi `repository` để lưu/lấy data. Phân vùng như sau để triển khai DI:

    * Các **Interface** : Chỉ khai báo hàm, không viết logic.
    * Tên file: `[Name]Service.java` (VD: `ProductService.java`).

    * Các lớp **Implementation** : Viết logic thực tế. 
    * Tên file: `[Name]ServiceImpl.java` (VD: `ProductServiceImpl.java`).

    * Class này phải có annotation `@Service` và implements Interface tương ứng.

* **`controller`**: 
    * Chỉ dùng để tiếp nhận Request từ Frontend và trả về Response. 
    * **Chỉ gọi sang Service**, tuyệt đối không gọi thẳng Repository.
    * Tên file: `[Name]Controller.java` (VD: `ProductController.java`).

* **`dto` (Data Transfer Object)**:
    * Chứa các class dùng để nhận dữ liệu đầu vào (`Request`) và bọc dữ liệu trả ra (`Response`).
    * **Tuyệt đối KHÔNG** dùng trực tiếp class `model` (Entity) để trả thẳng về Frontend.
    * **Quy tắc đặt tên:**
        * Class nhận dữ liệu: `[Name]Request.java` (VD: `LoginRequest.java`).
        * Class trả dữ liệu: `[Name]Response.java` (VD: `LoginResponse.java`).
    * **Luật cho file Request:** Phải khai báo đầy đủ các trường (fields) tương ứng với dữ liệu user/frontend gửi lên.
    * **Luật cho file Response & Vỏ bọc `ApiResponse<T>`:**
        * Các file `[Name]Response` chỉ dùng để gom những dữ liệu đặc thù của chức năng đó (VD: `LoginResponse` chứa token, quyền; `ProductResponse` chứa tên thuốc, giá bán).
        * Tuy nhiên, khi Controller `return` dữ liệu, **BẮT BUỘC** phải lấy cái `[Name]Response` đó nhét vào phần `data` của vỏ hộp chung mang tên `ApiResponse<T>`.
        * Status: thành công: 200, thất bại: 400

    * **Ví dụ cụ thể**
        Giả sử cần trả về thông tin đăng nhập (`LoginResponse`). Controller của bạn phải định nghĩa là `ResponseEntity<ApiResponse<LoginResponse>>`.
        Lúc này, cấu trúc JSON trả về:
        ```json
        {
        "status": 200,                      // <-- Thuộc tính của ApiResponse
        "message": "Đăng nhập thành công",  // <-- Thuộc tính của ApiResponse
        "data": {                           // <-- Toàn bộ cục data này chính là cái LoginResponse
            "token": "eyJhbGciOiJIUzI1Ni...",
            "hoten": "Nguyễn Văn A",
            "vaiTro": "ADMIN"
        }
        }

* **`config`**: Nơi chứa các file cấu hình hệ thống (VD: CORS, Swagger, Admin Seeder).

* **`security`**: Nơi chứa các cấu hình bảo mật, JWT, Filter, và phân quyền.
=> *không đụng vào phần này, chỉ đọc hiểu nếu cần gọi ra sử dụng*

---

## 2. Luồng Kiến Trúc & Xử Lý Dữ Liệu (Architecture Flow)
Luồng xử lý dữ liệu bắt buộc phải đi theo 3 lớp (3-tier architecture) và theo **1 hướng duy nhất**. Tuyệt đối **KHÔNG** gọi trực tiếp Repository từ Controller.

`[Client] => Controller => Service Interface => ServiceImpl => Repository => [Database]`

**Chi tiết luồng xử lý:**

1. **Controller** nhận JSON Request, ánh xạ vào một file `RequestDTO`.
2. Truyền `RequestDTO` đó sang **Service**.
3. **Service** xử lý logic, chuyển DTO thành `Model` tương ứng và gọi **Repository**.
4. **Repository** lưu vào Database, trả kết quả về Service.
5. **Service** chuyển kết quả thành `ResponseDTO` rồi ném lại cho Controller.
6. **Controller** bọc kết quả vào class `ApiResponse` và trả về cho Frontend (Kèm HTTP Status).

---

## 3. Quy Tắc Triển Khai IoC/DI

### Khai báo Service
Khi viết một chức năng mới (VD: Quản lý thuốc):
1. Tạo Interface `ProductService`
2. Tạo Class `ProductServiceImpl` để thực thi các hàm trong interface.

### Inject Service vào Controller
**Tuyệt đối không** inject trực tiếp lớp `Impl`, luôn inject thông qua **Interface**.
Dùng **Constructor Injection** (tuyệt đối không dùng `@Autowired` trên field).

---

## 4. Quy Tắc Đặt Tên & Phân Luồng API

Toàn bộ API của hệ thống bắt buộc phải bắt đầu bằng tiền tố `/api` và được phân nhánh cụ thể dựa theo vai trò (Role) đã cấu hình trong Security. Tuyệt đối tuân thủ các Prefix sau để tránh bị block bởi lỗi `403 Forbidden`:

### Phân Luồng Prefix Theo Quyền

* **Nhóm API Public (Không yêu cầu đăng nhập):**
    * Dùng cho các chức năng công khai, xác thực.
    * **Ví dụ:** `POST /api/login`, `POST /api/password/forgot`

* **Nhóm API Quản trị (Admin):**
    * Chỉ tài khoản có quyền `ADMIN` mới truy cập được. Phục vụ quản lý nhân sự, xem báo cáo tổng quan.
    * **Prefix bắt buộc:** `/api/admin/...`
    * **Ví dụ:** `GET /api/admin/employees`, `POST /api/admin/employees`

* **Nhóm API Kho (Warehouse):**
    * Dành cho nhân viên kho (`WAREHOUSE_STAFF`) và Quản lý (`ADMIN`). Phục vụ nghiệp vụ nhập hàng, kiểm kê, quản lý nhà cung cấp.
    * **Prefix bắt buộc:** `/api/warehouse/...`
    * **Ví dụ:** `POST /api/warehouse/import-receipts`, `GET /api/warehouse/inventory`

* **Nhóm API Bán hàng (Sales):**
    * Dành cho nhân viên bán thuốc (`SALES_STAFF`) và Quản lý (`ADMIN`). Phục vụ nghiệp vụ tạo hóa đơn, kê đơn, tra cứu khách hàng.
    * **Prefix bắt buộc:** `/api/sales/...`
    * **Ví dụ:** `POST /api/sales/invoices`, `GET /api/sales/customers`

* **Nhóm API Dùng chung (Authenticated):**
    * Mọi API **không** thuộc các nhánh trên sẽ yêu cầu đăng nhập (bất kể role nào cũng gọi được). Dùng cho các dữ liệu cơ bản cần tra cứu chung.
    * **Prefix:** `/api/[tên-tài-nguyên]`
    * **Ví dụ:** `GET /api/medicines` (Lấy danh mục thuốc để bán hoặc nhập kho), `GET /api/profile`

### Quy tắc đặt tên

Khi đã ghép đúng Prefix ở trên, phần đuôi (Resource) bắt buộc tuân thủ chuẩn REST:

1.  **Luôn dùng Danh từ số nhiều:** Đại diện cho một tập hợp dữ liệu.
    * **Đúng:** `/api/warehouse/suppliers` (Danh sách nhà cung cấp)
    * **Sai:** `/api/warehouse/supplier`, `/api/warehouse/getSupplier`

2.  **Dùng kebab-case (chữ thường, cách nhau bằng dấu gạch ngang):** Áp dụng nếu tên api có từ 2 chữ trở lên.
    * **Đúng:** `/api/sales/prescription-details`
    * **Sai:** `/api/sales/prescription_details`, `/api/sales/prescriptionDetails`

3.  **Tuyệt đối KHÔNG đưa động từ vào URL:** Sử dụng HTTP Method để thể hiện hành động:
    * `GET`: Lấy dữ liệu (VD: `GET /api/medicines`)
    * `POST`: Tạo mới dữ liệu (VD: `POST /api/admin/employees`)
    * `PUT`: Cập nhật toàn bộ thông tin đối tượng (VD: `PUT /api/medicines/{id}`)
    * `DELETE`: Xóa dữ liệu (VD: `DELETE /api/medicines/{id}`)

### Quy Ước Sử Dụng HTTP Method & Annotation (Spring Boot)

Để Frontend gọi đúng và Backend xử lý chuẩn, mỗi API bắt buộc phải được gắn một "nhãn" (Annotation) trên đầu hàm trong `Controller`. Các nhãn này đại diện cho hành động thao tác với Database.

Tuyệt đối tuân thủ thần chú: **"Lấy dùng GET, Thêm dùng POST, Sửa dùng PUT, Xóa dùng DELETE"**.

* **GET (`@GetMapping`) - Lấy / Tra cứu / Tìm kiếm:** * Dùng để lấy dữ liệu ra để xem. *(Tuyệt đối không dùng để thay đổi Database)*. 
  * **Ví dụ:** Tra cứu danh sách thuốc, lấy điểm tích lũy của khách hàng.

* **POST (`@PostMapping`) - Thêm mới / Tạo:** * Dùng để thêm một dòng dữ liệu hoàn toàn mới vào Database. 
  * **Ví dụ:** Lập hóa đơn bán hàng mới, thêm nhà cung cấp mới vào hệ thống.

* **PUT (`@PutMapping`) - Cập nhật / Sửa:** * Dùng để sửa thông tin của một dữ liệu đã tồn tại sẵn. 
  * **Ví dụ:** Cập nhật hồ sơ nhân viên, sửa thông tin định danh của thuốc.

* **DELETE (`@DeleteMapping`) - Xóa:** * Dùng để xóa dữ liệu khỏi hệ thống. 
  * **Ví dụ:** Xóa một danh mục thuốc không còn kinh doanh.

> **LƯU Ý:** > * Dữ liệu gửi lên API dùng `@GetMapping` sẽ nằm trực tiếp trên URL (Params/Query). 
> * Dữ liệu gửi lên API dùng `@PostMapping` và `@PutMapping` bắt buộc phải được bọc trong một đối tượng JSON (Body Request) và phải dùng kèm annotation `@RequestBody` ở tham số truyền vào hàm.

## 5. Danh Sách API Nghiệp Vụ Cốt Lõi (Core APIs)

* Danh sách các API cần phát triển, được phân luồng theo Prefix URL đã thống nhất.
* Tên các API chỉ cần đặt đúng prefix là được, còn resource có thể tự đặt sao cho hợp lý, miễn là đúng quy tắc. 
* Tên các API dưới đây chỉ là gợi ý (được gen bằng AI ^_^), chỉ cần đảm bảo yêu cầu của API là được.

### 5.1. Nhóm API Dùng Chung (Authenticated)
Dành cho các thao tác tra cứu cơ bản, yêu cầu đăng nhập nhưng không giới hạn quyền (Admin, Sales, Warehouse đều dùng được).

**Quản lý Danh mục & Sản phẩm (Thuốc)**
* `GET /api/categories`: Lấy danh sách toàn bộ danh mục thuốc.
* `GET /api/products`: Tra cứu danh sách sản phẩm (Hỗ trợ query params để tìm kiếm theo tên, hoạt chất, công dụng phục vụ Autocomplete).
* `GET /api/products/{id}`: Xem thông tin chi tiết một sản phẩm (tên, công dụng, thành phần, giá bán niêm yết).

### 5.2. Nhóm API Bán Hàng (Sales)
Phục vụ trực tiếp nghiệp vụ tại quầy. Prefix bắt buộc: `/api/sales/...`

**Quản lý Khách hàng & Điểm tích lũy**
* `GET /api/sales/customers`: Tìm kiếm khách hàng (bằng số điện thoại hoặc tên).
* `POST /api/sales/customers`: Tạo hồ sơ khách hàng mới (khách mua lần đầu muốn đăng ký).
* `GET /api/sales/customers/{id}/invoices`: Xem lịch sử mua hàng của một khách cụ thể (giới hạn 2 năm).
* `GET /api/sales/customers/{id}/loyalty-points`: Tra cứu điểm tích lũy hiện tại của khách.

**Giao dịch & Hóa đơn**
* `POST /api/sales/invoices`: Tạo hóa đơn mới. (Nhận DTO danh sách thuốc, xử lý tính tổng tiền, trừ tồn kho, cộng/trừ điểm tích lũy).
* `GET /api/sales/invoices/{id}`: Xem chi tiết hóa đơn đã tạo (phục vụ in bản cứng, kèm ghi chú tư vấn).

**Đổi trả từ Khách hàng**
* `POST /api/sales/return-receipts`: Tạo phiếu nhận hàng trả lại từ khách (Tự động tính tiền hoàn trả).

### 5.3. Nhóm API Quản lý Kho (Warehouse)
Dành cho nhân viên kho thực hiện nhập, xuất, kiểm kê. Prefix bắt buộc: `/api/warehouse/...`

**Nhà cung cấp**
* `GET /api/warehouse/suppliers`: Lấy danh sách nhà cung cấp.
* `POST /api/warehouse/suppliers`: Thêm nhà cung cấp mới.
* `PUT /api/warehouse/suppliers/{id}`: Cập nhật thông tin nhà cung cấp.

**Nhập kho & Trả hàng cho Nhà cung cấp**
* `GET /api/warehouse/import-receipts`: Xem lịch sử các phiếu nhập kho.
* `POST /api/warehouse/import-receipts`: Tạo phiếu nhập kho mới (Tính tổng tiền, tạo mã lô mới và cộng dồn tồn kho).
* `POST /api/warehouse/supplier-returns`: Tạo phiếu xuất trả hàng lỗi về cho Nhà cung cấp.

**Tồn kho & Lô sản phẩm**
* `GET /api/warehouse/inventory`: Xem chi tiết số lượng tồn kho của các sản phẩm.
* `GET /api/warehouse/batches`: Quản lý danh sách các lô thuốc, phân loại theo hạn sử dụng.
* `GET /api/warehouse/alerts/low-stock`: Lấy danh sách các thuốc đang có số lượng tồn dưới định mức an toàn.
* `GET /api/warehouse/alerts/expiring-soon`: Lấy danh sách các lô thuốc sắp hết hạn (dưới 3 tháng) để cảnh báo.

### 5.4. Nhóm API Quản trị Hệ Thống (Admin)
Chỉ Admin mới được phép thao tác. Prefix bắt buộc: `/api/admin/...`

**Quản trị tài khoản hệ thống**
* `PUT /api/admin/acounts/{id}/status`: Khóa / mở khóa tài khoản.
* `POST /api/admin/acounts/create`: Tạo tài khoản cho nhân viên mới (cấp quyền Sales hoặc Warehouse).

**Quản trị Dữ liệu Lõi**
* `POST /api/admin/categories` | `PUT /api/admin/categories/{id}` | `DELETE /api/admin/categories/{id}`: Quản lý thêm/sửa/xóa danh mục.
* `POST /api/admin/products` | `PUT /api/admin/products/{id}` | `DELETE /api/admin/products/{id}`: Quản lý thêm/sửa/xóa thông tin gốc của sản phẩm.

**Quản lý Nhân sự**
* `GET /api/admin/employees`: Lấy danh sách nhân viên.
* `PUT /api/admin/employees/{id}`: Cập nhật hồ sơ nhân viên
* `DELETE /api/admin/employees/{id}` : Xóa nhân viên (update thành đã nghỉ).


**Báo cáo & Thống kê (Dashboard)**
* `GET /api/admin/reports/revenue`: Thống kê doanh thu bán hàng (lọc theo ngày, tháng, năm).
* `GET /api/admin/reports/inventory-movements`: Báo cáo tình hình xuất/nhập kho.
* Sau này sẽ thêm các API khác về Dashboard.
---

**đây là quy tắc được thêm vào sau khi refactor**
## 6. Quy Tắc Xử Lý Lỗi (Exception Handling) & GlobalExceptionHandler

Để giữ cho tầng Controller mỏng nhẹ và code dễ bảo trì, dự án **nghiêm cấm** việc sử dụng `boolean` (`true`/`false`) hoặc trả về `null` từ tầng Service để kiểm tra lỗi nghiệp vụ (VD: không tìm thấy dữ liệu, sai mật khẩu, tài khoản đã bị khóa...). 

Tất cả các logic lỗi bắt buộc phải ném ra một **`RuntimeException`** kèm theo câu thông báo lỗi cụ thể.

### Cơ Chế Hoạt Động Của `GlobalExceptionHandler` // file này nằm trong folder config
Hệ thống đã được cấu hình sẵn một "Trạm thu phí" bắt lỗi mang tên `GlobalExceptionHandler` (nằm trong thư mục `exception`). 
* **Bắt lỗi tự động:** Bất cứ khi nào tầng `Service` ném ra một `RuntimeException`, luồng chạy sẽ lập tức dừng lại và `GlobalExceptionHandler` sẽ tự động "tóm" lấy cái lỗi đó.
* **Chuẩn hóa Response:** Nó sẽ lấy câu thông báo lỗi (message) và tự động đóng gói thành một file JSON chuẩn `ApiResponse` (Status 400 - Bad Request) để trả về cho Frontend.

### Ví dụ Thực Tế

**SAI (Cấm dùng): Trả về boolean / if-else**
```java
// Trong Service
public boolean deleteEmployee(String id) {
    Optional<Employee> empOpt = repo.findById(id);
    if (empOpt.isEmpty()) {
        return false; // Trả về false không rõ lý do lỗi
    }
    // ... logic xóa
    return true;
}

// Trong Controller (Code bị phức tạp vì phải check if-else)
@DeleteMapping("/{id}")
public ResponseEntity<?> delete(@PathVariable String id) {
    if (service.deleteEmployee(id)) {
        return ResponseEntity.ok(new ApiResponse<>(200, "Thành công", null));
    } else {
        return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Lỗi xóa", null));
    }
}
```

**CHUẨN: Quăng Exception & Bỏ qua if-else ở Controller**
```java
// Trong Service: Tận dụng orElseThrow của JPA để code ngắn gọn
public void deleteEmployee(String id) {
    Employee emp = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với mã: " + id));
        
    // ... logic xóa
}

// Trong Controller: Mặc định luôn là thành công (Vì nếu lỗi, Exception Handler sẽ bắt)
@DeleteMapping("/{id}")
public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
    service.deleteEmployee(id); // Gọi hàm, bỏ qua if-else
    return ResponseEntity.ok(new ApiResponse<>(200, "Đã xóa nhân viên thành công", null));
}
```

> **TỔNG KẾT:** 
> * **Tầng Service:** Có lỗi là `throw new RuntimeException("Lý do lỗi");`
> * **Tầng Controller:** Chỉ gọi Service và `return ResponseEntity.ok(...)`. Tuyệt đối không `try-catch`, không `if-else`.