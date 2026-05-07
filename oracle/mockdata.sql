-- ============================================================
-- MOCK DATA - HỆ THỐNG QUẢN LÝ NHÀ THUỐC
-- Mật khẩu mặc định cho tất cả tài khoản test: Test@1234
-- Tài khoản Admin (do AdminSeeder tạo sẵn): SDT=0987654321 / admin123
-- Chạy file này SAU KHI đã khởi động app ít nhất 1 lần
-- (để AdminSeeder chạy và tạo tài khoản root admin)
-- ============================================================

-- BCrypt của 'Test@1234'
-- $2a$10$tMNHjP.DzAaQgx1wPiVtjO6TPL6K.oJbGbfGsUsIsMsoiDoyxW1Mm

-- ============================================================
-- BƯỚC 1: DANH MỤC (không FK)
-- ============================================================
INSERT INTO DANHMUC (MADM, TENDM, MOTA, TYLELOINHUAN)
VALUES ('DM0001', 'Kháng sinh', 'Các loại thuốc kháng sinh điều trị nhiễm khuẩn', 30);

INSERT INTO DANHMUC (MADM, TENDM, MOTA, TYLELOINHUAN)
VALUES ('DM0002', 'Giảm đau - Hạ sốt', 'Thuốc giảm đau, hạ sốt thông thường', 25);

INSERT INTO DANHMUC (MADM, TENDM, MOTA, TYLELOINHUAN)
VALUES ('DM0003', 'Vitamin & Khoáng chất', 'Vitamin và các chất khoáng bổ sung', 35);

INSERT INTO DANHMUC (MADM, TENDM, MOTA, TYLELOINHUAN)
VALUES ('DM0004', 'Tim mạch', 'Thuốc điều trị tim mạch, huyết áp', 28);

INSERT INTO DANHMUC (MADM, TENDM, MOTA, TYLELOINHUAN)
VALUES ('DM0005', 'Tiêu hóa', 'Thuốc hỗ trợ tiêu hóa, dạ dày', 27);

COMMIT;

-- ============================================================
-- BƯỚC 2: NHÀ CUNG CẤP (không FK)
-- ============================================================
INSERT INTO NHACUNGCAP (MANCC, TENNCC, SDT, EMAIL, DIACHI)
VALUES ('NCC0001', 'Công ty Dược phẩm ABC', '0281234567', 'contact@dpabc.com', '123 Nguyễn Văn Linh, Q7, TP.HCM');

INSERT INTO NHACUNGCAP (MANCC, TENNCC, SDT, EMAIL, DIACHI)
VALUES ('NCC0002', 'Công ty TNHH Pharma XYZ', '0289876543', 'sales@pharmaxyz.vn', '456 Lê Văn Việt, Q9, TP.HCM');

INSERT INTO NHACUNGCAP (MANCC, TENNCC, SDT, EMAIL, DIACHI)
VALUES ('NCC0003', 'Nhà phân phối MedTech', '0283456789', 'order@medtech.com.vn', '789 Đinh Tiên Hoàng, Q1, TP.HCM');

COMMIT;

-- ============================================================
-- BƯỚC 3: TÀI KHOẢN NHÂN VIÊN
-- VAITRO = 'STAFF' cho tất cả nhân viên
-- CHUCVU trong NHANVIEN mới là ADMIN/SALES_STAFF/WAREHOUSE_STAFF
-- ============================================================
-- Sales Staff 1
INSERT INTO TAIKHOAN (MATK, VAITRO, PASSWORD, SDT, NGAYTAO, IS_FIRST_LOGIN, EMAIL, TRANGTHAI)
VALUES ('TKNV260001', 'STAFF', '$2a$10$tMNHjP.DzAaQgx1wPiVtjO6TPL6K.oJbGbfGsUsIsMsoiDoyxW1Mm',
        '0901000001', SYSDATE, 0, 'sales1@pharmacy.test', 'ACTIVE');

