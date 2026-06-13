
CREATE OR REPLACE PROCEDURE SP_THANHTOAN_HOADON (
    p_mahd IN HOADON.MAHD%TYPE,
    p_diemsudung IN NUMBER,
    p_phuongthuc IN THANHTOAN.PHUONGTHUC%TYPE
)
AS
    v_trangthai HOADON.TRANGTHAI%TYPE;
BEGIN
    SAVEPOINT sp_thanhtoan_start;

    SELECT TRANGTHAI INTO v_trangthai FROM HOADON WHERE MAHD = p_mahd FOR UPDATE;

    IF v_trangthai = 'HOANTAT' THEN
        RAISE_APPLICATION_ERROR(-20051, 'Lỗi: Hóa đơn này đã được thanh toán trước đó!');
    END IF;

    UPDATE HOADON
    SET DIEMSUDUNG = p_diemsudung,
        TRANGTHAI = 'HOANTAT'
    WHERE MAHD = p_mahd;

    INSERT INTO THANHTOAN (MATT, MAHD, PHUONGTHUC, TRANGTHAI)
    VALUES (NULL, p_mahd, p_phuongthuc, 'THANH_CONG');

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK TO sp_thanhtoan_start;
        RAISE_APPLICATION_ERROR(-20052, 'Thanh toán thất bại. Lỗi hệ thống: ' || SQLERRM);
END;



CREATE OR REPLACE PROCEDURE SP_QUET_LOTHUOC_HETHAN (
    p_so_lo_capnhat OUT NUMBER
)
AS
    -- Khai báo con trỏ để quét các lô thuốc có HSD nhỏ hơn ngày hiện tại
    CURSOR cur_lothuoc IS
        SELECT MALO, TRANGTHAI
        FROM LOSANPHAM
        WHERE HSD < TRUNC(SYSDATE) AND TRANGTHAI != 'HET_HAN'
        FOR UPDATE;
BEGIN
    p_so_lo_capnhat := 0;

    -- Duyệt qua từng dòng dữ liệu trong con trỏ [6]
    FOR rec IN cur_lothuoc LOOP
        UPDATE LOSANPHAM
        SET TRANGTHAI = 'HET_HAN'
        WHERE CURRENT OF cur_lothuoc; -- Cập nhật trực tiếp trên vị trí con trỏ đang đứng

        p_so_lo_capnhat := p_so_lo_capnhat + 1;
    END LOOP;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20053, 'Lỗi trong quá trình kiểm kê lô: ' || SQLERRM);
END;


CREATE OR REPLACE PROCEDURE SP_TAO_PHIEUTRA_KH (
    p_mahd IN HOADON.MAHD%TYPE,
    p_manv IN NHANVIEN.MANV%TYPE,
    p_malo IN LOSANPHAM.MALO%TYPE,
    p_sl_tra IN NUMBER
)
AS
    v_dongiahoan CTHD.DONGIA%TYPE;
    v_mapt VARCHAR2(20);
BEGIN
    SAVEPOINT sp_trahang;

    -- 1. Xác thực hàng hóa và lấy giá bán ban đầu trên hóa đơn gốc
    BEGIN
        SELECT DONGIA INTO v_dongiahoan
        FROM CTHD
        WHERE MAHD = p_mahd AND MALO = p_malo;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20054, 'Lỗi: Sản phẩm này không tồn tại trong hóa đơn gốc!');
    END;

    -- 2. Tạo phiếu trả khách hàng (Mã phiếu tự động gen NULL cho Trigger xử lý)
    INSERT INTO PHIEUTRA_KH (MAPT_KH, MAHD, MANV, NGAYTRA, LYDOTRA, TONGTIENHOAN)
    VALUES (NULL, p_mahd, p_manv, SYSDATE, 'Khách hoàn trả sản phẩm', 0)
    RETURNING MAPT_KH INTO v_mapt; -- Lấy mã phiếu vừa sinh ra

    -- 3. Tạo chi tiết phiếu trả (Trigger đồng bộ kho và trừ doanh thu sẽ tự động chạy)
    INSERT INTO CTPT_KH (MAPT_KH, MALO, SL, DONGIAHOAN, THANHTIEN)
    VALUES (v_mapt, p_malo, p_sl_tra, v_dongiahoan, p_sl_tra * v_dongiahoan);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK TO sp_trahang;
        RAISE_APPLICATION_ERROR(-20055, 'Xử lý trả hàng thất bại: ' || SQLERRM);
