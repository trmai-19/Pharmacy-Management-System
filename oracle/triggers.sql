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


--Thêm xóa sửa cho CTHD -> sửa số lượng tồn kho
CREATE OR REPLACE TRIGGER TRG_CTHD_SYNC_KHO
AFTER INSERT OR UPDATE OR DELETE ON CTHD
FOR EACH ROW
DECLARE
    v_diff NUMBER;
BEGIN
    IF INSERTING THEN
        UPDATE LOSANPHAM 
        SET SLSP = SLSP - :NEW.SL 
        WHERE MALO = :NEW.MALO;

        UPDATE KHO 
        SET SLTON = SLTON - :NEW.SL 
        WHERE MALO = :NEW.MALO;

    ELSIF DELETING THEN
        UPDATE LOSANPHAM 
        SET SLSP = SLSP + :OLD.SL 
        WHERE MALO = :OLD.MALO;
        
        UPDATE KHO 
        SET SLTON = SLTON + :OLD.SL 
        WHERE MALO = :OLD.MALO;

    ELSIF UPDATING THEN

        v_diff := :NEW.SL - :OLD.SL;

        UPDATE LOSANPHAM 
        SET SLSP = SLSP - v_diff 
        WHERE MALO = :NEW.MALO;
        
        UPDATE KHO 
        SET SLTON = SLTON - v_diff 
        WHERE MALO = :NEW.MALO;
    END IF;

    FOR r IN (SELECT SLSP FROM LOSANPHAM WHERE MALO = NVL(:NEW.MALO, :OLD.MALO)) LOOP
        IF r.SLSP < 0 THEN
            RAISE_APPLICATION_ERROR(-20005, 'Lỗi: Số lượng tồn kho không đủ để thực hiện thao tác này!');
        END IF;
    END LOOP;
END;
/

--CTHD.THANHTIEN = SL*DONGIA
CREATE OR REPLACE TRIGGER TRG_CTHD_THANHTIEN
BEFORE INSERT OR UPDATE ON CTHD
FOR EACH ROW
BEGIN
    :NEW.THANHTIEN := NVL(:NEW.SL, 0) * NVL(:NEW.DONGIA, 0);
END;
/

--HOADON.TONGTIEN = SUM(CTHD.THANHTIEN)
CREATE OR REPLACE TRIGGER TRG_CTHD_UPDATE_TONG_HD
AFTER INSERT OR UPDATE OR DELETE ON CTHD
FOR EACH ROW
DECLARE
    v_mahd VARCHAR2(20);
BEGIN
    IF DELETING THEN
        v_mahd := :OLD.MAHD;
    ELSE
        v_mahd := :NEW.MAHD;
    END IF;

    UPDATE HOADON 
    SET TONGTIEN = (SELECT NVL(SUM(THANHTIEN), 0) FROM CTHD WHERE MAHD = v_mahd)
    WHERE MAHD = v_mahd;
END;
/


--Số lượng bán không được lớn hơn số lượng trong kho (còn hsd)
CREATE OR REPLACE TRIGGER TRG_CTHD_CHECK_KHO_BEFORE
BEFORE INSERT OR UPDATE ON CTHD
FOR EACH ROW
DECLARE
    v_ton_kho NUMBER;
    v_hsd DATE;
BEGIN
    BEGIN
        SELECT SLSP, HSD INTO v_ton_kho, v_hsd
        FROM LOSANPHAM 
        WHERE MALO = :NEW.MALO;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20010, 'Lỗi: Mã lô ' || :NEW.MALO || ' không tồn tại!');
    END;

    IF v_hsd < TRUNC(SYSDATE) THEN
        RAISE_APPLICATION_ERROR(-20013, 'Lỗi: Thuốc thuộc lô ' || :NEW.MALO || ' đã hết hạn sử dụng!');
    END IF;

    IF INSERTING THEN
        IF :NEW.SL > v_ton_kho THEN
            RAISE_APPLICATION_ERROR(-20011, 'Lỗi: Không đủ hàng trong kho! (Tồn: ' || v_ton_kho || ')');
        END IF;
    ELSIF UPDATING THEN
        IF (:NEW.SL - :OLD.SL) > v_ton_kho THEN
            RAISE_APPLICATION_ERROR(-20012, 'Lỗi: Số lượng cập nhật vượt quá tồn kho hiện tại!');
        END IF;
    END IF;
