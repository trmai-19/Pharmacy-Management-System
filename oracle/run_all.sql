-- 0. Chạy file drop bảng nếu cần dọn dẹp database
@drop_all.sql;

-- 1. Chạy file tạo cấu trúc bảng (CREATE TABLE và Primary Key)
@database.sql;

-- 2. Chạy file tạo các ràng buộc (Khóa ngoại, Check, Default...)
-- @constraints.sql;

-- 3. Chạy file tạo các Trigger
-- @triggers.sql;

-- 4. Chạy file tạo các thủ tục/hàm
-- @procedures.sql;

-- Xác nhận lưu toàn bộ thay đổi vào database
COMMIT;