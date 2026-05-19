-- chạy đoạn này mới hiển thị được trong frontend 
CREATE OR REPLACE VIEW V_DANH_SACH_TAI_KHOAN AS
SELECT 
    tk.SDT AS username,
    NVL(nv.TENNV, 'Chưa cập nhật tên') AS ownerName, 
    tk.VAITRO AS role,
    tk.TRANGTHAI AS status,
    tk.EMAIL AS email,
    'EMPLOYEE' AS accountType
FROM TAIKHOAN tk
LEFT JOIN NHANVIEN nv ON tk.MATK = nv.MATK
WHERE tk.VAITRO = 'STAFF' OR tk.VAITRO = 'ADMIN'

UNION ALL

SELECT 
    tk.SDT AS username,
    NVL(kh.TENKH, 'Khách hàng vãng lai') AS ownerName, 
    tk.VAITRO AS role,
    tk.TRANGTHAI AS status,
    tk.EMAIL AS email,
    'CUSTOMER' AS accountType
FROM TAIKHOAN tk
LEFT JOIN KHACHHANG kh ON tk.MATK = kh.MATK
WHERE tk.VAITRO = 'CUSTOMER';