END;


-- Tự động cập nhật điểm tích lũy: Quy tắc: 10,000 VNĐ = 1 Điểm. Chỉ cập nhật khi hóa đơn HOANTAT.
CREATE OR REPLACE PROCEDURE SP_CAPNHAT_DIEM_TICHLUY (
    p_mahd IN HOADON.MAHD%TYPE
)
AS
    v_tongtien HOADON.TONGTIEN%TYPE;
    v_makh HOADON.MAKH%TYPE;
    v_diem_cong NUMBER;
BEGIN
    SELECT TONGTIEN, MAKH INTO v_tongtien, v_makh 
    FROM HOADON 
    WHERE MAHD = p_mahd AND TRANGTHAI = 'HOANTAT';

    IF v_makh IS NOT NULL THEN
        v_diem_cong := FLOOR(v_tongtien / 10000);
        
        UPDATE KHACHHANG
        SET DIEMTICHLUY = DIEMTICHLUY + v_diem_cong
        WHERE MAKH = v_makh;
        
        COMMIT;
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20101, 'Hóa đơn không tồn tại hoặc chưa thanh toán xong!');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20102, 'Lỗi cập nhật điểm: ' || SQLERRM);
END;



--Tự động cập nhật điểm tích lũy (SP_CAPNHAT_DIEM_TICHLUY)
--Quy tắc: 10,000 VNĐ = 1 Điểm. Chỉ cập nhật khi hóa đơn HOANTAT.

CREATE OR REPLACE PROCEDURE SP_CAPNHAT_DIEM_TICHLUY (
    p_mahd IN HOADON.MAHD%TYPE
)
AS
    v_tongtien HOADON.TONGTIEN%TYPE;
    v_makh HOADON.MAKH%TYPE;
    v_diem_cong NUMBER;
BEGIN
    SELECT TONGTIEN, MAKH INTO v_tongtien, v_makh 
    FROM HOADON 
    WHERE MAHD = p_mahd AND TRANGTHAI = 'HOANTAT';

    IF v_makh IS NOT NULL THEN
        v_diem_cong := FLOOR(v_tongtien / 10000);
        
        UPDATE KHACHHANG
        SET DIEMTICHLUY = DIEMTICHLUY + v_diem_cong
        WHERE MAKH = v_makh;
        
        COMMIT;
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20101, 'Hóa đơn không tồn tại hoặc chưa thanh toán xong!');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20102, 'Lỗi cập nhật điểm: ' || SQLERRM);
END;



--Hủy hóa đơn & Hoàn kho (SP_HUY_HOADON)
--Hủy hóa đơn chưa thanh toán, quét qua các chi tiết hóa đơn để trả lại số lượng tồn cho Lô sản phẩm tương ứng.

CREATE OR REPLACE PROCEDURE SP_HUY_HOADON (
    p_mahd IN HOADON.MAHD%TYPE
)
AS
    v_trangthai HOADON.TRANGTHAI%TYPE;
    CURSOR cur_cthd IS 
        SELECT MALO, SL FROM CTHD WHERE MAHD = p_mahd FOR UPDATE;
BEGIN
    SAVEPOINT sp_huy_hd;

    SELECT TRANGTHAI INTO v_trangthai FROM HOADON WHERE MAHD = p_mahd;
    IF v_trangthai != 'CHO_THANH_TOAN' THEN
        RAISE_APPLICATION_ERROR(-20103, 'Chỉ được hủy hóa đơn ở trạng thái CHO_THANH_TOAN!');
    END IF;

    FOR rec IN cur_cthd LOOP
        UPDATE LOSANPHAM
        SET SLTON = SLTON + rec.SL
        WHERE MALO = rec.MALO;
    END LOOP;

    UPDATE HOADON SET TRANGTHAI = 'DA_HUY' WHERE MAHD = p_mahd;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK TO sp_huy_hd;
        RAISE_APPLICATION_ERROR(-20104, 'Hủy hóa đơn thất bại: ' || SQLERRM);
