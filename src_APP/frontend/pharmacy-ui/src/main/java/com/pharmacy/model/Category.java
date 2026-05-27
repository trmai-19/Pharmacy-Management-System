package com.pharmacy.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Category {
    private final StringProperty categoryId;   // Mã danh mục
    private final StringProperty categoryName; // Tên danh mục (Kháng sinh, Vitamin...)
    private final StringProperty note;         // Ghi chú

    public Category(String categoryId, String categoryName, String note) {
        this.categoryId = new SimpleStringProperty(categoryId);
        this.categoryName = new SimpleStringProperty(categoryName);
        this.note = new SimpleStringProperty(note);
    }

    // Getters
    public String getCategoryId() { return categoryId.get(); }
    public String getCategoryName() { return categoryName.get(); }
    public String getNote() { return note.get(); }

    // Property getters cho TableView
    public StringProperty categoryIdProperty() { return categoryId; }
    public StringProperty categoryNameProperty() { return categoryName; }
    public StringProperty noteProperty() { return note; }
}