END;
/

--điểm sử dụng <= 50% tổng tiền && HD.TIENTHANHTOAN = HD.TONGTIEN - HD.DIEMSUDUNG
CREATE OR REPLACE TRIGGER TRG_HOADON_CHECK_DIEM
BEFORE INSERT OR UPDATE ON HOADON
FOR EACH ROW
DECLARE
    v_diem_hien_co NUMBER;
BEGIN
    IF NVL(:NEW.DIEMSUDUNG, 0) > (NVL(:NEW.TONGTIEN, 0) * 0.5) THEN
        RAISE_APPLICATION_ERROR(-20020, 
            'Lỗi: Điểm sử dụng (' || :NEW.DIEMSUDUNG || ') không được vượt quá 50% tổng tiền (' || (:NEW.TONGTIEN * 0.5) || ')');
    END IF;

    SELECT d.SL INTO v_diem_hien_co
    FROM DIEMTL d
    JOIN KHACHHANG k ON d.MADTL = k.MADTL
    WHERE k.MAKH = :NEW.MAKH;

    IF NVL(:NEW.DIEMSUDUNG, 0) > v_diem_hien_co THEN
        RAISE_APPLICATION_ERROR(-20021, 
            'Lỗi: Khách hàng không đủ điểm. Hiện có: ' || v_diem_hien_co || ', yêu cầu dùng: ' || :NEW.DIEMSUDUNG);
    END IF;

    
    :NEW.TIENTHANHTOAN := NVL(:NEW.TONGTIEN, 0) - NVL(:NEW.DIEMSUDUNG, 0);
END;
/

-- DIEMTL.SL = DIEMTL.SL + 1%*HD.TONGTIEN - HD.DIEMSUDUNG
CREATE OR REPLACE TRIGGER TRG_HOADON_UPDATE_DIEM_SAU_BAN
AFTER INSERT OR UPDATE ON HOADON
FOR EACH ROW
BEGIN
    UPDATE DIEMTL
    SET SL = SL + (NVL(:NEW.TONGTIEN, 0) * 0.01) - NVL(:NEW.DIEMSUDUNG, 0)
    WHERE MADTL = (SELECT MADTL FROM KHACHHANG WHERE MAKH = :NEW.MAKH);
END;
/


--TRIGGER LOG CTPN
CREATE OR REPLACE TRIGGER TRG_LOG_CTPN
AFTER INSERT OR UPDATE OR DELETE ON CTPN
FOR EACH ROW
DECLARE
    v_action VARCHAR2(50);
    v_details VARCHAR2(1000);
BEGIN
    IF INSERTING THEN
        v_action := 'INSERT';
        v_details := 'Nhập mới lô ' || :NEW.MALO || ': SL ' || :NEW.SL || ', Giá ' || :NEW.GIANHAP;
    ELSIF UPDATING THEN
        v_action := 'UPDATE';
        v_details := 'Sửa lô ' || :NEW.MALO || ': SL cũ ' || :OLD.SL || ' -> mới ' || :NEW.SL;
    ELSE
        v_action := 'DELETE';
        v_details := 'Xóa dòng nhập lô ' || :OLD.MALO || ' khỏi phiếu ' || :OLD.MAPN;
    END IF;

    INSERT INTO LOG_NHAP_HANG (MAPN, MALO, HANH_DONG, NGUOI_THUC_HIEN, NOI_DUNG_CHI_TIET)
    VALUES (NVL(:NEW.MAPN, :OLD.MAPN), NVL(:NEW.MALO, :OLD.MALO), v_action, USER, v_details);
END;
/

--TRIGGER LOG CTHD
CREATE OR REPLACE TRIGGER TRG_LOG_CTHD
AFTER INSERT OR UPDATE OR DELETE ON CTHD
FOR EACH ROW
DECLARE
    v_action VARCHAR2(50);
    v_details VARCHAR2(1000);
