
-- ============================================
-- MOCK DATA FOR PHARMACY MANAGEMENT SYSTEM
-- Timeline: 2023 - 2025
-- ============================================

ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD';

-- ============================================
-- DANH MUC
-- ============================================
INSERT INTO DANHMUC VALUES ('DM001', 'Thuốc cảm', 'Điều trị cảm cúm', 'ACTIVE');
INSERT INTO DANHMUC VALUES ('DM002', 'Kháng sinh', 'Điều trị nhiễm khuẩn', 'ACTIVE');
INSERT INTO DANHMUC VALUES ('DM003', 'Vitamin', 'Bổ sung vitamin', 'ACTIVE');
INSERT INTO DANHMUC VALUES ('DM004', 'Tiêu hóa', 'Điều trị tiêu hóa', 'ACTIVE');
INSERT INTO DANHMUC VALUES ('DM005', 'Tim mạch', 'Thuốc tim mạch', 'ACTIVE');
INSERT INTO DANHMUC VALUES ('DM006', 'Da liễu', 'Thuốc da liễu', 'ACTIVE');
INSERT INTO DANHMUC VALUES ('DM007', 'Xương khớp', 'Điều trị xương khớp', 'ACTIVE');
INSERT INTO DANHMUC VALUES ('DM008', 'Hô hấp', 'Thuốc hô hấp', 'ACTIVE');
INSERT INTO DANHMUC VALUES ('DM009', 'Tiểu đường', 'Thuốc tiểu đường', 'ACTIVE');
INSERT INTO DANHMUC VALUES ('DM010', 'Mắt', 'Thuốc mắt', 'ACTIVE');

-- ============================================
-- KHO
-- ============================================
INSERT INTO KHO VALUES ('KHO001', 'TONG', 0, 'HOP');
INSERT INTO KHO VALUES ('KHO002', 'LANH', 0, 'HOP');
INSERT INTO KHO VALUES ('KHO003', 'DU_PHONG', 0, 'HOP');

-- ============================================
-- NHA CUNG CAP
-- ============================================
BEGIN
    FOR i IN 1..40 LOOP
        INSERT INTO NHACUNGCAP (
            MANCC,
            TENNCC,
            SDT,
            EMAIL,
            DIACHI
        ) VALUES (
            'NCC' || LPAD(i, 3, '0'),
            'Nha cung cap ' || i,
            '09' || LPAD(TRUNC(DBMS_RANDOM.VALUE(10000000,99999999)),8,'0'),
            'ncc' || i || '@gmail.com',
            'TP HCM Quan ' || TO_CHAR(MOD(i,12) + 1)
        );
    END LOOP;
END;
/

SELECT *
FROM NHACUNGCAP

-- ============================================
-- TAI KHOAN NHAN VIEN
-- Password: 123456
-- BCrypt hash
-- ============================================
BEGIN
    FOR i IN 1..20 LOOP
        INSERT INTO TAIKHOAN (
            MATK,
            VAITRO,
            PASSWORD,
            SDT,
            EMAIL,
            TRANGTHAI,
            IS_FIRST_LOGIN
        ) VALUES (
            'TKNV' || LPAD(i,3,'0'),
            CASE
                WHEN i <= 2 THEN 'ADMIN'
                WHEN i <= 5 THEN 'MANAGER'
                ELSE 'STAFF'
            END,
            '$2a$10$tMNHjP.DzAaQgx1wPiVtjO6TPL6K.oJbGbfGsUsIsMsoiDoyxW1Mm',
            '09' || LPAD(TRUNC(DBMS_RANDOM.VALUE(10000000,99999999)),8,'0'),
            'nhanvien' || i || '@gmail.com',
            'ACTIVE',
            0
        );
    END LOOP;
END;
/


