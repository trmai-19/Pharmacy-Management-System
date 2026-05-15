-- TRIGGER Cập nhật giá bán
CREATE OR REPLACE TRIGGER TRG_CAPNHAT_GIABAN
AFTER INSERT OR UPDATE OF GIANHAP ON CTPN
FOR EACH ROW
DECLARE
    v_masp      VARCHAR2(20);
    v_new_price NUMBER;
    v_trangthai VARCHAR2(20); 
BEGIN
    BEGIN
        SELECT TRANGTHAI INTO v_trangthai
        FROM PHIEUNHAP WHERE MAPN = :NEW.MAPN;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RETURN;
    END;

    IF v_trangthai != 'HOANTAT' THEN RETURN; END IF;

    BEGIN
        SELECT MASP INTO v_masp 
        FROM LOSANPHAM 
        WHERE MALO = :NEW.MALO;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RETURN; 
    END;

    -- Tính giá bán mặc định (giá nhập + 20%)
    v_new_price := ROUND(:NEW.GIANHAP * 1.2, 0);

    IF v_new_price <= 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Lỗi: Giá bán tính toán phải lớn hơn 0!');
    END IF;

    UPDATE SANPHAM 
    SET GIABAN = v_new_price 
    WHERE MASP = v_masp;
END;
/

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
/

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
/

-- TRIGGER Cập nhật tổng doanh thu khách hàng và hạng thành viên

CREATE OR REPLACE TRIGGER TRG_CAPNHAT_DOANHTHU_HANGTV
AFTER INSERT OR UPDATE OF TONGTIEN, TIENTHANHTOAN ON HOADON
FOR EACH ROW
DECLARE
    v_doanhthu_cu   NUMBER;
    v_tong_doanhthu NUMBER;
    v_hang_moi      VARCHAR2(50);
BEGIN
    IF :NEW.MAKH IS NULL THEN
        RETURN;
    END IF;

    SELECT NVL(TONGDOANHTHU, 0) INTO v_doanhthu_cu 
    FROM KHACHHANG WHERE MAKH = :NEW.MAKH;

    IF INSERTING THEN
        v_tong_doanhthu := v_doanhthu_cu + NVL(:NEW.TIENTHANHTOAN, 0);
    ELSE
        v_tong_doanhthu := v_doanhthu_cu 
                         - NVL(:OLD.TIENTHANHTOAN, 0) 
                         + NVL(:NEW.TIENTHANHTOAN, 0);
    END IF;

    IF v_tong_doanhthu >= 50000000 THEN
        v_hang_moi := 'Kim Cương';
    ELSIF v_tong_doanhthu >= 10000000 THEN
        v_hang_moi := 'Vàng';
    ELSE
        v_hang_moi := 'Bạc';
    END IF;

    UPDATE KHACHHANG 
    SET TONGDOANHTHU = v_tong_doanhthu,
        HANGTV = v_hang_moi
    WHERE MAKH = :NEW.MAKH;

END;
/

--Thêm xóa sửa cho CTHD -> sửa số lượng tồn kho
CREATE OR REPLACE TRIGGER TRG_CTHD_SYNC_KHO
AFTER INSERT OR UPDATE OR DELETE ON CTHD
FOR EACH ROW
DECLARE
    v_diff NUMBER;
    v_makho VARCHAR2(20);
BEGIN
    IF INSERTING THEN
        SELECT MAKHO INTO v_makho FROM LOSANPHAM WHERE MALO = :NEW.MALO;
        
        UPDATE LOSANPHAM 
        SET SLSP = SLSP - :NEW.SL 
        WHERE MALO = :NEW.MALO;

        UPDATE KHO 
        SET SLTON = SLTON - :NEW.SL 
        WHERE MAKHO = v_makho;

    ELSIF DELETING THEN
        SELECT MAKHO INTO v_makho FROM LOSANPHAM WHERE MALO = :OLD.MALO;
        
        UPDATE LOSANPHAM 
        SET SLSP = SLSP + :OLD.SL 
        WHERE MALO = :OLD.MALO;
        
        UPDATE KHO 
        SET SLTON = SLTON + :OLD.SL 
        WHERE MAKHO = v_makho;

    ELSIF UPDATING THEN
        v_diff := :NEW.SL - :OLD.SL;
        SELECT MAKHO INTO v_makho FROM LOSANPHAM WHERE MALO = :NEW.MALO;

        UPDATE LOSANPHAM 
        SET SLSP = SLSP - v_diff 
        WHERE MALO = :NEW.MALO;
        
        UPDATE KHO 
        SET SLTON = SLTON - v_diff 
        WHERE MAKHO = v_makho;
    END IF;
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
CREATE OR REPLACE TRIGGER TRG_CTHD_SYNC_TOTAL_HD
AFTER INSERT OR UPDATE OR DELETE ON CTHD
FOR EACH ROW
DECLARE
    v_mahd VARCHAR2(20);
    v_diff NUMBER := 0;
