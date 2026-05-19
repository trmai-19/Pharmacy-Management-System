package com.pharmacy.model;

import javafx.beans.property.*;

public class Medicine {
    // ==========================================
    // 1. KHAI BÁO BIẾN (Gộp của Team và của Khoa)
    // ==========================================
    private final SimpleStringProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty category;

    // Biến của Team thêm vào
    private final SimpleStringProperty unit;
    private final SimpleStringProperty ingredient;
    private final SimpleStringProperty usage;
    private final SimpleDoubleProperty price;

    // Biến của Khoa (Phục vụ Dashboard Inventory)
    private final SimpleIntegerProperty stock;
    private final SimpleStringProperty expiryDate;
    private final SimpleStringProperty status;

    // ==========================================
    // 2. CONSTRUCTOR CỦA TEAM (Không làm sập code nhóm)
    // ==========================================
    public Medicine(String id, String name, String unit, String ingredient, String usage, String category, double price) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.unit = new SimpleStringProperty(unit);
        this.ingredient = new SimpleStringProperty(ingredient);
        this.usage = new SimpleStringProperty(usage);
        this.price = new SimpleDoubleProperty(price);

        // Khởi tạo mặc định cho các biến của Khoa để chống lỗi Null
        this.stock = new SimpleIntegerProperty(0);
        this.expiryDate = new SimpleStringProperty("");
        this.status = new SimpleStringProperty("");
    }

    // ==========================================
    // 3. CONSTRUCTOR CỦA KHOA (Để ReportController vẫn chạy data giả)
    // ==========================================
    public Medicine(String id, String name, String category, int stock, String expiryDate, String status) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.stock = new SimpleIntegerProperty(stock);
        this.expiryDate = new SimpleStringProperty(expiryDate);
        this.status = new SimpleStringProperty(status);

        // Khởi tạo mặc định cho các biến của Team
        this.unit = new SimpleStringProperty("");
        this.ingredient = new SimpleStringProperty("");
        this.usage = new SimpleStringProperty("");
        this.price = new SimpleDoubleProperty(0.0);
    }

    // ==========================================
    // 4. CÁC HÀM GETTER CHUẨN JAVAFX
    // ==========================================
    public String getId() { return id.get(); }
    public StringProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }

    public String getUnit() { return unit.get(); }
    public StringProperty unitProperty() { return unit; }

    public String getIngredient() { return ingredient.get(); }
    public StringProperty ingredientProperty() { return ingredient; }

    public String getUsage() { return usage.get(); }
    public StringProperty usageProperty() { return usage; }

    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }

    // --- Getter của Khoa ---
    public int getStock() { return stock.get(); }
    public IntegerProperty stockProperty() { return stock; }

    public String getExpiryDate() { return expiryDate.get(); }
    public StringProperty expiryDateProperty() { return expiryDate; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}