-- ============================================
-- NHAN VIEN
-- ============================================
BEGIN
    FOR i IN 1..20 LOOP
        INSERT INTO NHANVIEN (
            MANV,
            MATK,
            TENNV,
            GIOITINH,
            NGAYSINH,
            SDT,
            CHUCVU,
            TRANGTHAI
        ) VALUES (
            'NV' || LPAD(i,3,'0'),
            'TKNV' || LPAD(i,3,'0'),
            'Nhân viên ' || i,
            CASE WHEN MOD(i,2)=0 THEN 'NAM' ELSE 'NU' END,
            DATE '1990-01-01' + TRUNC(DBMS_RANDOM.VALUE(0,5000)),
            '09' || LPAD(TRUNC(DBMS_RANDOM.VALUE(10000000,99999999)),8,'0'),
            CASE
                WHEN i <= 2 THEN 'ADMIN'
                WHEN i <= 5 THEN 'QUAN_LY'
                ELSE 'NHAN_VIEN'
            END,
            'ACTIVE'
        );
    END LOOP;
END;
/

-- ============================================
-- TAI KHOAN KHACH HANG
-- ============================================
BEGIN
    FOR i IN 1..2000 LOOP
        INSERT INTO TAIKHOAN (
            MATK,
            VAITRO,
            PASSWORD,
            SDT,
            EMAIL,
            TRANGTHAI,
            IS_FIRST_LOGIN
        ) VALUES (
            'TKKH' || LPAD(i,5,'0'),
            'CUSTOMER',
            '$2a$10$tMNHjP.DzAaQgx1wPiVtjO6TPL6K.oJbGbfGsUsIsMsoiDoyxW1Mm',
            '09' || LPAD(TRUNC(DBMS_RANDOM.VALUE(10000000,99999999)),8,'0'),
            'khachhang' || i || '@gmail.com',
            'ACTIVE',
            0
        );
    END LOOP;
END;
/

-- ============================================
-- KHACH HANG
-- ============================================
BEGIN
    FOR i IN 1..2000 LOOP
        INSERT INTO KHACHHANG (
            MAKH,
            MATK,
            TENKH,
            GIOITINH,
            NGAYSINH,
            SDT,
            TONGDOANHTHU,
            DIEMTICHLUY,
            HANGTV
        ) VALUES (
            'KH' || LPAD(i,5,'0'),
            'TKKH' || LPAD(i,5,'0'),
            'Khách hàng ' || i,
            CASE WHEN MOD(i,2)=0 THEN 'NAM' ELSE 'NU' END,
            DATE '1975-01-01' + TRUNC(DBMS_RANDOM.VALUE(0,15000)),
            '09' || LPAD(TRUNC(DBMS_RANDOM.VALUE(10000000,99999999)),8,'0'),
            0,
            0,
            'Bạc'
        );
    END LOOP;
END;
/

-- ============================================
-- SAN PHAM
-- ============================================
BEGIN
    FOR i IN 1..250 LOOP
        INSERT INTO SANPHAM (
            MASP,
            MADM,
            TENSANPHAM,
            DVT,
            CONGDUNG,
            THANHPHAN,
            GIABAN,
            TRANGTHAI
        ) VALUES (
            'SP' || LPAD(i,4,'0'),
            'DM' || LPAD(MOD(i,10)+1,3,'0'),
            'Sản phẩm thuốc ' || i,
            'HOP',
            'Công dụng sản phẩm ' || i,
            'Paracetamol, Vitamin C',
            0,
            'ACTIVE'
        );
    END LOOP;
END;
/

-- ============================================
-- LO SAN PHAM
-- ============================================
BEGIN
    FOR i IN 1..1500 LOOP
        INSERT INTO LOSANPHAM (
            MALO,
            MASP,
            MAKHO,
            NGAYSX,
            NGAYNHAP,
            HSD,
            SLSP,
            TRANGTHAI
        ) VALUES (
            'LO' || LPAD(i,5,'0'),
            'SP' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,251)),4,'0'),
            'KHO00' || TO_CHAR(TRUNC(DBMS_RANDOM.VALUE(1,4))),
            
            -- ngày sản xuất
            DATE '2023-01-01' + TRUNC(DBMS_RANDOM.VALUE(0,365)),
            
            -- ngày nhập
            DATE '2024-01-01' + TRUNC(DBMS_RANDOM.VALUE(0,365)),
            
            -- hạn sử dụng xa hơn
            DATE '2027-01-01' + TRUNC(DBMS_RANDOM.VALUE(0,1000)),
            
            TRUNC(DBMS_RANDOM.VALUE(50,500)),
            'CON_HANG'
        );
    END LOOP;
