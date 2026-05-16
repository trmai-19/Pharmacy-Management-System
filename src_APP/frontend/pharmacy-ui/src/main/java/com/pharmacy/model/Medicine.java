package com.pharmacy.model; // Đổi lại package cho đúng với thư mục cậu để file nhé

public class Medicine {
    private String id;
    private String name;
    private String category;
    private int stock;
    private String expiryDate;
    private String status;

    public Medicine(String id, String name, String category, int stock, String expiryDate, String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.stock = stock;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getStock() { return stock; }
    public String getExpiryDate() { return expiryDate; }
    public String getStatus() { return status; }
}