BEGIN
    IF INSERTING THEN
        v_mahd := :NEW.MAHD;
        v_diff := NVL(:NEW.THANHTIEN, 0);
        
    ELSIF DELETING THEN
        v_mahd := :OLD.MAHD;
        v_diff := -NVL(:OLD.THANHTIEN, 0);
        
    ELSIF UPDATING THEN
        v_mahd := :NEW.MAHD;
        v_diff := NVL(:NEW.THANHTIEN, 0) - NVL(:OLD.THANHTIEN, 0);
    END IF;

    UPDATE HOADON 
    SET TONGTIEN = NVL(TONGTIEN, 0) + v_diff,
        TIENTHANHTOAN = (NVL(TONGTIEN, 0) + v_diff) - NVL(DIEMSUDUNG, 0)
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
    v_masp VARCHAR2(20);
    v_trangthai_sp VARCHAR2(50);
BEGIN
    -- 1. Lấy thông tin lô hàng và mã sản phẩm
    BEGIN
        SELECT SLSP, HSD, MASP INTO v_ton_kho, v_hsd, v_masp
        FROM LOSANPHAM 
        WHERE MALO = :NEW.MALO
        FOR UPDATE;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20010, 'Lỗi: Mã lô ' || :NEW.MALO || ' không tồn tại!');
    END;

    -- 2. Kiểm tra trạng thái sản phẩm (có xử lý Exception)
    BEGIN
        SELECT TRANGTHAI INTO v_trangthai_sp
        FROM SANPHAM
        WHERE MASP = v_masp;
        
        IF v_trangthai_sp = 'NGUNG_BAN' THEN
            RAISE_APPLICATION_ERROR(-20014, 'Lỗi: Sản phẩm này đã ngừng kinh doanh!');
        END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN 
            NULL; -- Bỏ qua nếu không tìm thấy cấu hình sản phẩm
    END;

    -- 3. Kiểm tra hạn sử dụng
    IF v_hsd < TRUNC(SYSDATE) THEN
        RAISE_APPLICATION_ERROR(-20013, 'Lỗi: Thuốc thuộc lô ' || :NEW.MALO || ' đã hết hạn!');
    END IF;

    -- 4. Kiểm tra tồn kho
    IF INSERTING THEN
        IF :NEW.SL > v_ton_kho THEN
            RAISE_APPLICATION_ERROR(-20011, 'Lỗi: Không đủ hàng trong kho! (Tồn: ' || v_ton_kho || ')');
        END IF;
    ELSIF UPDATING THEN
        IF (:NEW.SL - :OLD.SL) > v_ton_kho THEN
            RAISE_APPLICATION_ERROR(-20012, 'Lỗi: Số lượng cập nhật vượt quá tồn kho!');
        END IF;
    END IF;
END;
/

--điểm sử dụng <= 50% tổng tiền && HD.TIENTHANHTOAN = HD.TONGTIEN - HD.DIEMSUDUNG

CREATE OR REPLACE TRIGGER TRG_HOADON_CHECK_DIEM
BEFORE INSERT ON HOADON
FOR EACH ROW
DECLARE
    v_diem_hien_co NUMBER := 0;
    v_tongtien     NUMBER := 0;