END;

--Đăng ký khách hàng mới (SP_DANGKY_KHACHHANG_MOI)

CREATE OR REPLACE PROCEDURE SP_DANGKY_KHACHHANG_MOI (
    p_tenkh IN KHACHHANG.TENKH%TYPE,
    p_sdt IN KHACHHANG.SDT%TYPE,
    p_gioitinh IN KHACHHANG.GIOITINH%TYPE
)
AS
    v_count NUMBER;
BEGIN
    -- Check trùng SDT
    SELECT COUNT(*) INTO v_count FROM KHACHHANG WHERE SDT = p_sdt;
    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20105, 'Số điện thoại này đã được đăng ký!');
    END IF;

    INSERT INTO KHACHHANG (MAKH, TENKH, SDT, GIOITINH, DIEMTICHLUY, HANG)
    VALUES (NULL, p_tenkh, p_sdt, p_gioitinh, 0, 'Thường'); -- MAKH có thể Trigger/Sequence tự lo

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20106, 'Đăng ký khách hàng lỗi: ' || SQLERRM);
END;


--Nhập kho mới & Tạo Lô (SP_NHAP_KHO_MOI)

CREATE OR REPLACE PROCEDURE SP_NHAP_KHO_MOI (
    p_maphieunhap IN PHIEUNHAP.MAPN%TYPE,
    p_masp IN SANPHAM.MASP%TYPE,
    p_slnhap IN NUMBER,
    p_hsd IN DATE,
    p_gianhap IN NUMBER
)
AS
BEGIN
    SAVEPOINT sp_nhapkho;

    -- 1. Tạo lô sản phẩm mới
    INSERT INTO LOSANPHAM (MALO, MASP, SLTON, HSD, GIANHAP, TRANGTHAI)
    VALUES (NULL, p_masp, p_slnhap, p_hsd, p_gianhap, 'BINH_THUONG');

    -- 2. Cập nhật số lượng tổng vào Kho thuốc (Hoặc tự động qua Trigger nếu đã làm)
    UPDATE WAREHOUSE -- (Hoặc bảng KHO tùy thiết kế)
    SET SLTON = SLTON + p_slnhap
    WHERE LOAIKHO = 'THUOC'; 

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK TO sp_nhapkho;
        RAISE_APPLICATION_ERROR(-20201, 'Nhập kho thất bại: ' || SQLERRM);
END;


--Điều chuyển số lượng giữa Lô và Quầy (SP_DIEU_CHUYEN_KHO)

CREATE OR REPLACE PROCEDURE SP_DIEU_CHUYEN_KHO (
    p_malo IN LOSANPHAM.MALO%TYPE,
    p_sl_chuyen IN NUMBER
)
AS
    v_sl_hientai NUMBER;
BEGIN
    SAVEPOINT sp_dieuchuyen;
    
    SELECT SLTON INTO v_sl_hientai FROM LOSANPHAM WHERE MALO = p_malo FOR UPDATE;
    
    IF v_sl_hientai < p_sl_chuyen THEN
        RAISE_APPLICATION_ERROR(-20202, 'Số lượng tồn không đủ để điều chuyển!');
    END IF;

    -- Trừ ở kho tổng
    UPDATE LOSANPHAM SET SLTON = SLTON - p_sl_chuyen WHERE MALO = p_malo;
    
    -- Ghi nhận vào quầy (Giả sử có bảng tồn tại quầy)
    -- UPDATE QUAY_THUOC SET SL = SL + p_sl_chuyen WHERE MASP = ...
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK TO sp_dieuchuyen;
        RAISE_APPLICATION_ERROR(-20203, 'Điều chuyển lỗi: ' || SQLERRM);
END;

