CREATE OR REPLACE PROCEDURE SP_THANHTOAN_HOADON (
    p_mahd IN HOADON.MAHD%TYPE,
    p_diemsudung IN NUMBER,
    p_phuongthuc IN THANHTOAN.PHUONGTHUC%TYPE
)
AS
    v_trangthai HOADON.TRANGTHAI%TYPE;
BEGIN
    -- Thiết lập điểm neo giao tác [6]
    SAVEPOINT sp_thanhtoan_start;

    -- Kiểm tra trạng thái hóa đơn và khóa dòng (FOR UPDATE) để tránh Lost Update [6]
    SELECT TRANGTHAI INTO v_trangthai FROM HOADON WHERE MAHD = p_mahd FOR UPDATE;

    IF v_trangthai = 'HOANTAT' THEN
        RAISE_APPLICATION_ERROR(-20051, 'Lỗi: Hóa đơn này đã được thanh toán trước đó!');
    END IF;

    -- 1. Cập nhật hóa đơn (Trigger TRG_HOADON_CHECK_DIEM sẽ tự động bắt lỗi nếu điểm > 50%)
    UPDATE HOADON
    SET DIEMSUDUNG = p_diemsudung,
        TRANGTHAI = 'HOANTAT'
    WHERE MAHD = p_mahd;

    -- 2. Khởi tạo bản ghi thanh toán (Để NULL giá trị khóa chính nếu đã cài Trigger sinh mã tự động)
    INSERT INTO THANHTOAN (MATT, MAHD, PHUONGTHUC, TRANGTHAI)
    VALUES (NULL, p_mahd, p_phuongthuc, 'THANH_CONG');

    -- Xác nhận giao tác [6]
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK TO sp_thanhtoan_start;
        RAISE_APPLICATION_ERROR(-20052, 'Thanh toán thất bại. Lỗi hệ thống: ' || SQLERRM);
END;
/


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
/

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
/