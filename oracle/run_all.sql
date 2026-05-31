-- 0. Chạy file drop bảng nếu cần dọn dẹp database
@drop_all.sql;
COMMIT;
-- 1. Chạy file tạo cấu trúc bảng (CREATE TABLE và Primary Key)
@database.sql;
commit;
-- 2. Chạy file tạo các ràng buộc (Khóa ngoại, Check, Default...)
@constraints.sql;
commit;
-- 3. Chạy file tạo các Trigger
@triggers.sql;
COMMIT;
-- 4. Chạy file tạo các thủ tục/hàm
@procedures.sql;
COMMIT;

-- 5. Chay view
@get_all_accounts.sql;
COMMIT;

-- Xác nhận lưu toàn bộ thay đổi vào database
COMMIT;

select * from TAIKHOAN;
select * from NHANVIEN;
SELECT * FROM SANPHAM;
select * from NHACUNGCAP;
SELECT * FROM DANHMUC;
select * from KHO;
select * from PHIEUNHAP;
select * from CTPN;
select * from LOSANPHAM;
select * from KHACHHANG;
select * from HOADON;
select * from CTHD;
select * from PHIEUTRA_KH;
select * from CTPT_KH;
select * from DIEMTL;
select * from MEDICINE_CACHE;

SET SERVEROUTPUT ON;

UPDATE SANPHAM SET GIABAN = 100000 WHERE MASP = 'SP00001';
COMMIT;

@session1.sql;
COMMIT;

@session1_fix.sql;
COMMIT;

@sesson2.sql;
COMMIT;