--Cảnh báo tồn kho thấp (SP_CANH_BAO_TON_KHO_THAP)
--Dùng SYS_REFCURSOR trả về danh sách sản phẩm để gọi lên màn hình cảnh báo.


CREATE OR REPLACE PROCEDURE SP_CANH_BAO_TON_KHO_THAP (
    p_nguong_canh_bao IN NUMBER,
    p_cursor OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN p_cursor FOR
        SELECT SP.MASP, SP.TENSP, SUM(LO.SLTON) AS TONG_TON
        FROM SANPHAM SP
        JOIN LOSANPHAM LO ON SP.MASP = LO.MASP
        WHERE LO.TRANGTHAI != 'HET_HAN'
        GROUP BY SP.MASP, SP.TENSP
        HAVING SUM(LO.SLTON) < p_nguong_canh_bao;
END;

--Kiểm kê Lô Hết Hạn & Chuyển Trạng Thái (SP_XU_LY_THUOC_HET_HAN)
--(Bổ sung thêm logic đưa vào bảng xuất hủy so với bản cũ)

CREATE OR REPLACE PROCEDURE SP_XU_LY_THUOC_HET_HAN
AS
    CURSOR cur_lothuoc IS
        SELECT MALO, SLTON 
        FROM LOSANPHAM 
        WHERE HSD < TRUNC(SYSDATE) AND TRANGTHAI != 'HET_HAN' 
        FOR UPDATE;
BEGIN
    FOR rec IN cur_lothuoc LOOP
        -- Đổi trạng thái lô
        UPDATE LOSANPHAM 
        SET TRANGTHAI = 'HET_HAN', SLTON = 0 
        WHERE CURRENT OF cur_lothuoc;

        -- Ghi log vào bảng tiêu hủy (Nếu có)
        -- INSERT INTO PHIEU_TIEU_HUY (MAPTH, MALO, SL, NGAY) VALUES (NULL, rec.MALO, rec.SLTON, SYSDATE);
    END LOOP;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20204, 'Lỗi quét hàng hết hạn: ' || SQLERRM);
END;


--Tạo phiếu đặt hàng gửi Nhà Cung Cấp (SP_TAO_PHIEU_DAT_HANG_NCC)
--Khởi tạo một phiếu đặt hàng mới khi kho báo sắp hết thuốc.

CREATE OR REPLACE PROCEDURE SP_TAO_PHIEU_DAT_HANG_NCC (
    p_mancc IN NHACUNGCAP.MANCC%TYPE,
    p_manv IN NHANVIEN.MANV%TYPE,
    p_masp IN SANPHAM.MASP%TYPE,
    p_sl_dat IN NUMBER,
    p_dongia_dukien IN NUMBER
)
AS
    v_mapd VARCHAR2(20);
BEGIN
    SAVEPOINT sp_taophieudat;

    -- 1. Tạo phiếu đặt hàng gốc (Mã tự gen qua Trigger hoặc Sequence)
    INSERT INTO PHIEUDAT (MAPD, MANCC, MANV, NGAYDAT, TRANGTHAI, TONGTIEN)
    VALUES (NULL, p_mancc, p_manv, SYSDATE, 'CHO_XAC_NHAN', p_sl_dat * p_dongia_dukien)
    RETURNING MAPD INTO v_mapd;

    -- 2. Thêm chi tiết phiếu đặt
    INSERT INTO CT_PHIEUDAT (MAPD, MASP, SLDAT, DONGIADAT)
    VALUES (v_mapd, p_masp, p_sl_dat, p_dongia_dukien);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK TO sp_taophieudat;
        RAISE_APPLICATION_ERROR(-20301, 'Tạo phiếu đặt hàng thất bại: ' || SQLERRM);
END;

--Ghi nhận thanh toán công nợ cho NCC (SP_THANHTOAN_CONG_NO_NCC)
--Thanh toán tiền cho NCC và trừ thẳng vào tổng nợ hiện tại.

CREATE OR REPLACE PROCEDURE SP_THANHTOAN_CONG_NO_NCC (
    p_mancc IN NHACUNGCAP.MANCC%TYPE,
    p_sotien_tra IN NUMBER,
    p_nguoichi IN NHANVIEN.MANV%TYPE
)
AS
    v_congno_hientai NHACUNGCAP.CONGNO%TYPE;