-- Sales Staff 2
INSERT INTO TAIKHOAN (MATK, VAITRO, PASSWORD, SDT, NGAYTAO, IS_FIRST_LOGIN, EMAIL, TRANGTHAI)
VALUES ('TKNV260002', 'STAFF', '$2a$10$tMNHjP.DzAaQgx1wPiVtjO6TPL6K.oJbGbfGsUsIsMsoiDoyxW1Mm',
        '0901000002', SYSDATE, 0, 'sales2@pharmacy.test', 'ACTIVE');

-- Warehouse Staff
INSERT INTO TAIKHOAN (MATK, VAITRO, PASSWORD, SDT, NGAYTAO, IS_FIRST_LOGIN, EMAIL, TRANGTHAI)
VALUES ('TKNV260003', 'STAFF', '$2a$10$tMNHjP.DzAaQgx1wPiVtjO6TPL6K.oJbGbfGsUsIsMsoiDoyxW1Mm',
        '0901000003', SYSDATE, 0, 'warehouse1@pharmacy.test', 'ACTIVE');

-- Tài khoản test IS_FIRST_LOGIN=1 (để test API đổi mật khẩu lần đầu)
INSERT INTO TAIKHOAN (MATK, VAITRO, PASSWORD, SDT, NGAYTAO, IS_FIRST_LOGIN, EMAIL, TRANGTHAI)
VALUES ('TKNV260004', 'STAFF', '$2a$10$tMNHjP.DzAaQgx1wPiVtjO6TPL6K.oJbGbfGsUsIsMsoiDoyxW1Mm',
        '0901000004', SYSDATE, 1, 'newstaff@pharmacy.test', 'ACTIVE');

COMMIT;

-- ============================================================
-- BƯỚC 4: NHÂN VIÊN (FK → TAIKHOAN)
-- Lấy MATK từ AdminSeeder: cần query sau khi app đã chạy
-- AdminSeeder tạo account với SDT=0987654321, sau đó tạo employee
-- Ta cần biết MATK của admin để insert NV admin
-- Dùng subquery để lấy MATK
-- ============================================================

-- Lấy MATK admin từ tài khoản SDT=0987654321 (do AdminSeeder tạo)
-- Insert NV admin (AdminSeeder chưa set đầy đủ thông tin)
UPDATE NHANVIEN SET
    TENNV    = 'Nguyễn Văn Admin',
    GIOITINH = 'Nam',
    NGAYSINH = TO_DATE('1985-01-15', 'YYYY-MM-DD'),
    SDT      = '0987654321',
    CHUCVU   = 'ADMIN',
    TRANGTHAI = 'WORKING'
WHERE MATK = (SELECT MATK FROM TAIKHOAN WHERE SDT = '0987654321');

-- Sales Staff 1
INSERT INTO NHANVIEN (MANV, MATK, TENNV, GIOITINH, NGAYSINH, SDT, CHUCVU, TRANGTHAI)
VALUES ('NV260001', 'TKNV260001', 'Trần Thị Lan', 'Nữ',
        TO_DATE('1995-03-20', 'YYYY-MM-DD'), '0901000001', 'SALES_STAFF', 'WORKING');

-- Sales Staff 2
INSERT INTO NHANVIEN (MANV, MATK, TENNV, GIOITINH, NGAYSINH, SDT, CHUCVU, TRANGTHAI)
VALUES ('NV260002', 'TKNV260002', 'Lê Minh Tuấn', 'Nam',
        TO_DATE('1998-07-10', 'YYYY-MM-DD'), '0901000002', 'SALES_STAFF', 'WORKING');

-- Warehouse Staff
INSERT INTO NHANVIEN (MANV, MATK, TENNV, GIOITINH, NGAYSINH, SDT, CHUCVU, TRANGTHAI)
VALUES ('NV260003', 'TKNV260003', 'Phạm Văn Kho', 'Nam',
        TO_DATE('1993-11-05', 'YYYY-MM-DD'), '0901000003', 'WAREHOUSE_STAFF', 'WORKING');

