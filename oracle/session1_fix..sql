SET SERVEROUTPUT ON;

DECLARE
    v_gia_lan1 NUMBER;
    v_gia_lan2 NUMBER;
BEGIN
    -- Thiết lập mức cô lập SERIALIZABLE ngay từ đầu phiên làm việc
    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
    
    SELECT GIABAN INTO v_gia_lan1 FROM SANPHAM WHERE MASP = 'SP00001'; 
    
    DBMS_SESSION.SLEEP(5); 
    
    SELECT GIABAN INTO v_gia_lan2 FROM SANPHAM WHERE MASP = 'SP00001'; 
    
    COMMIT;
    
    DBMS_OUTPUT.PUT_LINE('Giá tư vấn lần 1: ' || v_gia_lan1 || ' - Giá chốt đơn lần 2 (Đã fix): ' || v_gia_lan2);
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK; 
        RAISE;
END;