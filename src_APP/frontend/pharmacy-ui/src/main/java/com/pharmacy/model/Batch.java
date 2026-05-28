package com.pharmacy.model;

import javafx.beans.property.SimpleStringProperty;

public class Batch {
    private final SimpleStringProperty batchId;
    private final SimpleStringProperty productName; // Đã sửa tên biến thành productName
    private final SimpleStringProperty mfgDate;
    private final SimpleStringProperty expDate;
    private final SimpleStringProperty importDate;
    private final SimpleStringProperty currentQty;
    private final SimpleStringProperty status;
    private final SimpleStringProperty importPrice;

    public Batch(String batchId, String mfgDate, String expDate, String importDate, String currentQty, String status) {
        this.batchId = new SimpleStringProperty(batchId);
        this.productName = new SimpleStringProperty("");
        this.mfgDate = new SimpleStringProperty(mfgDate);
        this.expDate = new SimpleStringProperty(expDate);
        this.importDate = new SimpleStringProperty(importDate);
        this.currentQty = new SimpleStringProperty(currentQty);
        this.status = new SimpleStringProperty(status);
        this.importPrice = new SimpleStringProperty("0");
    }

    public Batch(String batchId, String mfgDate, String expDate, String importDate, String currentQty, String status, String importPrice) {
        this.batchId = new SimpleStringProperty(batchId);
        this.productName = new SimpleStringProperty("");
        this.mfgDate = new SimpleStringProperty(mfgDate);
        this.expDate = new SimpleStringProperty(expDate);
        this.importDate = new SimpleStringProperty(importDate);
        this.currentQty = new SimpleStringProperty(currentQty);
        this.status = new SimpleStringProperty(status);
        this.importPrice = new SimpleStringProperty(importPrice);
    }

    public Batch(String batchId, String productName, String mfgDate, String expDate, String importDate, String currentQty, String status, String importPrice) {
        this.batchId = new SimpleStringProperty(batchId);
        this.productName = new SimpleStringProperty(productName);
        this.mfgDate = new SimpleStringProperty(mfgDate);
        this.expDate = new SimpleStringProperty(expDate);
        this.importDate = new SimpleStringProperty(importDate);
        this.currentQty = new SimpleStringProperty(currentQty);
        this.status = new SimpleStringProperty(status);
        this.importPrice = new SimpleStringProperty(importPrice);
    }

    public SimpleStringProperty batchIdProperty() { return batchId; }
    public SimpleStringProperty productNameProperty() { return productName; } // Getter mới
    public SimpleStringProperty mfgDateProperty() { return mfgDate; }
    public SimpleStringProperty expDateProperty() { return expDate; }
    public SimpleStringProperty importDateProperty() { return importDate; }
    public SimpleStringProperty currentQtyProperty() { return currentQty; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty importPriceProperty() { return importPrice; }
}