-- NV cho tài khoản first-login test
INSERT INTO NHANVIEN (MANV, MATK, TENNV, GIOITINH, NGAYSINH, SDT, CHUCVU, TRANGTHAI)
VALUES ('NV260004', 'TKNV260004', 'Nhân Viên Mới', 'Nữ',
        TO_DATE('2000-05-15', 'YYYY-MM-DD'), '0901000004', 'SALES_STAFF', 'WORKING');

COMMIT;

-- ============================================================
-- BƯỚC 5: SẢN PHẨM (FK → DANHMUC)
-- IS_MANUAL_PRICE = 0: giá do trigger tự tính
-- IS_MANUAL_PRICE = 1: giá admin set thủ công (để test price suggestion)
-- ============================================================
INSERT INTO SANPHAM (MASP, MADM, TENSANPHAM, DVT, CONGDUNG, THANHPHAN, GIABAN, IS_MANUAL_PRICE)
VALUES ('SP00001', 'DM0001', 'Amoxicillin 500mg', 'Viên', 
        'Điều trị nhiễm khuẩn đường hô hấp, tiết niệu',
        'Amoxicillin trihydrate 500mg', 0, 0);

INSERT INTO SANPHAM (MASP, MADM, TENSANPHAM, DVT, CONGDUNG, THANHPHAN, GIABAN, IS_MANUAL_PRICE)
VALUES ('SP00002', 'DM0002', 'Paracetamol 500mg', 'Viên',
        'Hạ sốt, giảm đau nhẹ đến vừa',
        'Paracetamol 500mg', 0, 0);

INSERT INTO SANPHAM (MASP, MADM, TENSANPHAM, DVT, CONGDUNG, THANHPHAN, GIABAN, IS_MANUAL_PRICE)
VALUES ('SP00003', 'DM0003', 'Vitamin C 1000mg', 'Viên sủi',
        'Bổ sung Vitamin C, tăng sức đề kháng',
        'Acid ascorbic 1000mg', 0, 0);

INSERT INTO SANPHAM (MASP, MADM, TENSANPHAM, DVT, CONGDUNG, THANHPHAN, GIABAN, IS_MANUAL_PRICE)
VALUES ('SP00004', 'DM0004', 'Amlodipine 5mg', 'Viên',
        'Điều trị tăng huyết áp, đau thắt ngực',
        'Amlodipine besylate 6.93mg (tương đương 5mg amlodipine)', 0, 0);

INSERT INTO SANPHAM (MASP, MADM, TENSANPHAM, DVT, CONGDUNG, THANHPHAN, GIABAN, IS_MANUAL_PRICE)
VALUES ('SP00005', 'DM0002', 'Ibuprofen 400mg', 'Viên',
        'Giảm đau, kháng viêm, hạ sốt',
        'Ibuprofen 400mg', 85000, 1);
-- SP00005 set IS_MANUAL_PRICE=1 và GIABAN thủ công để test price suggestion API

COMMIT;

-- ============================================================
-- BƯỚC 6: LÔ SẢN PHẨM (FK → SANPHAM, DANHMUC)
-- Chú ý: TRG_CHECK_HSD_PERCENT yêu cầu (HSD - NGAYNHAP)/(HSD - NGAYSX) >= 20%
-- Chú ý: TRG_LOSANPHAM_AUTO_STATUS tự set TRANGTHAI
-- Ngày hiện tại: 2026-05-03
-- ============================================================
-- LO00001: SP00001 Amoxicillin - lô còn nhiều hàng, đang bán
-- vongdoi=730d, conlai=606d, percent=83% ✓
INSERT INTO LOSANPHAM (MALO, MASP, MADM, NGAYSX, NGAYNHAP, HSD, SLSP)
VALUES ('LO00001', 'SP00001', 'DM0001',
        TO_DATE('2025-01-01','YYYY-MM-DD'),
        TO_DATE('2026-03-01','YYYY-MM-DD'),
        TO_DATE('2027-01-01','YYYY-MM-DD'), 100);