BEGIN
    SAVEPOINT sp_thanhtoan_ncc;

    -- Lấy công nợ hiện tại và khóa dòng (FOR UPDATE) để tránh đụng độ
    SELECT CONGNO INTO v_congno_hientai 
    FROM NHACUNGCAP 
    WHERE MANCC = p_mancc FOR UPDATE;

    IF v_congno_hientai < p_sotien_tra THEN
        RAISE_APPLICATION_ERROR(-20302, 'Số tiền trả lớn hơn công nợ hiện tại!');
    END IF;

    -- Trừ nợ
    UPDATE NHACUNGCAP 
    SET CONGNO = CONGNO - p_sotien_tra 
    WHERE MANCC = p_mancc;

    -- Lưu lịch sử chi tiền (Nếu có bảng PHIEUCHI)
    -- INSERT INTO PHIEUCHI (MAPC, MANCC, MANV, SOTIEN, NGAYCHI, NOIDUNG) 
    -- VALUES (NULL, p_mancc, p_nguoichi, p_sotien_tra, SYSDATE, 'Thanh toán công nợ NCC');

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20303, 'Không tìm thấy thông tin Nhà cung cấp!');
    WHEN OTHERS THEN
        ROLLBACK TO sp_thanhtoan_ncc;
        RAISE_APPLICATION_ERROR(-20304, 'Thanh toán công nợ thất bại: ' || SQLERRM);
END;

--Trả hàng cho Nhà Cung Cấp (SP_TAO_PHIEUTRA_NCC)
--Khi lô thuốc bị lỗi hoặc cận date, xuất kho trả lại NCC và trừ số lượng tồn.

CREATE OR REPLACE PROCEDURE SP_TAO_PHIEUTRA_NCC (
    p_mancc IN NHACUNGCAP.MANCC%TYPE,
    p_manv IN NHANVIEN.MANV%TYPE,
    p_malo IN LOSANPHAM.MALO%TYPE,
    p_sl_tra IN NUMBER,
    p_lydo IN VARCHAR2
)
AS
    v_sl_ton_hientai NUMBER;
    v_mapt VARCHAR2(20);
BEGIN
    SAVEPOINT sp_tra_ncc;

    -- 1. Kiểm tra tồn kho của lô hàng
    SELECT SLTON INTO v_sl_ton_hientai FROM LOSANPHAM WHERE MALO = p_malo FOR UPDATE;
    
    IF v_sl_ton_hientai < p_sl_tra THEN
        RAISE_APPLICATION_ERROR(-20305, 'Số lượng trong lô không đủ để trả nhà cung cấp!');
    END IF;

    -- 2. Trừ số lượng tồn kho
    UPDATE LOSANPHAM SET SLTON = SLTON - p_sl_tra WHERE MALO = p_malo;

    -- 3. Tạo phiếu trả
    INSERT INTO PHIEUTRA_NCC (MAPT_NCC, MANCC, MANV, NGAYTRA, LYDOTRA, TRANGTHAI)
    VALUES (NULL, p_mancc, p_manv, SYSDATE, p_lydo, 'DA_XUAT_KHO')
    RETURNING MAPT_NCC INTO v_mapt;

    -- 4. Tạo chi tiết phiếu trả
    INSERT INTO CTPT_NCC (MAPT_NCC, MALO, SLTRA)
    VALUES (v_mapt, p_malo, p_sl_tra);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK TO sp_tra_ncc;
        RAISE_APPLICATION_ERROR(-20306, 'Lỗi trả hàng NCC: ' || SQLERRM);
END;

--Xử lý đổi mật khẩu an toàn (SP_DOI_MAT_KHAU)
--Check mật khẩu cũ phải khớp thì mới cho update mật khẩu mới (Mật khẩu nên được mã hóa Hash từ Spring Boot truyền xuống).