END;
/


-- ============================================
-- PHIEU NHAP
-- ============================================
BEGIN
    FOR i IN 1..1200 LOOP
        INSERT INTO PHIEUNHAP (
            MAPN,
            MANV,
            MANCC,
            NGAYNHAP,
            TONGTIEN,
            TRANGTHAI
        ) VALUES (
            'PN' || LPAD(i,5,'0'),
            'NV' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,21)),3,'0'),
            'NCC' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,41)),3,'0'),
            DATE '2023-01-01' + TRUNC(DBMS_RANDOM.VALUE(0,900)),
            0,
            'CHOXULY'
        );
    END LOOP;
END;
/
-- ============================================
-- CHI TIET PHIEU NHAP
-- Trigger sẽ tự cập nhật giá bán
-- ============================================
DECLARE
    v_malo VARCHAR2(20);
BEGIN
    FOR i IN 1..6000 LOOP

        v_malo := 'LO' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,1501)),5,'0');

        BEGIN
            INSERT INTO CTPN (
                MAPN,
                MALO,
                SL,
                GIANHAP,
                DVT,
                GHICHU,
                THANHTIEN
            ) VALUES (
                'PN' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,1201)),5,'0'),
                v_malo,
                TRUNC(DBMS_RANDOM.VALUE(10,100)),
                TRUNC(DBMS_RANDOM.VALUE(20000,500000)),
                'HOP',
                'Nhap hang dinh ky',
                0
            );

        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                NULL;
        END;

    END LOOP;
END;
/


-- ============================================
-- HOA DON
-- FLOW:
-- 1. TAO HOA DON -> CHOXULY
-- 2. THEM CTHD
-- 3. THANH TOAN
-- 4. HOAN TAT
-- ============================================

BEGIN
    FOR i IN 1..20000 LOOP
        INSERT INTO HOADON (
            MAHD,
            MANV,
            MAKH,
            NGAYBAN,
            TONGTIEN,
            DIEMSUDUNG,
            TIENTHANHTOAN,
            TRANGTHAI
        ) VALUES (
            'HD' || LPAD(i,6,'0'),
            'NV' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,21)),3,'0'),
            'KH' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,2001)),5,'0'),
            DATE '2023-01-01' + TRUNC(DBMS_RANDOM.VALUE(0,900)),
            0,
            0,
            0,
            'CHOXULY'
        );
    END LOOP;
END;
/

-- ============================================
-- CHI TIET HOA DON
-- ============================================

DECLARE
    v_mahd VARCHAR2(20);
    v_malo VARCHAR2(20);
    v_dongia NUMBER;
BEGIN

    FOR i IN 1..70000 LOOP

        v_mahd := 'HD' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,20001)),6,'0');

        v_malo := 'LO' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,1501)),5,'0');

        BEGIN

            SELECT SP.GIABAN
            INTO v_dongia
            FROM SANPHAM SP
            JOIN LOSANPHAM LSP
                ON SP.MASP = LSP.MASP
            WHERE LSP.MALO = v_malo
            FETCH FIRST 1 ROWS ONLY;

            INSERT INTO CTHD (
                MAHD,
                MALO,
                SL,
                DONGIA,
                THANHTIEN,
                GHICHU
            ) VALUES (
                v_mahd,
                v_malo,
                TRUNC(DBMS_RANDOM.VALUE(1,5)),
                NVL(v_dongia, 50000),
                0,
                'Ban tai quay'
            );

        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                NULL;

            WHEN NO_DATA_FOUND THEN
                NULL;

            WHEN OTHERS THEN
                NULL;
        END;

    END LOOP;

END;
/

-- ============================================
-- THANH TOAN
-- ============================================

BEGIN
    FOR i IN 1..20000 LOOP

        BEGIN

            INSERT INTO THANHTOAN (
                MATT,
                MAHD,
                PHUONGTHUC,
                TRANGTHAI
            ) VALUES (
                'TT' || LPAD(i,6,'0'),
                'HD' || LPAD(i,6,'0'),

                CASE
                    WHEN MOD(i,3)=0 THEN 'TIEN_MAT'
                    WHEN MOD(i,3)=1 THEN 'CHUYEN_KHOAN'
                    ELSE 'MOMO'
                END,

                'THANH_CONG'
            );

        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                NULL;
        END;

    END LOOP;
