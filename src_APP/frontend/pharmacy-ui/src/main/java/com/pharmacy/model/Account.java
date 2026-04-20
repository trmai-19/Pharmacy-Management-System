package com.pharmacy.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Account {
    private final StringProperty username;    // Tên đăng nhập / Số điện thoại / Email
    private final StringProperty ownerName;   // Chủ tài khoản (Tên NV hoặc Tên KH)
    private final StringProperty role;        // Quyền (Admin, Dược sĩ, Khách hàng Web...)
    private final StringProperty status;      // Trạng thái (Hoạt động, Bị khóa, Đã nghỉ việc)
    private final StringProperty lastLogin;   // Lần đăng nhập cuối
    private final StringProperty accountType; // Phân loại: "EMPLOYEE" hoặc "CUSTOMER"

    public Account(String username, String ownerName, String role, String status, String lastLogin, String accountType) {
        this.username = new SimpleStringProperty(username);
        this.ownerName = new SimpleStringProperty(ownerName);
        this.role = new SimpleStringProperty(role);
        this.status = new SimpleStringProperty(status);
        this.lastLogin = new SimpleStringProperty(lastLogin);
        this.accountType = new SimpleStringProperty(accountType);
    }

    public String getUsername() { return username.get(); }
    public String getOwnerName() { return ownerName.get(); }
    public String getRole() { return role.get(); }
    public String getStatus() { return status.get(); }
    public String getLastLogin() { return lastLogin.get(); }
    public String getAccountType() { return accountType.get(); }

    public StringProperty usernameProperty() { return username; }
    public StringProperty ownerNameProperty() { return ownerName; }
    public StringProperty roleProperty() { return role; }
    public StringProperty statusProperty() { return status; }
    public StringProperty lastLoginProperty() { return lastLogin; }
    public StringProperty accountTypeProperty() { return accountType; }
}