CREATE OR REPLACE PROCEDURE SP_DOI_MAT_KHAU (
    p_sdt_taikhoan IN TAIKHOAN.SDT%TYPE,
    p_matkhau_cu IN TAIKHOAN.PASSWORD%TYPE,
    p_matkhau_moi IN TAIKHOAN.PASSWORD%TYPE
)
AS
    v_matkhau_hientai TAIKHOAN.PASSWORD%TYPE;
BEGIN
    -- Lấy mật khẩu hiện tại trong DB
    SELECT PASSWORD INTO v_matkhau_hientai 
    FROM TAIKHOAN 
    WHERE SDT = p_sdt_taikhoan;

    -- Kiểm tra khớp mật khẩu
    IF v_matkhau_hientai != p_matkhau_cu THEN
        RAISE_APPLICATION_ERROR(-20401, 'Mật khẩu cũ không chính xác!');
    END IF;

    -- Đổi mật khẩu
    UPDATE TAIKHOAN 
    SET PASSWORD = p_matkhau_moi, 
        IS_FIRST_LOGIN = 0 
    WHERE SDT = p_sdt_taikhoan;

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20402, 'Tài khoản không tồn tại!');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20403, 'Lỗi đổi mật khẩu: ' || SQLERRM);
END;

--Phân quyền vai trò nhân viên (SP_PHAN_QUYEN_NHANVIEN)
--Thay đổi quyền (Role) của một tài khoản trên hệ thống.

CREATE OR REPLACE PROCEDURE SP_PHAN_QUYEN_NHANVIEN (
    p_matk IN TAIKHOAN.MATK%TYPE,
    p_vaitro_moi IN TAIKHOAN.VAITRO%TYPE
)
AS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM TAIKHOAN WHERE MATK = p_matk;
    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20404, 'Mã tài khoản không tồn tại trong hệ thống!');
    END IF;

    -- Kiểm tra tính hợp lệ của vai trò
    IF p_vaitro_moi NOT IN ('ADMIN', 'MANAGER', 'STAFF') THEN
        RAISE_APPLICATION_ERROR(-20405, 'Vai trò phân quyền không hợp lệ!');
    END IF;

    UPDATE TAIKHOAN 
    SET VAITRO = p_vaitro_moi 
    WHERE MATK = p_matk;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20406, 'Lỗi phân quyền: ' || SQLERRM);
END;

--Ghi Nhật ký hệ thống (SP_GHI_LOG_HETHONG)
--Đẩy các thao tác quan trọng (Xóa, Hủy, Sửa) vào bảng Log để Admin kiểm tra sau này.

CREATE OR REPLACE PROCEDURE SP_GHI_LOG_HETHONG (
    p_matk IN TAIKHOAN.MATK%TYPE,
    p_hanhdong IN VARCHAR2, -- Vd: 'HUY_HOA_DON', 'XOA_SAN_PHAM'
    p_noidung IN VARCHAR2
)
AS
BEGIN
    INSERT INTO SYSTEM_LOG (MALOG, MATK, THOIGIAN, HANHDONG, NOIDUNG)
    VALUES (NULL, p_matk, SYSDATE, p_hanhdong, p_noidung);
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        -- Hàm này thường được gọi trong các Exception khác, nên có thể không cần throw thêm lỗi để tránh đứt gãy luồng chính.
        DBMS_OUTPUT.PUT_LINE('Lỗi ghi log: ' || SQLERRM);
END;


--Áp dụng Khuyến mãi vào Hóa đơn (SP_APDUNG_KHUYENMAI)
--Nghiệp vụ: Kiểm tra mã giảm giá (còn hạn không, còn lượt dùng không, đơn hàng có đủ điều kiện tối thiểu không) rồi mới trừ tiền.

CREATE OR REPLACE PROCEDURE SP_APDUNG_KHUYENMAI (
    p_mahd IN HOADON.MAHD%TYPE,
    p_makm IN KHUYENMAI.MAKM%TYPE
)
AS
    v_tongtien HOADON.TONGTIEN%TYPE;
    v_dieukien KHUYENMAI.DIEUKIENTOITHIEU%TYPE;
    v_giatrigiam KHUYENMAI.GIATRIGIAM%TYPE;
    v_loaikm KHUYENMAI.LOAIKM%TYPE; -- 'PHAN_TRAM' hoặc 'TIEN_MAT'
    v_soluong KHUYENMAI.SOLUONG%TYPE;
    v_hsd DATE;
    v_tien_sau_giam NUMBER;
