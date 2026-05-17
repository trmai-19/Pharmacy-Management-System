package com.pharmacy.model;

public class User {
    private String manv;     // Trường mới thêm để lưu Mã nhân viên (VD: NV001)
    private String username; // Số điện thoại hoặc tên tài khoản đăng nhập
    private String fullName;
    private String role;     // ADMIN, SALES, WAREHOUSE

    // Constructor cập nhật lên 4 tham số để hứng đủ dữ liệu từ API Login
    public User(String manv, String username, String fullName, String role) {
        this.manv = manv;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    // Getter lấy Mã nhân viên để truyền vào phiếu nhập kho
    public String getManv() {
        return manv;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName != null && !fullName.isEmpty() ? fullName : username;
    }

    public String getRole() {
        return role;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}