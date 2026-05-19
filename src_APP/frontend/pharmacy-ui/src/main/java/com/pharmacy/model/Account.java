package com.pharmacy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // CỰC KỲ QUAN TRỌNG: Gặp trường lạ từ Backend sẽ không bị crash nữa
public class Account {
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty ownerName = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty accountType = new SimpleStringProperty();

    public Account() {}

    // --- USERNAME ---
    @JsonProperty("username")
    public String getUsername() { return username.get(); }
    public void setUsername(String value) { this.username.set(value); }
    public StringProperty usernameProperty() { return username; }

    // --- OWNER NAME ---
    @JsonProperty("ownerName")
    public String getOwnerName() { return ownerName.get(); }
    public void setOwnerName(String value) { this.ownerName.set(value); }
    public StringProperty ownerNameProperty() { return ownerName; }

    // --- ROLE ---
    @JsonProperty("role")
    public String getRole() { return role.get(); }
    public void setRole(String value) { this.role.set(value); }
    public StringProperty roleProperty() { return role; }

    // --- EMAIL ---
    @JsonProperty("email")
    public String getEmail() { return email.get(); }
    public void setEmail(String value) { this.email.set(value); }
    public StringProperty emailProperty() { return email; }

    // --- STATUS ---
    @JsonProperty("status")
    public String getStatus() { return status.get(); }
    public void setStatus(String value) { this.status.set(value); }
    public StringProperty statusProperty() { return status; }

    // --- ACCOUNT TYPE ---
    @JsonProperty("accountType")
    public String getAccountType() { return accountType.get(); }
    public void setAccountType(String value) { this.accountType.set(value); }
    public StringProperty accountTypeProperty() { return accountType; }
}