-- LO00002: SP00002 Paracetamol - lô số lượng lớn
-- vongdoi=730d, conlai=760d... HSD>NGAYNHAP ✓ percent=104%? 
-- Thực ra conlai = 2027-06-01 - 2026-02-01 = 485d, vongdoi = 2027-06-01 - 2025-06-01 = 730d, percent=66% ✓
INSERT INTO LOSANPHAM (MALO, MASP, MADM, NGAYSX, NGAYNHAP, HSD, SLSP)
VALUES ('LO00002', 'SP00002', 'DM0002',
        TO_DATE('2025-06-01','YYYY-MM-DD'),
        TO_DATE('2026-02-01','YYYY-MM-DD'),
        TO_DATE('2027-06-01','YYYY-MM-DD'), 200);

-- LO00003: SP00003 Vitamin C - lô đang bán tốt
-- vongdoi = 2027-03-01 - 2025-03-01 = 730d
-- conlai  = 2027-03-01 - 2026-04-01 = 334d, percent=45.7% ✓
INSERT INTO LOSANPHAM (MALO, MASP, MADM, NGAYSX, NGAYNHAP, HSD, SLSP)
VALUES ('LO00003', 'SP00003', 'DM0003',
        TO_DATE('2025-03-01','YYYY-MM-DD'),
        TO_DATE('2026-04-01','YYYY-MM-DD'),
        TO_DATE('2027-03-01','YYYY-MM-DD'), 150);

-- LO00004: SP00004 Amlodipine - lô sắp hết hạn trong 6 tháng (để test expiry timeline)
-- vongdoi = 2026-12-01 - 2024-12-01 = 730d
-- conlai  = 2026-12-01 - 2026-01-01 = 334d, percent=45.7% ✓
INSERT INTO LOSANPHAM (MALO, MASP, MADM, NGAYSX, NGAYNHAP, HSD, SLSP)
VALUES ('LO00004', 'SP00004', 'DM0004',
        TO_DATE('2024-12-01','YYYY-MM-DD'),
        TO_DATE('2026-01-01','YYYY-MM-DD'),
        TO_DATE('2026-12-01','YYYY-MM-DD'), 80);

-- LO00005: SP00001 Amoxicillin - lô thứ 2, KHÔNG có giao dịch bán (để test dead stock)
-- vongdoi = 2027-09-01 - 2025-09-01 = 730d
-- conlai  = 2027-09-01 - 2026-04-01 = 487d, percent=66.7% ✓
INSERT INTO LOSANPHAM (MALO, MASP, MADM, NGAYSX, NGAYNHAP, HSD, SLSP)
VALUES ('LO00005', 'SP00001', 'DM0001',
        TO_DATE('2025-09-01','YYYY-MM-DD'),
        TO_DATE('2026-04-01','YYYY-MM-DD'),
        TO_DATE('2027-09-01','YYYY-MM-DD'), 50);

-- LO00006: SP00005 Ibuprofen - lô tồn kho thấp (để test reorder suggestion)
-- vongdoi = 2027-08-01 - 2025-08-01 = 730d
-- conlai  = 2027-08-01 - 2026-03-01 = 517d, percent=70.8% ✓
INSERT INTO LOSANPHAM (MALO, MASP, MADM, NGAYSX, NGAYNHAP, HSD, SLSP)
VALUES ('LO00006', 'SP00005', 'DM0002',
        TO_DATE('2025-08-01','YYYY-MM-DD'),
        TO_DATE('2026-03-01','YYYY-MM-DD'),
        TO_DATE('2027-08-01','YYYY-MM-DD'), 8);

COMMIT;

