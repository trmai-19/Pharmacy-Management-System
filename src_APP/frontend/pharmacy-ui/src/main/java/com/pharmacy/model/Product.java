package com.pharmacy.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {
    private final StringProperty id;              // Mã thuốc (SKU)
    private final StringProperty name;            // Tên thương mại
    private final StringProperty activeIngredient;// Hoạt chất chính
    private final StringProperty category;        // Nhóm thuốc
    private final StringProperty unit;            // Đơn vị (Hộp, Lọ, Vỉ)
    private final StringProperty quantity;        // Số lượng tồn
    private final StringProperty expiryDate;      // Hạn sử dụng (Date)
    private final StringProperty status;          // Tình trạng (Bình thường, Cận Date, Hết hàng)

    public Product(String id, String name, String activeIngredient, String category, String unit, String quantity, String expiryDate, String status) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.activeIngredient = new SimpleStringProperty(activeIngredient);
        this.category = new SimpleStringProperty(category);
        this.unit = new SimpleStringProperty(unit);
        this.quantity = new SimpleStringProperty(quantity);
        this.expiryDate = new SimpleStringProperty(expiryDate);
        this.status = new SimpleStringProperty(status);
    }

    // Getters
    public String getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getActiveIngredient() { return activeIngredient.get(); }
    public String getCategory() { return category.get(); }
    public String getUnit() { return unit.get(); }
    public String getQuantity() { return quantity.get(); }
    public String getExpiryDate() { return expiryDate.get(); }
    public String getStatus() { return status.get(); }

    // Property getters cho TableView
    public StringProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty activeIngredientProperty() { return activeIngredient; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty unitProperty() { return unit; }
    public StringProperty quantityProperty() { return quantity; }
    public StringProperty expiryDateProperty() { return expiryDate; }
    public StringProperty statusProperty() { return status; }
}