BEGIN
    SAVEPOINT sp_khuyenmai;

    -- 1. Lấy thông tin hóa đơn
    SELECT TONGTIEN INTO v_tongtien FROM HOADON WHERE MAHD = p_mahd;

    -- 2. Lấy thông tin khuyến mãi và khóa dòng
    SELECT DIEUKIENTOITHIEU, GIATRIGIAM, LOAIKM, SOLUONG, HSD 
    INTO v_dieukien, v_giatrigiam, v_loaikm, v_soluong, v_hsd
    FROM KHUYENMAI WHERE MAKM = p_makm FOR UPDATE;

    -- 3. Kiểm tra các điều kiện khắc nghiệt
    IF v_soluong <= 0 THEN
        RAISE_APPLICATION_ERROR(-20501, 'Mã khuyến mãi đã hết lượt sử dụng!');
    END IF;
    IF v_hsd < TRUNC(SYSDATE) THEN
        RAISE_APPLICATION_ERROR(-20502, 'Mã khuyến mãi đã hết hạn!');
    END IF;
    IF v_tongtien < v_dieukien THEN
        RAISE_APPLICATION_ERROR(-20503, 'Hóa đơn chưa đạt giá trị tối thiểu để áp dụng mã này!');
    END IF;

    -- 4. Tính toán số tiền sau giảm
    IF v_loaikm = 'PHAN_TRAM' THEN
        v_tien_sau_giam := v_tongtien - (v_tongtien * v_giatrigiam / 100);
    ELSE
        v_tien_sau_giam := v_tongtien - v_giatrigiam;
    END IF;
    
    IF v_tien_sau_giam < 0 THEN v_tien_sau_giam := 0; END IF;

    -- 5. Cập nhật lại Hóa đơn và giảm số lượng Mã khuyến mãi
    UPDATE HOADON SET TONGTIEN = v_tien_sau_giam, MAKM = p_makm WHERE MAHD = p_mahd;
    UPDATE KHUYENMAI SET SOLUONG = SOLUONG - 1 WHERE MAKM = p_makm;

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20504, 'Dữ liệu không tồn tại!');
    WHEN OTHERS THEN
        ROLLBACK TO sp_khuyenmai;
        RAISE_APPLICATION_ERROR(-20505, 'Áp dụng khuyến mãi thất bại: ' || SQLERRM);
END;

--Kiểm kê kho (SP_KIEM_KE_KHO)
--Nghiệp vụ: So sánh số lượng thực tế nhân viên đếm được với số lượng trong DB. Nếu lệch, tự động tạo phiếu kiểm kê và cập nhật lại DB.

CREATE OR REPLACE PROCEDURE SP_KIEM_KE_KHO (
    p_manv IN NHANVIEN.MANV%TYPE,
    p_malo IN LOSANPHAM.MALO%TYPE,
    p_sl_thucte IN NUMBER,
    p_ghichu IN VARCHAR2
)
AS
    v_sl_hethong NUMBER;
    v_chenhlech NUMBER;
BEGIN
    SAVEPOINT sp_kiemke;

    -- Lấy số lượng hệ thống hiện tại
    SELECT SLTON INTO v_sl_hethong FROM LOSANPHAM WHERE MALO = p_malo FOR UPDATE;

    -- Nếu có chênh lệch thì mới xử lý
    IF v_sl_hethong != p_sl_thucte THEN
        v_chenhlech := p_sl_thucte - v_sl_hethong;

        -- 1. Lưu lịch sử kiểm kê
        INSERT INTO PHIEUKIEMKE (MAPKK, MANV, MALO, NGAYKK, SL_HETHONG, SL_THUCTE, CHENHLECH, GHICHU)
        VALUES (NULL, p_manv, p_malo, SYSDATE, v_sl_hethong, p_sl_thucte, v_chenhlech, p_ghichu);

        -- 2. Ép số lượng hệ thống bằng với số thực tế
        UPDATE LOSANPHAM SET SLTON = p_sl_thucte WHERE MALO = p_malo;
    END IF;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK TO sp_kiemke;
        RAISE_APPLICATION_ERROR(-20506, 'Kiểm kê thất bại: ' || SQLERRM);