-- ============================================================
-- BƯỚC 7: KHO (FK → LOSANPHAM)
-- TRG_KHONGSUA_TONKHO chỉ block UPDATE, không block INSERT
-- ============================================================
INSERT INTO KHO (MAKHO, MALO, SLTON, DVSP) VALUES ('KHO001', 'LO00001', 80, 'Hộp');
INSERT INTO KHO (MAKHO, MALO, SLTON, DVSP) VALUES ('KHO002', 'LO00002', 180, 'Hộp');
INSERT INTO KHO (MAKHO, MALO, SLTON, DVSP) VALUES ('KHO003', 'LO00003', 150, 'Hộp');
INSERT INTO KHO (MAKHO, MALO, SLTON, DVSP) VALUES ('KHO004', 'LO00004', 75, 'Hộp');
INSERT INTO KHO (MAKHO, MALO, SLTON, DVSP) VALUES ('KHO005', 'LO00005', 50, 'Hộp');
INSERT INTO KHO (MAKHO, MALO, SLTON, DVSP) VALUES ('KHO006', 'LO00006', 8,  'Hộp');

COMMIT;

-- ============================================================
-- BƯỚC 8: PHIẾU NHẬP (FK → NHANVIEN, NHACUNGCAP)
-- ============================================================
-- Phiếu nhập đã hoàn tất (để trigger TRG_CAPNHAT_GIABAN kích hoạt)
INSERT INTO PHIEUNHAP (MAPN, MANV, MANCC, NGAYNHAP, TONGTIEN, TRANGTHAI)
VALUES ('PN26030001', 'NV260003', 'NCC0001',
        TO_DATE('2026-03-01','YYYY-MM-DD'), 0, 'HOANTAT');

INSERT INTO PHIEUNHAP (MAPN, MANV, MANCC, NGAYNHAP, TONGTIEN, TRANGTHAI)
VALUES ('PN26040001', 'NV260003', 'NCC0002',
        TO_DATE('2026-04-01','YYYY-MM-DD'), 0, 'HOANTAT');

-- Phiếu nhập đang khởi tạo (để test API lấy danh sách)
INSERT INTO PHIEUNHAP (MAPN, MANV, MANCC, NGAYNHAP, TONGTIEN, TRANGTHAI)
VALUES ('PN26050001', 'NV260003', 'NCC0003',
        TO_DATE('2026-05-01','YYYY-MM-DD'), 0, 'KHOI_TAO');

COMMIT;

-- ============================================================
-- BƯỚC 9: CHI TIẾT PHIẾU NHẬP (FK → PHIEUNHAP, LOSANPHAM)
-- TRG_CTPN_THANHTIEN tự tính THANHTIEN = SL * GIANHAP
-- TRG_CAPNHAT_GIABAN kích hoạt khi phiếu HOANTAT
-- ============================================================
-- Chi tiết PN26030001 (HOANTAT) → trigger sẽ cập nhật giá SP00001
INSERT INTO CTPN (MAPN, MALO, SL, GIANHAP, DVT, GHICHU)
VALUES ('PN26030001', 'LO00001', 100, 50000, 'Hộp', 'Nhập lô Amoxicillin tháng 3');

INSERT INTO CTPN (MAPN, MALO, SL, GIANHAP, DVT, GHICHU)
VALUES ('PN26030001', 'LO00005', 50, 50000, 'Hộp', 'Nhập lô Amoxicillin dự phòng');

-- Chi tiết PN26040001 (HOANTAT)
INSERT INTO CTPN (MAPN, MALO, SL, GIANHAP, DVT, GHICHU)
VALUES ('PN26040001', 'LO00002', 200, 2500, 'Hộp', 'Nhập Paracetamol số lượng lớn');

INSERT INTO CTPN (MAPN, MALO, SL, GIANHAP, DVT, GHICHU)
VALUES ('PN26040001', 'LO00003', 150, 15000, 'Hộp', 'Nhập Vitamin C');