BEGIN
    -- Ưu tiên lấy TONGTIEN từ :NEW (nếu đã có)
    v_tongtien := NVL(:NEW.TONGTIEN, 0);

    -- Nếu chưa có tổng tiền thì bỏ qua kiểm tra 50% (vì CTHD chưa insert)
    -- Trigger khác sẽ kiểm tra sau khi có CTHD
    IF NVL(:NEW.DIEMSUDUNG, 0) > 0 THEN
        
        IF :NEW.MAKH IS NULL THEN
            RAISE_APPLICATION_ERROR(-20022, 'Phải có mã khách hàng khi sử dụng điểm');
        END IF;

        -- Lấy điểm hiện có của khách
        BEGIN
            SELECT NVL(diemtichluy, 0) INTO v_diem_hien_co
            FROM KHACHHANG
            WHERE MAKH = :NEW.MAKH;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(-20021, 'Không tìm thấy khách hàng: ' || :NEW.MAKH);
        END;

        IF :NEW.DIEMSUDUNG > v_diem_hien_co THEN
            RAISE_APPLICATION_ERROR(-20021, 
                'Khách hàng không đủ điểm. Hiện có: ' || v_diem_hien_co 
                || ', yêu cầu: ' || :NEW.DIEMSUDUNG);
        END IF;

        -- Chỉ kiểm tra 50% nếu đã có tổng tiền
        IF v_tongtien > 0 AND :NEW.DIEMSUDUNG > (v_tongtien * 0.5) THEN
            RAISE_APPLICATION_ERROR(-20020, 
                'Điểm sử dụng (' || :NEW.DIEMSUDUNG || ') không được vượt quá 50% tổng tiền (' || v_tongtien || ')');
        END IF;
    END IF;

    -- Tính tiền thanh toán
    :NEW.TIENTHANHTOAN := v_tongtien - NVL(:NEW.DIEMSUDUNG, 0);

END;
/


-- DIEMTL.SL = DIEMTL.SL + 1%*HD.TONGTIEN - HD.DIEMSUDUNG
CREATE OR REPLACE TRIGGER TRG_HOADON_AUTO_LOG_DIEM
AFTER INSERT OR UPDATE OF TONGTIEN, DIEMSUDUNG ON HOADON
FOR EACH ROW
DECLARE
    v_diem_thuong NUMBER;
BEGIN
    -- Xóa log cũ của hóa đơn này để tránh duplicate
    DELETE FROM DIEMTL WHERE MAHD = :NEW.MAHD;

    v_diem_thuong := FLOOR(NVL(:NEW.TONGTIEN, 0) * 0.01);
    
    IF v_diem_thuong > 0 THEN
        INSERT INTO DIEMTL (MAKH, MAHD, LOAIGD, DIEMTHAYDOI, NGAYGD, GHICHU)
        VALUES (:NEW.MAKH, :NEW.MAHD, 'CONG_DIEM', v_diem_thuong, SYSDATE, 
                'Tích điểm 1% từ đơn ' || :NEW.MAHD);
    END IF;

    IF NVL(:NEW.DIEMSUDUNG, 0) > 0 THEN
        INSERT INTO DIEMTL (MAKH, MAHD, LOAIGD, DIEMTHAYDOI, NGAYGD, GHICHU)
        VALUES (:NEW.MAKH, :NEW.MAHD, 'TRU_DIEM', -(:NEW.DIEMSUDUNG), SYSDATE, 
                'Dùng điểm cho đơn ' || :NEW.MAHD);
    END IF;
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
    v_masp VARCHAR2(20);
    v_makho VARCHAR2(20);
BEGIN
    SELECT SLSP, MASP, MAKHO INTO v_ton_kho, v_masp, v_makho
    FROM LOSANPHAM 
    WHERE MALO = :NEW.MALO;

    IF v_ton_kho < :NEW.SL THEN
        RAISE_APPLICATION_ERROR(-20060, 
            'Lỗi: Lô hàng ' || :NEW.MALO || ' chỉ còn ' || v_ton_kho || 
            ' sản phẩm. Không đủ để trả số lượng ' || :NEW.SL);
    END IF;

    UPDATE LOSANPHAM 
    SET SLSP = SLSP - :NEW.SL 
    WHERE MALO = :NEW.MALO;

    UPDATE KHO 
    SET SLTON = SLTON - :NEW.SL 
    WHERE MAKHO = v_makho;
END;
/