BEGIN
    IF INSERTING THEN
        v_action := 'INSERT';
        v_details := 'Bán lô ' || :NEW.MALO || ': SL ' || :NEW.SL || ', Đơn giá ' || :NEW.DONGIA;
    ELSIF UPDATING THEN
        v_action := 'UPDATE';
        v_details := 'Sửa bán lô ' || :NEW.MALO || ': SL cũ ' || :OLD.SL || ' -> ' || :NEW.SL;
    ELSE
        v_action := 'DELETE';
        v_details := 'Hủy bán lô ' || :OLD.MALO || ' (Hoàn kho)';
    END IF;

    INSERT INTO LOG_BAN_HANG (MAHD, MALO, HANH_DONG, NGUOI_THUC_HIEN, NOI_DUNG_CHI_TIET)
    VALUES (NVL(:NEW.MAHD, :OLD.MAHD), NVL(:NEW.MALO, :OLD.MALO), v_action, USER, v_details);
END;
/


-- TRIGGER trả hàng cho nhà cung cấp 

CREATE OR REPLACE TRIGGER TRG_CTPT_NCC_THANHTIEN
BEFORE INSERT OR UPDATE ON CTPT_NCC
FOR EACH ROW
BEGIN

    :NEW.THANHTIEN := NVL(:NEW.SL, 0) * NVL(:NEW.DONGIATRA, 0);
END;
/

CREATE OR REPLACE TRIGGER TRG_CTPT_NCC_SUBTRACT_STOCK
AFTER INSERT ON CTPT_NCC
FOR EACH ROW
DECLARE
    v_ton_kho NUMBER;
BEGIN

    SELECT SLSP INTO v_ton_kho 
    FROM LOSANPHAM 
    WHERE MALO = :NEW.MALO;

    IF v_ton_kho < :NEW.SL THEN
        RAISE_APPLICATION_ERROR(-20060, 
            'Lỗi: Lô hàng ' || :NEW.MALO || ' chỉ còn ' || v_ton_kho || ' sản phẩm. Không đủ để trả số lượng ' || :NEW.SL);
    END IF;

    UPDATE LOSANPHAM 
    SET SLSP = SLSP - :NEW.SL 
    WHERE MALO = :NEW.MALO;

    UPDATE KHO 
    SET SLTON = SLTON - :NEW.SL 
    WHERE MALO = :NEW.MALO;

    INSERT INTO LOG_NHAP_HANG (MAPN, MALO, HANH_DONG, NGUOI_THUC_HIEN, NOI_DUNG_CHI_TIET)
    VALUES (:NEW.MAPN, :NEW.MALO, 'RETURN_TO_NCC', USER, 
            'Xuất trả NCC: SP ' || :NEW.MASP || ', SL: ' || :NEW.SL);
END;
/

--TRIGGER cập nhật tổng tiền phiếu trả NCC sau khi thêm/sửa/xóa chi tiết trả hàng
CREATE OR REPLACE TRIGGER TRG_CTPT_NCC_TOTAL_UPDATE
AFTER INSERT OR UPDATE OR DELETE ON CTPT_NCC
FOR EACH ROW
DECLARE
    v_mapt VARCHAR2(20);
BEGIN
    v_mapt := NVL(:NEW.MAPT_NCC, :OLD.MAPT_NCC);

    UPDATE PHIEUTRA_NCC 
    SET TONGTIEN = (SELECT NVL(SUM(THANHTIEN), 0) FROM CTPT_NCC WHERE MAPT_NCC = v_mapt)
    WHERE MAPT_NCC = v_mapt;
END;
/


--TRIGGER Thêm xóa sửa chi tiết phiếu nhập -> sửa số lượng tồn của kho.(CTPN & KHO.TONKHO)

--thêm chi tiết phiếu nhập 
CREATE OR REPLACE TRIGGER TRG_PN_HOANTAT_KHO
AFTER UPDATE OF TRANGTHAI ON PHIEUNHAP
FOR EACH ROW
WHEN (NEW.TRANGTHAI = 'HOANTAT')
DECLARE
BEGIN
    FOR r IN (
        SELECT MALO, SL
        FROM CTPN
        WHERE MAPN = :NEW.MAPN
    )
    LOOP
        UPDATE KHO
        SET SLTON = SLTON + r.SL
        WHERE MALO = r.MALO;
    END LOOP;
END;
/
--chặn sửa và xóa chi tiết phiếu nhập khi phiếu nhập đã done 
CREATE OR REPLACE TRIGGER TRG_KHOA_CTPN
BEFORE UPDATE OR DELETE ON CTPN
FOR EACH ROW
DECLARE
    v_trangthai VARCHAR2(20);