END;
/

-- ============================================
-- UPDATE HOA DON -> HOANTAT
-- ============================================

UPDATE HOADON
SET TRANGTHAI = 'HOANTAT'
WHERE TRANGTHAI = 'CHOXULY';

COMMIT;

-- ============================================
-- DIEM TICH LUY
-- ============================================

BEGIN

    FOR i IN 1..15000 LOOP

        BEGIN

            INSERT INTO DIEMTL (
                MADTL,
                MAKH,
                MAHD,
                DIEMTHAYDOI,
                NGAYGD,
                LOAIGD,
                GHICHU
            ) VALUES (
                'DTL' || LPAD(i,6,'0'),

                'KH' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,2001)),5,'0'),

                'HD' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,20001)),6,'0'),

                TRUNC(DBMS_RANDOM.VALUE(5,200)),

                DATE '2023-01-01'
                    + TRUNC(DBMS_RANDOM.VALUE(0,900)),

                CASE
                    WHEN MOD(i,5)=0 THEN 'TRU_DIEM'
                    ELSE 'CONG_DIEM'
                END,

                'Tich diem mua hang'
            );

        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                NULL;
        END;

    END LOOP;

END;
/

-- ============================================
-- PHIEU TRA KH
-- ============================================

BEGIN

    FOR i IN 1..500 LOOP

        INSERT INTO PHIEUTRA_KH (
            MAPT_KH,
            MAHD,
            MANV,
            NGAYTRA,
            LYDOTRA,
            TONGTIENHOAN
        ) VALUES (
            'PTKH' || LPAD(i,5,'0'),

            'HD' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,20001)),6,'0'),

            'NV' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,21)),3,'0'),

            DATE '2023-01-01'
                + TRUNC(DBMS_RANDOM.VALUE(0,900)),

            'Khach doi tra san pham',

            0
        );

    END LOOP;

END;
/

-- ============================================
-- CHI TIET PHIEU TRA KH
-- ============================================


BEGIN

    FOR i IN 1..1200 LOOP

        BEGIN

            INSERT INTO CTPT_KH (
                MAPT_KH,
                MALO,
                SL,
                DONGIAHOAN,
                THANHTIEN
            ) VALUES (
                'PTKH' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,501)),5,'0'),

                'LO' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,1501)),5,'0'),

                TRUNC(DBMS_RANDOM.VALUE(1,3)),

                TRUNC(DBMS_RANDOM.VALUE(20000,500000)),

                0
            );

        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                NULL;
        END;

    END LOOP;

END;
/

SELECT *
FROM PHIEUTRA_KH


COMMIT;

-- ============================================
-- PHIEU TRA NCC
-- ============================================


BEGIN

    FOR i IN 1..150 LOOP

        INSERT INTO PHIEUTRA_NCC (
            MAPT_NCC,
            MAPN,
            MANV,
            NGAYTRA,
            LYDOTRA,
            TONGTIEN
        ) VALUES (
            'PTNCC' || LPAD(i,5,'0'),

            'PN' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,1201)),5,'0'),

            'NV' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,21)),3,'0'),

            DATE '2023-01-01'
                + TRUNC(DBMS_RANDOM.VALUE(0,900)),

            'Thuoc loi hoac het han',

            0
        );

    END LOOP;

END;

/

-- ============================================
-- CHI TIET PHIEU TRA NCC
-- ============================================



BEGIN

    FOR i IN 1..500 LOOP

        BEGIN

            INSERT INTO CTPT_NCC (
                MAPT_NCC,
                MALO,
                SL,
                DONGIATRA,
                THANHTIEN
            ) VALUES (
                'PTNCC' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,151)),5,'0'),

                'LO' || LPAD(TRUNC(DBMS_RANDOM.VALUE(1,1501)),5,'0'),

                TRUNC(DBMS_RANDOM.VALUE(1,10)),

                TRUNC(DBMS_RANDOM.VALUE(20000,500000)),

                0
            );

        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                NULL;
        END;

    END LOOP;

END;
/



COMMIT;