INSERT INTO CTPN (MAPN, MALO, SL, GIANHAP, DVT, GHICHU)
VALUES ('PN26040001', 'LO00004', 80, 25000, 'Hộp', 'Nhập Amlodipine');

INSERT INTO CTPN (MAPN, MALO, SL, GIANHAP, DVT, GHICHU)
VALUES ('PN26040001', 'LO00006', 8, 65000, 'Hộp', 'Nhập Ibuprofen');

-- Chi tiết PN26050001 (KHOI_TAO - chưa hoàn tất)
-- Sẽ dùng để test POST /api/warehouse/import-receipts qua API
COMMIT;

-- ============================================================
-- BƯỚC 10: KHÁCH HÀNG (FK → TAIKHOAN optional)
-- ============================================================
-- KH không có tài khoản (tích điểm bằng SDT)
INSERT INTO KHACHHANG (MAKH, MATK, TENKH, GIOITINH, NGAYSINH, SDT, TONGDOANHTHU, DIEMTICHLUY, HANGTV)
VALUES ('KH2600001', NULL, 'Nguyễn Thị Hoa', 'Nữ',
        TO_DATE('1980-06-15','YYYY-MM-DD'), '0912000001', 0, 0, 'THANH VIEN');

-- KH tích điểm không tài khoản
INSERT INTO KHACHHANG (MAKH, MATK, TENKH, GIOITINH, NGAYSINH, SDT, TONGDOANHTHU, DIEMTICHLUY, HANGTV)
VALUES ('KH2600002', NULL, 'Trần Văn Bình', 'Nam',
        TO_DATE('1975-12-20','YYYY-MM-DD'), '0912000002', 0, 0, 'THANH VIEN');

-- KH có doanh thu cao để test hạng thành viên (Vàng = 10-50 triệu)
INSERT INTO KHACHHANG (MAKH, MATK, TENKH, GIOITINH, NGAYSINH, SDT, TONGDOANHTHU, DIEMTICHLUY, HANGTV)
VALUES ('KH2600003', NULL, 'Lê Thị Cúc', 'Nữ',
        TO_DATE('1965-03-08','YYYY-MM-DD'), '0912000003', 15000000, 150, 'Vàng');

COMMIT;

-- ============================================================
-- BƯỚC 11: HÓA ĐƠN (FK → NHANVIEN, KHACHHANG)
-- TRG_CTHD_CHECK_KHO_BEFORE kiểm tra tồn kho trước khi thêm CTHD
-- Nên insert HOADON trước, sau đó insert CTHD
-- TRANGTHAI: 'KHOI TAO' (space) theo code Java
-- ============================================================
-- HD đã hoàn tất - KH1
INSERT INTO HOADON (MAHD, MANV, MAKH, NGAYBAN, TONGTIEN, DIEMSUDUNG, TIENTHANHTOAN, TRANGTHAI)
VALUES ('HD26040001', 'NV260001', 'KH2600001',
        TO_DATE('2026-04-10','YYYY-MM-DD'), 0, 0, 0, 'HOANTAT');

-- HD đã hoàn tất - KH2
INSERT INTO HOADON (MAHD, MANV, MAKH, NGAYBAN, TONGTIEN, DIEMSUDUNG, TIENTHANHTOAN, TRANGTHAI)
VALUES ('HD26040002', 'NV260002', 'KH2600002',
        TO_DATE('2026-04-15','YYYY-MM-DD'), 0, 0, 0, 'HOANTAT');

-- HD đã hoàn tất - KH3 (dùng điểm)
INSERT INTO HOADON (MAHD, MANV, MAKH, NGAYBAN, TONGTIEN, DIEMSUDUNG, TIENTHANHTOAN, TRANGTHAI)
VALUES ('HD26050001', 'NV260001', 'KH2600003',
        TO_DATE('2026-05-01','YYYY-MM-DD'), 0, 50, 0, 'HOANTAT');

