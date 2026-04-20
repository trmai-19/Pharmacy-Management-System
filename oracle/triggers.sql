-- TRIGGER Cập nhật giá bán
CREATE OR REPLACE TRIGGER TRG_CAPNHAT_GIABAN
AFTER INSERT OR UPDATE OF GIANHAP ON CTPN
FOR EACH ROW
DECLARE
    v_masp VARCHAR2(20);
    v_madm VARCHAR2(20);
    v_tyle NUMBER;
    v_is_manual NUMBER;
    v_new_price NUMBER;
BEGIN
    -- Lấy mã SP từ bảng LOSANPHAM
    SELECT MASP INTO v_masp FROM LOSANPHAM WHERE MALO = :NEW.MALO;

    -- Lấy mã danh mục và cờ nhập giá tay từ SANPHAM
    SELECT MADM, IS_MANUAL_PRICE INTO v_madm, v_is_manual FROM SANPHAM WHERE MASP = v_masp;

    -- Nếu sản phẩm không phải nhập tay (Mục 3: Auto set)
    IF v_is_manual = 0 THEN
        -- Lấy tỷ lệ lợi nhuận của danh mục
        SELECT TYLELOINHUAN INTO v_tyle FROM DANHMUC WHERE MADM = v_madm;

        IF v_tyle IS NOT NULL THEN
            v_new_price := :NEW.GIANHAP * v_tyle;
        ELSE
            v_new_price := :NEW.GIANHAP; -- Nếu không có tỷ lệ, set theo giá nhập
        END IF;

        -- Mục 1: Kiểm tra không cho giá bán NULL
        IF v_new_price IS NULL THEN
            RAISE_APPLICATION_ERROR(-20001, 'Lỗi: Giá bán tính toán bị NULL!');
        END IF;

        -- Cập nhật giá bán mới vào bảng SANPHAM
        UPDATE SANPHAM SET GIABAN = v_new_price WHERE MASP = v_masp;
    END IF;
END;

-- TRIGGER Không cho xóa khách hàng đã có giao dịch
CREATE OR REPLACE TRIGGER TRG_KHONGXOA_KHACHHANG
BEFORE DELETE ON KHACHHANG
FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM HOADON WHERE MAKH = :OLD.MAKH;
    
    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Lỗi: Không thể xóa khách hàng đã có lịch sử giao dịch!');
    END IF;
END;

-- TRIGGER Không cho xóa nhà cung cấp đã từng nhập hàng
CREATE OR REPLACE TRIGGER TRG_KHONGXOA_NHACUNGCAP
BEFORE DELETE ON NHACUNGCAP
FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM PHIEUNHAP WHERE MANCC = :OLD.MANCC;
    
    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Lỗi: Không thể xóa nhà cung cấp đã từng nhập hàng!');
    END IF;
END;

-- TRIGGER Không cho sửa tồn kho
CREATE OR REPLACE TRIGGER TRG_KHONGSUA_TONKHO
BEFORE UPDATE OF SLTON ON KHO
FOR EACH ROW
BEGIN
    RAISE_APPLICATION_ERROR(-20004, 'Lỗi: Không được phép sửa số lượng tồn kho trực tiếp!');
END;

-- TRIGGER Cập nhật tổng doanh thu khách hàng và hạng thành viên
CREATE OR REPLACE TRIGGER TRG_CAPNHAT_DOANHTHU_HANGTV
AFTER INSERT OR UPDATE OF TIENTHANHTOAN ON HOADON
FOR EACH ROW
DECLARE
    v_doanhthu_cu NUMBER;
    v_tong_doanhthu NUMBER;
    v_hang_moi VARCHAR2(50);
BEGIN
    -- Chỉ thực thi nếu hóa đơn này có định danh khách hàng
    IF :NEW.MAKH IS NOT NULL THEN
        -- 1. Lấy mức doanh thu hiện tại của khách hàng
        SELECT NVL(TONGDOANHTHU, 0) INTO v_doanhthu_cu FROM KHACHHANG WHERE MAKH = :NEW.MAKH;

        -- 2. Tính toán tổng doanh thu mới sau khi mua hàng
        IF INSERTING THEN
            v_tong_doanhthu := v_doanhthu_cu + NVL(:NEW.TIENTHANHTOAN, 0);
        ELSIF UPDATING THEN
            v_tong_doanhthu := v_doanhthu_cu - NVL(:OLD.TIENTHANHTOAN, 0) + NVL(:NEW.TIENTHANHTOAN, 0);
        END IF;

        -- 3. Xét hạng thành viên dựa trên mốc doanh thu
        IF v_tong_doanhthu < 10000000 THEN
            v_hang_moi := 'Bạc';
        ELSIF v_tong_doanhthu >= 10000000 AND v_tong_doanhthu < 50000000 THEN
            v_hang_moi := 'Vàng';
        ELSE
            v_hang_moi := 'Kim Cương';
        END IF;

        -- 4. Cập nhật lại 2 thuộc tính cùng 1 lúc vào bảng Khách Hàng
        UPDATE KHACHHANG 
        SET TONGDOANHTHU = v_tong_doanhthu, 
            HANGTV = v_hang_moi 
        WHERE MAKH = :NEW.MAKH;
    END IF;
END;