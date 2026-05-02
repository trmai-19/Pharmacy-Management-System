-- 0. Chạy file drop bảng nếu cần dọn dẹp database
@drop_all.sql;

-- 1. Chạy file tạo cấu trúc bảng (CREATE TABLE và Primary Key)
@database.sql;

-- 2. Chạy file tạo các ràng buộc (Khóa ngoại, Check, Default...)
@constraints.sql;

-- 3. Chạy file tạo các Trigger
@triggers.sql;

-- 4. Chạy file tạo các thủ tục/hàm
-- @procedures.sql;

-- Xác nhận lưu toàn bộ thay đổi vào database
COMMIT;


-- 1. NẠP TÀI KHOẢN & NHÂN VIÊN (Để test phân quyền)
-- Mật khẩu mặc định đều là 'admin123' (đã băm BCrypt)
INSERT INTO TAIKHOAN (MATK, VAITRO, PASSWORD, SDT, NGAYTAO, TRANGTHAI) 
VALUES ('TKNV26001', 'STAFF', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', '0911222333', SYSDATE, 'ACTIVE');

INSERT INTO NHANVIEN (MANV, MATK, TENNV, GIOITINH, SDT, CHUCVU, TRANGTHAI)
VALUES ('NV001', 'TKNV26001', 'Nguyễn Thị Bán Thuốc', 'NỮ', '0911222333', 'SALES_STAFF', 'WORKING');

-- 2. NẠP DANH MỤC & SẢN PHẨM
INSERT INTO DANHMUC (MADM, TENDM, MOTA, TYLELOINHUAN) VALUES ('DM001', 'Thuốc giảm đau', 'Các loại thuốc giảm đau, hạ sốt', 15);
INSERT INTO DANHMUC (MADM, TENDM, MOTA, TYLELOINHUAN) VALUES ('DM002', 'Thực phẩm chức năng', 'Vitamin và khoáng chất', 20);

INSERT INTO SANPHAM (MASP, MADM, TENSANPHAM, DVT, GIABAN) VALUES ('SP001', 'DM001', 'Paracetamol 500mg', 'Viên', 2000);
INSERT INTO SANPHAM (MASP, MADM, TENSANPHAM, DVT, GIABAN) VALUES ('SP002', 'DM002', 'Vitamin C Enervon', 'Lọ', 55000);

-- 3. NẠP KHÁCH HÀNG (Để test tra cứu SĐT và Điểm) [cite: 170, 1250]
-- Khách vãng lai (Chỉ có SĐT)
INSERT INTO KHACHHANG (MAKH, TENKH, GIOITINH, SDT, TONGDOANHTHU, DIEMTICHLUY, HANGTV)
VALUES ('KH2600001', 'Nguyễn Văn An', 'NAM', '0901234567', 150000, 15, 'THANH VIEN');

-- Khách thân thiết (Đã có tài khoản)
INSERT INTO TAIKHOAN (MATK, VAITRO, PASSWORD, SDT, NGAYTAO, EMAIL, TRANGTHAI)
VALUES ('TKKH26001', 'CUSTOMER', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', '0988777666', SYSDATE, 'khachhang@gmail.com', 'ACTIVE');

INSERT INTO KHACHHANG (MAKH, MATK, TENKH, GIOITINH, SDT, TONGDOANHTHU, DIEMTICHLUY, HANGTV)
VALUES ('KH2600002', 'TKKH26001', 'Trần Thị Bình', 'NỮ', '0988777666', 2500000, 250, 'THANH VIEN');

-- 4. NẠP HÓA ĐƠN (Lịch sử mua hàng 2 năm) [cite: 175, 1473]
-- Đơn hàng năm ngoái (2025)
INSERT INTO HOADON (MAHD, MANV, MAKH, NGAYBAN, TONGTIEN, DIEMSUDUNG, TIENTHANHTOAN, TRANGTHAI)
VALUES ('HD25001', 'NV001', 'KH2600002', TO_DATE('2025-05-15', 'YYYY-MM-DD'), 1000000, 0, 1000000, 'HOAN_THANH');

-- Đơn hàng mới tháng trước (2026)
INSERT INTO HOADON (MAHD, MANV, MAKH, NGAYBAN, TONGTIEN, DIEMSUDUNG, TIENTHANHTOAN, TRANGTHAI)
VALUES ('HD26001', 'NV001', 'KH2600002', SYSDATE - 30, 500000, 50, 450000, 'HOAN_THANH');

-- 5. NẠP LOG ĐIỂM TÍCH LŨY [cite: 1311, 1316]
INSERT INTO DIEMTL (MADTL, MAKH, MAHD, LOAIGD, diemthaydoi, ngaygd, ghichu)
VALUES ('DTL25001', 'KH2600002', 'HD25001', 'CONG_DIEM', 100, TO_DATE('2025-05-15', 'YYYY-MM-DD'), 'Tích điểm đơn HD25001');

INSERT INTO DIEMTL (MADTL, MAKH, MAHD, LOAIGD, diemthaydoi, ngaygd, ghichu)
VALUES ('DTL26001', 'KH2600002', 'HD26001', 'TRU_DIEM', -50, SYSDATE - 30, 'Dùng điểm cho đơn HD26001');

INSERT INTO DIEMTL (MADTL, MAKH, MAHD, LOAIGD, diemthaydoi, ngaygd, ghichu)
VALUES ('DTL26002', 'KH2600002', 'HD26001', 'CONG_DIEM', 50, SYSDATE - 30, 'Tích điểm đơn HD26001');

-- QUAN TRỌNG: Phải có lệnh này để API nhìn thấy dữ liệu [cite: 704, 706]
COMMIT;

SELECT * from TAIKHOAN;

SELECT * from NHANVIEN;

SELECT * from KHACHHANG;