--TRIGGER cập nhật tổng tiền phiếu trả NCC sau khi thêm/sửa/xóa chi tiết trả hàng
CREATE OR REPLACE TRIGGER TRG_CTPT_NCC_TOTAL_UPDATE
AFTER INSERT OR UPDATE OR DELETE ON CTPT_NCC
FOR EACH ROW
DECLARE
    v_diff NUMBER := 0;
    v_mapt VARCHAR2(20);
BEGIN
    -- Tính toán mức độ chênh lệch tiền
    IF INSERTING THEN
        v_diff := NVL(:NEW.THANHTIEN, 0);
        v_mapt := :NEW.MAPT_NCC;
    ELSIF DELETING THEN
        v_diff := -NVL(:OLD.THANHTIEN, 0);
        v_mapt := :OLD.MAPT_NCC;
    ELSIF UPDATING THEN
        v_diff := NVL(:NEW.THANHTIEN, 0) - NVL(:OLD.THANHTIEN, 0);
        v_mapt := :NEW.MAPT_NCC;
    END IF;

    -- Cập nhật trực tiếp vào vỏ phiếu trả PHIEUTRA_NCC
    UPDATE PHIEUTRA_NCC 
    SET TONGTIEN = NVL(TONGTIEN, 0) + v_diff
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
        SELECT c.MALO, c.SL, l.MAKHO
        FROM CTPN c
        JOIN LOSANPHAM l ON c.MALO = l.MALO
        WHERE c.MAPN = :NEW.MAPN
    )
    LOOP
        UPDATE KHO
        SET SLTON = SLTON + r.SL
        WHERE MAKHO = r.MAKHO;
    END LOOP;
END;
/
--chặn sửa và xóa chi tiết phiếu nhập khi phiếu nhập đã done 
/*CREATE OR REPLACE TRIGGER TRG_KHOA_CTPN
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
/*/
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
    IF INSERTING THEN
        -- Khi thêm 1 chi tiết mới -> CỘNG thêm thành tiền vào tổng
        UPDATE PHIEUNHAP
        SET TONGTIEN = NVL(TONGTIEN, 0) + NVL(:NEW.THANHTIEN, 0)
        WHERE MAPN = :NEW.MAPN;
        
    ELSIF UPDATING THEN
        -- Khi sửa chi tiết (VD: sửa số lượng làm thành tiền đổi) -> TRỪ đi cái cũ, CỘNG vào cái mới
        UPDATE PHIEUNHAP
        SET TONGTIEN = NVL(TONGTIEN, 0) - NVL(:OLD.THANHTIEN, 0) + NVL(:NEW.THANHTIEN, 0)
        WHERE MAPN = :NEW.MAPN;
        
    ELSIF DELETING THEN
        -- Khi xóa 1 chi tiết -> TRỪ thành tiền đó ra khỏi tổng
        UPDATE PHIEUNHAP
        SET TONGTIEN = NVL(TONGTIEN, 0) - NVL(:OLD.THANHTIEN, 0)
        WHERE MAPN = :OLD.MAPN;
    END IF;
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
        DBMS_OUTPUT.PUT_LINE ('Cảnh báo: Kho ' || :NEW.MAKHO || ' sắp hết hàng');
    END IF;
END;
/

----TRIGGER TẠO MÃ TỰ ĐỘNG CHO 

--SEQUENCES 
CREATE SEQUENCE SEQ_DANHMUC START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_KHO START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_NHACUNGCAP START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_SANPHAM START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_LOSANPHAM START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE SEQ_KHACHHANG START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_NHANVIEN START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_TAIKHOAN START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE SEQ_HOADON START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_PHIEUNHAP START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_PHIEUTRA_NCC START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_PHIEUTRA_KH START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_THANHTOAN START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_DIEMTL START WITH 1 INCREMENT BY 1;


--DANHMUC
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_DANHMUC
BEFORE INSERT ON DANHMUC
FOR EACH ROW
BEGIN
    IF :NEW.MADM IS NULL THEN
        :NEW.MADM := 'DM' || LPAD(SEQ_DANHMUC.NEXTVAL, 4, '0');
    END IF;
END;
/
--KHO
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_KHO
BEFORE INSERT ON KHO
FOR EACH ROW
BEGIN
    IF :NEW.MAKHO IS NULL THEN
        :NEW.MAKHO := 'KHO' || LPAD(SEQ_KHO.NEXTVAL, 3, '0');
    END IF;
