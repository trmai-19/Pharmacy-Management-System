package com.pharmacy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty activeIngredient = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty unit = new SimpleStringProperty();
    private final StringProperty quantity = new SimpleStringProperty();
    private final StringProperty expiryDate = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

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

    @JsonProperty("masp")
    public void setId(String id) { this.id.set(id); }

    @JsonProperty("tensanpham")
    public void setName(String name) { this.name.set(name); }

    @JsonProperty("thanhphan")
    public void setActiveIngredient(String activeIngredient) { this.activeIngredient.set(activeIngredient); }

    @JsonProperty("tendm") // Nếu backend trả về tên danh mục, hoặc "madm" tùy response thực tế
    public void setCategory(String category) { this.category.set(category); }

    @JsonProperty("dvt")
    public void setUnit(String unit) { this.unit.set(unit); }

    @JsonProperty("tongtonkho") // Khớp chính xác với MedicineResponse trường tổng tồn
    public void setQuantity(String quantity) { this.quantity.set(quantity); }

    @JsonProperty("hansudung")
    public void setExpiryDate(String expiryDate) { this.expiryDate.set(expiryDate); }

    @JsonProperty("trangthai")
    public void setStatus(String status) { this.status.set(status); }

    public String getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getActiveIngredient() { return activeIngredient.get(); }
    public String getCategory() { return category.get(); }
    public String getUnit() { return unit.get(); }
    public String getQuantity() { return quantity.get(); }
    public String getExpiryDate() { return expiryDate.get(); }
    public String getStatus() { return status.get(); }

    public StringProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty activeIngredientProperty() { return activeIngredient; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty unitProperty() { return unit; }
    public StringProperty quantityProperty() { return quantity; }
    public StringProperty expiryDateProperty() { return expiryDate; }
    public StringProperty statusProperty() { return status; }
}