END;

--Tính toán Lợi nhuận thuần (SP_TINH_TOAN_LOI_NHUAN_THUAN)
--Nghiệp vụ: Tính toán chính xác tiền lời bằng cách lấy [Tiền bán cho khách] trừ đi [Giá gốc lúc nhập từ NCC] của TỪNG viên thuốc đã bán.

CREATE OR REPLACE PROCEDURE SP_TINH_TOAN_LOI_NHUAN_THUAN (
    p_tungay IN DATE,
    p_denngay IN DATE,
    p_loinhuan OUT NUMBER
)
AS
BEGIN
    -- Công thức: SUM (Số lượng bán * (Giá bán - Giá nhập))
    SELECT NVL(SUM(CT.SL * (CT.DONGIA - LO.GIANHAP)), 0)
    INTO p_loinhuan
    FROM CTHD CT
    JOIN HOADON HD ON CT.MAHD = HD.MAHD
    JOIN LOSANPHAM LO ON CT.MALO = LO.MALO
    WHERE HD.TRANGTHAI = 'HOANTAT'
      AND HD.NGAYTAO BETWEEN p_tungay AND p_denngay;
      
EXCEPTION
    WHEN OTHERS THEN
        p_loinhuan := 0;
        RAISE_APPLICATION_ERROR(-20601, 'Lỗi tính toán lợi nhuận: ' || SQLERRM);
END;

--Thống kê Hàng sắp hết hạn (SP_THONGKE_HANG_SAP_HET_HAN)
--Nghiệp vụ: Trả ra danh sách các lô thuốc sẽ hết hạn trong X ngày tới (thường là 30, 60, 90 ngày).

CREATE OR REPLACE PROCEDURE SP_THONGKE_HANG_SAP_HET_HAN (
    p_songay_toihan IN NUMBER,
    p_cursor OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN p_cursor FOR
        SELECT 
            SP.MASP, 
            SP.TENSP, 
            LO.MALO, 
            LO.SLTON, 
            LO.HSD,
            (LO.HSD - TRUNC(SYSDATE)) AS CON_LAI_SO_NGAY
        FROM LOSANPHAM LO
        JOIN SANPHAM SP ON LO.MASP = SP.MASP
        WHERE LO.HSD > TRUNC(SYSDATE) 
          AND LO.HSD <= TRUNC(SYSDATE) + p_songay_toihan
          AND LO.TRANGTHAI = 'BINH_THUONG'
        ORDER BY LO.HSD ASC;
END;

--Thống kê doanh thu theo NGÀY (SP_THONGKE_DOANHTHU_NGAY)
--Nghiệp vụ: Dùng để chốt sổ cuối ngày, xuất báo cáo xem ngày nào đông khách nhất.

CREATE OR REPLACE PROCEDURE SP_THONGKE_DOANHTHU_NGAY (
    p_thang IN NUMBER,
    p_nam IN NUMBER,
    p_cursor OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN p_cursor FOR
        SELECT 
            TO_CHAR(NGAYTAO, 'DD/MM/YYYY') AS NGAY, 
            COUNT(MAHD) AS SO_DON_HANG,
            SUM(TONGTIEN) AS TONG_DOANH_THU
        FROM HOADON
        WHERE EXTRACT(MONTH FROM NGAYTAO) = p_thang 
          AND EXTRACT(YEAR FROM NGAYTAO) = p_nam
          AND TRANGTHAI = 'HOANTAT'
        GROUP BY TO_CHAR(NGAYTAO, 'DD/MM/YYYY')
        ORDER BY TO_DATE(NGAY, 'DD/MM/YYYY');
END;

