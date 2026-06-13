SET SERVEROUTPUT ON;

DECLARE
    v_gia_lan1 NUMBER;
    v_gia_lan2 NUMBER;
BEGIN
    -- Đọc lần 1
    SELECT GIABAN INTO v_gia_lan1 FROM SANPHAM WHERE MASP = 'SP00001';
    
    -- Mô phỏng thời gian Dược sĩ chém gió với khách (5 giây)
    DBMS_SESSION.SLEEP(5);
    
    -- Đọc lần 2 để lên đơn
    SELECT GIABAN INTO v_gia_lan2 FROM SANPHAM WHERE MASP = 'SP00001';
    
    DBMS_OUTPUT.PUT_LINE('Giá tư vấn lần 1: ' || v_gia_lan1 || ' - Giá chốt đơn lần 2: ' || v_gia_lan2);
END;