-- HD đang khởi tạo (để test danh sách)
INSERT INTO HOADON (MAHD, MANV, MAKH, NGAYBAN, TONGTIEN, DIEMSUDUNG, TIENTHANHTOAN, TRANGTHAI)
VALUES ('HD26050002', 'NV260002', 'KH2600001',
        TO_DATE('2026-05-03','YYYY-MM-DD'), 0, 0, 0, 'KHOI TAO');

COMMIT;

-- ============================================================
-- BƯỚC 12: CHI TIẾT HÓA ĐƠN (FK → HOADON, LOSANPHAM)
-- TRG_CTHD_THANHTIEN tự tính THANHTIEN = SL * DONGIA
-- TRG_CTHD_SYNC_KHO tự trừ tồn kho
-- TRG_CTHD_UPDATE_TONG_HD tự cập nhật TONGTIEN hóa đơn
-- Giá bán đã được trigger TRG_CAPNHAT_GIABAN set sau khi nhập:
--   SP00001 Amoxicillin: 50000 * 1.30 = 65000
--   SP00002 Paracetamol: 2500 * 1.25 = 3125
--   SP00003 Vitamin C:   15000 * 1.35 = 20250
--   SP00004 Amlodipine:  25000 * 1.28 = 32000
--   SP00006 Ibuprofen:   65000 * 1.25 = 81250 (nhưng IS_MANUAL_PRICE=1 nên = 85000)
-- ============================================================
-- HD26040001: mua Amoxicillin + Paracetamol
INSERT INTO CTHD (MAHD, MALO, SL, DONGIA, GHICHU)
VALUES ('HD26040001', 'LO00001', 10, 65000, NULL);

INSERT INTO CTHD (MAHD, MALO, SL, DONGIA, GHICHU)
VALUES ('HD26040001', 'LO00002', 20, 3125, NULL);

-- HD26040002: mua Vitamin C + Amlodipine
INSERT INTO CTHD (MAHD, MALO, SL, DONGIA, GHICHU)
VALUES ('HD26040002', 'LO00003', 5, 20250, NULL);

INSERT INTO CTHD (MAHD, MALO, SL, DONGIA, GHICHU)
VALUES ('HD26040002', 'LO00004', 30, 32000, NULL);

-- HD26050001: KH3 mua Amoxicillin + Paracetamol, dùng 50 điểm
INSERT INTO CTHD (MAHD, MALO, SL, DONGIA, GHICHU)
VALUES ('HD26050001', 'LO00001', 5, 65000, NULL);

INSERT INTO CTHD (MAHD, MALO, SL, DONGIA, GHICHU)
VALUES ('HD26050001', 'LO00002', 10, 3125, NULL);

COMMIT;

-- Cập nhật TIENTHANHTOAN cho HD26050001 (có dùng điểm)
-- TRG_HOADON_CHECK_DIEM tự xử lý khi UPDATE, nhưng do insert trực tiếp
-- cần update lại để đồng bộ
UPDATE HOADON SET TIENTHANHTOAN = TONGTIEN - 50 WHERE MAHD = 'HD26050001';
COMMIT;

-- ============================================================
-- BƯỚC 13: THANH TOÁN (FK → HOADON)
-- ============================================================
INSERT INTO THANHTOAN (MATT, MAHD, PHUONGTHUC, TRANGTHAI)
VALUES ('TT26040001', 'HD26040001', 'TIEN_MAT', 'HOANTAT');

INSERT INTO THANHTOAN (MATT, MAHD, PHUONGTHUC, TRANGTHAI)
VALUES ('TT26040002', 'HD26040002', 'CHUYEN_KHOAN', 'HOANTAT');

INSERT INTO THANHTOAN (MATT, MAHD, PHUONGTHUC, TRANGTHAI)
VALUES ('TT26050001', 'HD26050001', 'TIEN_MAT', 'HOANTAT');