BEGIN
    SELECT TRANGTHAI INTO v_trangthai
    FROM PHIEUNHAP
    WHERE MAPN = :OLD.MAPN;

    IF v_trangthai = 'HOANTAT' THEN
        RAISE_APPLICATION_ERROR(-20030, 
        'Phiếu nhập đã hoàn tất, không được chỉnh sửa');
    END IF;
END;
/
-- không cho thêm chi tiết phiếu nhập vào phiếu nhập đã done
CREATE OR REPLACE TRIGGER TRG_CHECK_INSERT_CTPN
BEFORE INSERT ON CTPN
FOR EACH ROW
DECLARE
    v_trangthai VARCHAR2(20);
BEGIN
    SELECT TRANGTHAI INTO v_trangthai
    FROM PHIEUNHAP
    WHERE MAPN = :NEW.MAPN;

    IF v_trangthai = 'HOANTAT' THEN
        RAISE_APPLICATION_ERROR(-20031, 
        'Không thể thêm chi tiết vào phiếu đã hoàn tất');
    END IF;
END;
/
-- TRIGGERCTPN.THANHTIEN = SL*DONGIA
CREATE OR REPLACE TRIGGER TRG_CTPN_THANHTIEN
BEFORE INSERT OR UPDATE ON CTPN
FOR EACH ROW
BEGIN
    :NEW.THANHTIEN := :NEW.SL * :NEW.GIANHAP;
END;
/
-- TRIGGER PHIEUNHAP.TONGTIEN = SUM(CTPN.THANHTIEN)
CREATE OR REPLACE TRIGGER TRG_PN_TONGTIEN
AFTER INSERT OR UPDATE OR DELETE ON CTPN
FOR EACH ROW
BEGIN
    UPDATE PHIEUNHAP
    SET TONGTIEN = (
        SELECT NVL(SUM(THANHTIEN),0)
        FROM CTPN
        WHERE MAPN = NVL(:NEW.MAPN, :OLD.MAPN)
    )
    WHERE MAPN = NVL(:NEW.MAPN, :OLD.MAPN);
END;
/
--TRIGGER Kiểm tra khoảng cách HSD hợp lý
--bài này dựa dựa trên giải pháp tính vòng đời, so sánh số ngày còn lại với vòng đời,
--tính phần trăm, nếu nó còn ngắn quá thì kh nhập hàng
CREATE OR REPLACE TRIGGER TRG_CHECK_HSD_PERCENT
BEFORE INSERT OR UPDATE ON LOSANPHAM
FOR EACH ROW
DECLARE
    v_vongdoi NUMBER;
    v_conlai NUMBER;
    v_percent NUMBER;
BEGIN
    -- Kiểm tra dữ liệu hợp lệ
    IF :NEW.HSD <= :NEW.NGAYSX THEN
        RAISE_APPLICATION_ERROR(-20020, 'HSD phải lớn hơn ngày sản xuất');
    END IF;

    IF :NEW.NGAYNHAP < :NEW.NGAYSX THEN
        RAISE_APPLICATION_ERROR(-20021, 'Ngày nhập không hợp lệ');
    END IF;

    -- Tính vòng đời (số ngày)
    v_vongdoi := :NEW.HSD - :NEW.NGAYSX;
    -- Thời gian còn lại
    v_conlai := :NEW.HSD - :NEW.NGAYNHAP;
    -- % còn lại
    v_percent := (v_conlai / v_vongdoi) * 100;
    -- Kiểm tra cận date (ngưỡng 20%) nếu phần trăm càng nhỏ => càng cận date 
    IF v_percent < 20 THEN
        RAISE_APPLICATION_ERROR(-20022, 
        'Hàng cận date (còn ' || ROUND(v_percent,2) || '% vòng đời) - không cho nhập');
    END IF;

END;
/
--TRIGGER Soluongton < đến mức nào đó thì thông báo đề xuất nhập hàng
CREATE OR REPLACE TRIGGER TRG_KHO_WARNING
AFTER UPDATE ON KHO
FOR EACH ROW
BEGIN
    IF :NEW.SLTON < 10 THEN
        DBMS_OUTPUT.PUT_LINE ('Cảnh báo: Sản phẩm ' || :NEW.MALO || ' sắp hết hàng');
    END IF;
END;
/
