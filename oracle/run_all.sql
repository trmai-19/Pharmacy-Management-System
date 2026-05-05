-- 0. Chạy file drop bảng nếu cần dọn dẹp database
@drop_all.sql;
commit
-- 1. Chạy file tạo cấu trúc bảng (CREATE TABLE và Primary Key)
@database.sql;
commit
-- 2. Chạy file tạo các ràng buộc (Khóa ngoại, Check, Default...)
@constraints.sql;
commit
-- 3. Chạy file tạo các Trigger
@triggers.sql;
COMMIT
-- 4. Chạy file tạo các thủ tục/hàm
-- @procedures.sql;

-- 5. Chay Mock Data
@mockdata.sql;

-- Xác nhận lưu toàn bộ thay đổi vào database
COMMIT;