END;
/
--NHACUNGCAP
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_NHACUNGCAP
BEFORE INSERT ON NHACUNGCAP
FOR EACH ROW
BEGIN
    IF :NEW.MANCC IS NULL THEN
        :NEW.MANCC := 'NCC' || LPAD(SEQ_NHACUNGCAP.NEXTVAL, 4, '0');
    END IF;
END;
/
--SANPHAM
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_SANPHAM
BEFORE INSERT ON SANPHAM
FOR EACH ROW
BEGIN
    IF :NEW.MASP IS NULL THEN
        :NEW.MASP := 'SP' || LPAD(SEQ_SANPHAM.NEXTVAL, 5, '0');
    END IF;
END;
/
--LOSANPHAM
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_LOSANPHAM
BEFORE INSERT ON LOSANPHAM
FOR EACH ROW
BEGIN
    IF :NEW.MALO IS NULL THEN
        :NEW.MALO := 'LO' || LPAD(SEQ_LOSANPHAM.NEXTVAL, 5, '0');
    END IF;
END;
/

--KHACHHANG
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_KHACHHANG
BEFORE INSERT ON KHACHHANG
FOR EACH ROW
BEGIN
    IF :NEW.MAKH IS NULL THEN
        :NEW.MAKH := 'KH' || TO_CHAR(SYSDATE, 'YY') || LPAD(SEQ_KHACHHANG.NEXTVAL, 5, '0');
    END IF;
END;
/
--NHANVIEN
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_NHANVIEN
BEFORE INSERT ON NHANVIEN
FOR EACH ROW
BEGIN
    IF :NEW.MANV IS NULL THEN
        :NEW.MANV := 'NV' || TO_CHAR(SYSDATE, 'YY') || LPAD(SEQ_NHANVIEN.NEXTVAL, 4, '0');
    END IF;
END;
/
--TAIKHOAN
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_TAIKHOAN
BEFORE INSERT ON TAIKHOAN
FOR EACH ROW
DECLARE
    v_seq_val NUMBER;
BEGIN
    IF :NEW.MATK IS NULL THEN
        v_seq_val := SEQ_TAIKHOAN.NEXTVAL;
        IF UPPER(TRIM(:NEW.VAITRO)) = 'CUSTOMER' THEN
            :NEW.MATK := 'TKKH' || TO_CHAR(SYSDATE, 'YY') || LPAD(v_seq_val, 5, '0');
        ELSE
            :NEW.MATK := 'TKNV' || TO_CHAR(SYSDATE, 'YY') || LPAD(v_seq_val, 4, '0');
        END IF;
    END IF;
END;
/

--HOADON
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_HOADON
BEFORE INSERT ON HOADON
FOR EACH ROW
BEGIN
    IF :NEW.MAHD IS NULL THEN
        :NEW.MAHD := 'HD' || TO_CHAR(SYSDATE, 'YYMM') || LPAD(SEQ_HOADON.NEXTVAL, 4, '0');
    END IF;
END;
/

--PHIEUNHAP
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_PHIEUNHAP
BEFORE INSERT ON PHIEUNHAP
FOR EACH ROW
BEGIN
    IF :NEW.MAPN IS NULL THEN
        :NEW.MAPN := 'PN' || TO_CHAR(SYSDATE, 'YYMM') || LPAD(SEQ_PHIEUNHAP.NEXTVAL, 4, '0');
    END IF;
END;
/
--PHIEUTRA_NCC
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_PHIEUTRA_NCC
BEFORE INSERT ON PHIEUTRA_NCC
FOR EACH ROW
BEGIN
    IF :NEW.MAPT_NCC IS NULL THEN
        :NEW.MAPT_NCC := 'PTN' || TO_CHAR(SYSDATE, 'YYMM') || LPAD(SEQ_PHIEUTRA_NCC.NEXTVAL, 4, '0');
    END IF;
END;
/
--PHIEUTRA_KH
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_PHIEUTRA_KH
BEFORE INSERT ON PHIEUTRA_KH
FOR EACH ROW
BEGIN
    IF :NEW.MAPT_KH IS NULL THEN
        :NEW.MAPT_KH := 'PTK' || TO_CHAR(SYSDATE, 'YYMM') || LPAD(SEQ_PHIEUTRA_KH.NEXTVAL, 4, '0');
    END IF;
