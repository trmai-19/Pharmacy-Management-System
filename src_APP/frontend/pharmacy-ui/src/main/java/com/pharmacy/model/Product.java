package com.pharmacy.model;

import com.fasterxml.jackson.annotation.JsonProperty; // Quan trọng nhất
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Bỏ qua các trường dư thừa từ Backend
public class Product {
    // Dùng @JsonProperty để chỉ định tên trường tương ứng trong JSON từ Backend
    
    @JsonProperty("masp")
    private final StringProperty id = new SimpleStringProperty();

    @JsonProperty("tensanpham")
    private final StringProperty name = new SimpleStringProperty();

    @JsonProperty("thanhphan") // Hoặc hoatchat tùy theo ProductResponse ở Backend
    private final StringProperty activeIngredient = new SimpleStringProperty();

    @JsonProperty("madm") // Hoặc tendanhmuc
    private final StringProperty category = new SimpleStringProperty();

    @JsonProperty("dvt")
    private final StringProperty unit = new SimpleStringProperty();

    @JsonProperty("tongton") // Kiểm tra lại tên trường trong ProductResponse backend
    private final StringProperty quantity = new SimpleStringProperty();

    @JsonProperty("trangthai")
    private final StringProperty status = new SimpleStringProperty();

    // Hạn sử dụng (nếu backend trả về)
    private final StringProperty expiryDate = new SimpleStringProperty();

    // Constructor mặc định cho Jackson (Bắt buộc phải có khi nối API)
    public Product() {}

    public Product(String id, String name, String activeIngredient, String category, String unit, String quantity, String expiryDate, String status) {
        this.id.set(id);
        this.name.set(name);
        this.activeIngredient.set(activeIngredient);
        this.category.set(category);
        this.unit.set(unit);
        this.quantity.set(quantity);
        this.expiryDate.set(expiryDate);
        this.status.set(status);
    }

    // --- GETTERS (Dùng cho logic nghiệp vụ) ---
    public String getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getActiveIngredient() { return activeIngredient.get(); }
    public String getCategory() { return category.get(); }
    public String getUnit() { return unit.get(); }
    public String getQuantity() { return quantity.get(); }
    public String getExpiryDate() { return expiryDate.get(); }
    public String getStatus() { return status.get(); }

    // --- SETTERS (Dùng cho Jackson ánh xạ dữ liệu từ JSON) ---
    @JsonProperty("masp")
    public void setId(String id) { this.id.set(id); }
    @JsonProperty("tensanpham")
    public void setName(String name) { this.name.set(name); }
    @JsonProperty("thanhphan")
    public void setActiveIngredient(String activeIngredient) { this.activeIngredient.set(activeIngredient); }
    @JsonProperty("madm")
    public void setCategory(String category) { this.category.set(category); }
    @JsonProperty("dvt")
    public void setUnit(String unit) { this.unit.set(unit); }
    @JsonProperty("tongtonkho")
    public void setQuantity(String quantity) { this.quantity.set(quantity); }
    @JsonProperty("hansudung")
    public void setExpiryDate(String expiryDate) { this.expiryDate.set(expiryDate); }
    @JsonProperty("trangthai")
    public void setStatus(String status) { this.status.set(status); }

    // --- PROPERTY GETTERS (Dùng cho TableView.setCellValueFactory) ---
    public StringProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty activeIngredientProperty() { return activeIngredient; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty unitProperty() { return unit; }
    public StringProperty quantityProperty() { return quantity; }
    public StringProperty expiryDateProperty() { return expiryDate; }
    public StringProperty statusProperty() { return status; }
}