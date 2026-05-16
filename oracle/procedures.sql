
CREATE OR REPLACE FUNCTION FUNC_TINH_DIEM_TICH_LUY(p_TONGTIEN IN NUMBER)

RETURN NUMBER IS
BEGIN
    RETURN FLOOR(NVL(p_TONGTIEN, 0) * 0.01);  -- Tích 1% theo trigger
END;
/

-- Định nghĩa TYPE cho danh sách thuốc mua (hỗ trợ nhiều dòng)
CREATE OR REPLACE TYPE item_type AS OBJECT (
    MALO   VARCHAR2(20),
    SL     NUMBER
);
/

CREATE OR REPLACE TYPE item_table AS TABLE OF item_type;
/

-- PROCEDURE XỬ LÝ BÁN HÀNG (Transaction hoàn chỉnh)
CREATE OR REPLACE PROCEDURE PROC_XULY_BANHANG (
    p_MANV       IN  VARCHAR2,
    p_MAKH       IN  VARCHAR2,
    p_ITEMS      IN  item_table,          -- Danh sách thuốc mua
    p_DIEMSUDUNG IN  NUMBER DEFAULT 0,
    p_MAHD_OUT   OUT VARCHAR2
) AS
    v_MAHD      VARCHAR2(20);
    v_TONGTIEN  NUMBER;
    SAVEPOINT after_insert_hd;
BEGIN
    -- Bắt đầu transaction
    INSERT INTO HOADON (MANV, MAKH, NGAYBAN, TRANGTHAI, DIEMSUDUNG)
    VALUES (p_MANV, p_MAKH, SYSDATE, 'KHOI_TAO', p_DIEMSUDUNG)
    RETURNING MAHD INTO v_MAHD;

    SAVEPOINT after_insert_hd;

    -- Thêm chi tiết hóa đơn (bulk)
    FOR i IN 1..p_ITEMS.COUNT LOOP
        INSERT INTO CTHD (MAHD, MALO, SL, DONGIA)
        VALUES (v_MAHD, p_ITEMS(i).MALO, p_ITEMS(i).SL, 
                (SELECT GIABAN FROM SANPHAM WHERE MASP = 
                 (SELECT MASP FROM LOSANPHAM WHERE MALO = p_ITEMS(i).MALO)));
    END LOOP;

    -- Cập nhật trạng thái HOANTAT → Trigger sẽ tự động:
    --   • Kiểm tra tồn kho / HSD
    --   • Trừ tồn kho
    --   • Tính TONGTIEN / TIENTHANHTOAN
    --   • Xử lý điểm tích lũy + log DIEMTL
    UPDATE HOADON 
    SET TRANGTHAI = 'HOANTAT'
    WHERE MAHD = v_MAHD;

    -- Lấy tổng tiền sau khi trigger chạy
    SELECT TONGTIEN INTO v_TONGTIEN 
    FROM HOADON WHERE MAHD = v_MAHD;

    -- Gọi function minh họa (có thể dùng để kiểm tra trước khi commit)
    DBMS_OUTPUT.PUT_LINE('Điểm tích lũy dự kiến: ' || FUNC_TINH_DIEM_TICH_LUY(v_TONGTIEN));

    COMMIT;

    p_MAHD_OUT := v_MAHD;
    DBMS_OUTPUT.PUT_LINE('=== TRANSACTION THÀNH CÔNG ===');
    DBMS_OUTPUT.PUT_LINE('Mã hóa đơn: ' || v_MAHD);
    DBMS_OUTPUT.PUT_LINE('Tổng tiền: ' || v_TONGTIEN);

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK TO SAVEPOINT after_insert_hd;   -- Hoàn nguyên một phần hoặc toàn bộ
        DBMS_OUTPUT.PUT_LINE('=== TRANSACTION ROLLBACK === Lỗi: ' || SQLERRM);
        RAISE;
END;
/



DECLARE
    v_items item_table := item_table();
    v_MAHD  VARCHAR2(20);
BEGIN
    -- Thêm 2 lô thuốc
    v_items.EXTEND; v_items(1) := item_type('LO00001', 5);
    v_items.EXTEND; v_items(2) := item_type('LO00002', 3);

    PROC_XULY_BANHANG(
        p_MANV       => 'NV2501XXXX',
        p_MAKH       => 'KH2501XXXX',
        p_ITEMS      => v_items,
        p_DIEMSUDUNG => 10000,          -- Ví dụ dùng điểm
        p_MAHD_OUT   => v_MAHD
    );

    DBMS_OUTPUT.PUT_LINE('Hóa đơn được tạo: ' || v_MAHD);
END;
/