END;
/
--THANHTOAN
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_THANHTOAN
BEFORE INSERT ON THANHTOAN
FOR EACH ROW
BEGIN
    IF :NEW.MATT IS NULL THEN
        :NEW.MATT := 'TT' || TO_CHAR(SYSDATE, 'YYMM') || LPAD(SEQ_THANHTOAN.NEXTVAL, 4, '0');
    END IF;
END;
/
--DIEMTL
CREATE OR REPLACE TRIGGER TRG_AUTO_ID_DIEMTL
BEFORE INSERT ON DIEMTL
FOR EACH ROW
BEGIN
    IF :NEW.MADTL IS NULL THEN
        :NEW.MADTL := 'DTL' || TO_CHAR(SYSDATE, 'YYMM') || LPAD(SEQ_DIEMTL.NEXTVAL, 4, '0');
    END IF;
END;
/

-- 1a. Tự tính THANHTIEN của từng dòng
CREATE OR REPLACE TRIGGER TRG_CTPT_KH_THANHTIEN
BEFORE INSERT OR UPDATE ON CTPT_KH
FOR EACH ROW
BEGIN
    :NEW.THANHTIEN := NVL(:NEW.SL, 0) * NVL(:NEW.DONGIAHOAN, 0);
END;
/

-- 1b. Cộng lại tồn kho khi khách trả hàng
CREATE OR REPLACE TRIGGER TRG_CTPT_KH_HOAN_KHO
AFTER INSERT ON CTPT_KH
FOR EACH ROW
DECLARE
    v_ton NUMBER;
    v_makho VARCHAR2(20);
BEGIN
    SELECT SLSP, MAKHO INTO v_ton, v_makho 
    FROM LOSANPHAM 
    WHERE MALO = :NEW.MALO;

    UPDATE LOSANPHAM 
    SET SLSP = SLSP + :NEW.SL 
    WHERE MALO = :NEW.MALO;
    
    UPDATE KHO 
    SET SLTON = SLTON + :NEW.SL 
    WHERE MAKHO = v_makho;
END;
/

CREATE OR REPLACE TRIGGER TRG_LOSANPHAM_AUTO_STATUS
BEFORE INSERT OR UPDATE ON LOSANPHAM
FOR EACH ROW
BEGIN
    IF :NEW.SLSP <= 0 THEN
        :NEW.TRANGTHAI := 'HET_HANG';
    ELSIF :NEW.HSD < TRUNC(SYSDATE) THEN
        :NEW.TRANGTHAI := 'HET_HAN';
    ELSE
        :NEW.TRANGTHAI := 'CON_HANG';
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_KHOA_CTHD
BEFORE UPDATE OR DELETE ON CTHD
FOR EACH ROW
DECLARE
    v_trangthai VARCHAR2(20);
BEGIN
    SELECT TRANGTHAI INTO v_trangthai
    FROM HOADON WHERE MAHD = :OLD.MAHD;

    IF v_trangthai = 'HOANTAT' THEN
        RAISE_APPLICATION_ERROR(-20040,
        'Hóa đơn đã hoàn tất, không được chỉnh sửa chi tiết!');
    END IF;
END;
/

-- sửa để chạy api trả hàng

CREATE OR REPLACE TRIGGER TRG_DIEMTL_SYNC_KHACHHANG
AFTER INSERT OR UPDATE OR DELETE ON DIEMTL
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        UPDATE KHACHHANG 
        SET DIEMTICHLUY = NVL(DIEMTICHLUY, 0) + :NEW.DIEMTHAYDOI 
        WHERE MAKH = :NEW.MAKH;

    ELSIF DELETING THEN
        UPDATE KHACHHANG 
        SET DIEMTICHLUY = NVL(DIEMTICHLUY, 0) - :OLD.DIEMTHAYDOI 
        WHERE MAKH = :OLD.MAKH;

    ELSIF UPDATING THEN
        UPDATE KHACHHANG 
        SET DIEMTICHLUY = NVL(DIEMTICHLUY, 0) - :OLD.DIEMTHAYDOI + :NEW.DIEMTHAYDOI 
        WHERE MAKH = :NEW.MAKH;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_CTPT_KH_SYNC_TOTAL