COMMIT;

-- ============================================================
-- BƯỚC 14: ĐIỂM TÍCH LŨY thêm lịch sử cho KH3
-- (để test API loyalty history)
-- TRG_DIEMTL_UPDATE_KHACHHANG tự cập nhật DIEMTICHLUY trên KHACHHANG
-- Nhưng để mock data nhanh, insert thẳng vào DIEMTL
-- ============================================================
INSERT INTO DIEMTL (MADTL, MAKH, MAHD, DIEMTHAYDOI, NGAYGD, LOAIGD, GHICHU)
VALUES ('DTL26040001', 'KH2600003', 'HD26040002', 50,
        TO_DATE('2026-04-01','YYYY-MM-DD'), 'CONG_DIEM', 'Tích điểm từ hóa đơn cũ');

INSERT INTO DIEMTL (MADTL, MAKH, MAHD, DIEMTHAYDOI, NGAYGD, LOAIGD, GHICHU)
VALUES ('DTL26040002', 'KH2600003', 'HD26040001', 100,
        TO_DATE('2026-04-10','YYYY-MM-DD'), 'CONG_DIEM', 'Tích điểm từ hóa đơn cũ 2');

COMMIT;

-- ============================================================
-- BƯỚC 15: VERIFY - Kiểm tra dữ liệu đã insert
-- ============================================================
SELECT 'DANHMUC'    AS bang, COUNT(*) AS so_luong FROM DANHMUC    UNION ALL
SELECT 'NHACUNGCAP',       COUNT(*) FROM NHACUNGCAP               UNION ALL
SELECT 'TAIKHOAN',         COUNT(*) FROM TAIKHOAN                 UNION ALL
SELECT 'NHANVIEN',         COUNT(*) FROM NHANVIEN                 UNION ALL
SELECT 'SANPHAM',          COUNT(*) FROM SANPHAM                  UNION ALL
SELECT 'LOSANPHAM',        COUNT(*) FROM LOSANPHAM                UNION ALL
SELECT 'KHO',              COUNT(*) FROM KHO                      UNION ALL
SELECT 'KHACHHANG',        COUNT(*) FROM KHACHHANG                UNION ALL
SELECT 'PHIEUNHAP',        COUNT(*) FROM PHIEUNHAP                UNION ALL
SELECT 'CTPN',             COUNT(*) FROM CTPN                     UNION ALL
SELECT 'HOADON',           COUNT(*) FROM HOADON                   UNION ALL
SELECT 'CTHD',             COUNT(*) FROM CTHD                     UNION ALL
SELECT 'THANHTOAN',        COUNT(*) FROM THANHTOAN                UNION ALL
SELECT 'DIEMTL',           COUNT(*) FROM DIEMTL;

-- Kiểm tra giá bán sau khi trigger chạy
SELECT MASP, TENSANPHAM, GIABAN, IS_MANUAL_PRICE FROM SANPHAM ORDER BY MASP;

-- Kiểm tra tồn kho sau khi CTHD insert
SELECT k.MALO, k.SLTON, l.SLSP, s.TENSANPHAM
FROM KHO k
JOIN LOSANPHAM l ON k.MALO = l.MALO
JOIN SANPHAM s ON l.MASP = s.MASP
ORDER BY k.MALO;


-- Note: Không sử dụng phương thức tự cập nhất giá bán nữa, mình tự cho giá bán luôn 
-- nên là data cần phải tự nhập giá bán 

UPDATE SANPHAM
SET GIABAN = 50000, 
    IS_MANUAL_PRICE = 1
WHERE MASP = 'SP00001';


UPDATE SANPHAM
SET GIABAN = 60000, 
    IS_MANUAL_PRICE = 1
WHERE MASP = 'SP00002';
UPDATE SANPHAM
SET GIABAN = 70000, 
    IS_MANUAL_PRICE = 1
WHERE MASP = 'SP00004';

COMMIT;