AFTER INSERT OR UPDATE OR DELETE ON CTPT_KH
FOR EACH ROW
DECLARE
    v_diff NUMBER := 0;
    v_mapt VARCHAR2(20);
BEGIN
    -- Tính toán mức độ chênh lệch tiền
    IF INSERTING THEN
        v_diff := NVL(:NEW.THANHTIEN, 0);
        v_mapt := :NEW.MAPT_KH;
    ELSIF DELETING THEN
        v_diff := -NVL(:OLD.THANHTIEN, 0);
        v_mapt := :OLD.MAPT_KH;
    ELSIF UPDATING THEN
        v_diff := NVL(:NEW.THANHTIEN, 0) - NVL(:OLD.THANHTIEN, 0);
        v_mapt := :NEW.MAPT_KH;
    END IF;

    -- Cập nhật trực tiếp vào vỏ phiếu trả PHIEUTRA_KH
    UPDATE PHIEUTRA_KH
    SET TONGTIENHOAN = NVL(TONGTIENHOAN, 0) + v_diff
    WHERE MAPT_KH = v_mapt;
END;
/

CREATE OR REPLACE TRIGGER TRG_RETURN_DIEMTL
AFTER UPDATE OF TONGTIENHOAN ON PHIEUTRA_KH
FOR EACH ROW
DECLARE
    v_makh VARCHAR2(20);
    v_diem_can_tru NUMBER;
    v_diff NUMBER;
BEGIN
    -- Tính tiền hoàn chênh lệch so với trước đó
    v_diff := NVL(:NEW.TONGTIENHOAN, 0) - NVL(:OLD.TONGTIENHOAN, 0);
    
    IF v_diff > 0 THEN
        SELECT MAKH INTO v_makh 
        FROM HOADON 
        WHERE MAHD = :NEW.MAHD;

        v_diem_can_tru := FLOOR(v_diff * 0.01);

        IF v_diem_can_tru > 0 THEN
            INSERT INTO DIEMTL (MAKH, MAHD, LOAIGD, DIEMTHAYDOI, NGAYGD, GHICHU)
            VALUES (v_makh, :NEW.MAHD, 'TRU_DIEM', -v_diem_can_tru, SYSDATE, 
                    'Thu hồi điểm do trả hàng đơn ' || :NEW.MAHD);
        END IF;
    END IF;
END;
/

-- TRIGGER TRỪ DOANH THU KHI TRẢ HÀNG 
CREATE OR REPLACE TRIGGER TRG_PHIEUTRA_KH_UPDATE_DOANHTHU
AFTER INSERT OR UPDATE OF TONGTIENHOAN ON PHIEUTRA_KH
FOR EACH ROW
DECLARE
    v_makh          VARCHAR2(20);
    v_doanhthu_cu   NUMBER;
    v_tong_moi      NUMBER;
    v_hang_moi      VARCHAR2(50);
    v_diff          NUMBER := 0;
BEGIN
    SELECT MAKH INTO v_makh 
    FROM HOADON 
    WHERE MAHD = :NEW.MAHD;

    SELECT NVL(TONGDOANHTHU, 0) INTO v_doanhthu_cu 
    FROM KHACHHANG 
    WHERE MAKH = v_makh;

    -- Tính toán phần chênh lệch cần trừ
    IF INSERTING THEN
        v_diff := NVL(:NEW.TONGTIENHOAN, 0);
    ELSIF UPDATING THEN
        v_diff := NVL(:NEW.TONGTIENHOAN, 0) - NVL(:OLD.TONGTIENHOAN, 0);
    END IF;

    v_tong_moi := v_doanhthu_cu - v_diff;
    IF v_tong_moi < 0 THEN 
        v_tong_moi := 0; 
    END IF;

    -- Cập nhật lại hạng
    IF v_tong_moi < 10000000 THEN v_hang_moi := 'Bạc';
    ELSIF v_tong_moi < 50000000 THEN v_hang_moi := 'Vàng';
    ELSE v_hang_moi := 'Kim Cương';
    END IF;

    UPDATE KHACHHANG 
    SET TONGDOANHTHU = v_tong_moi, HANGTV = v_hang_moi
    WHERE MAKH = v_makh;
END;
/

