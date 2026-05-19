package com.pharmacy.model;

import javafx.beans.property.SimpleStringProperty;

public class Batch {
    private final SimpleStringProperty batchId;
    private final SimpleStringProperty mfgDate;
    private final SimpleStringProperty expDate;
    private final SimpleStringProperty importDate;
    private final SimpleStringProperty currentQty;
    private final SimpleStringProperty status;
    private final SimpleStringProperty importPrice; // THÊM TRƯỜNG NÀY

    // Constructor cũ (6 tham số) - Giữ nguyên để các file khác không bị lỗi
    public Batch(String batchId, String mfgDate, String expDate, String importDate, String currentQty, String status) {
        this.batchId = new SimpleStringProperty(batchId);
        this.mfgDate = new SimpleStringProperty(mfgDate);
        this.expDate = new SimpleStringProperty(expDate);
        this.importDate = new SimpleStringProperty(importDate);
        this.currentQty = new SimpleStringProperty(currentQty);
        this.status = new SimpleStringProperty(status);
        this.importPrice = new SimpleStringProperty("0");
    }

    // Constructor mới (7 tham số) - Dùng cho bảng kho hiển thị giá nhập
    public Batch(String batchId, String mfgDate, String expDate, String importDate, String currentQty, String status, String importPrice) {
        this.batchId = new SimpleStringProperty(batchId);
        this.mfgDate = new SimpleStringProperty(mfgDate);
        this.expDate = new SimpleStringProperty(expDate);
        this.importDate = new SimpleStringProperty(importDate);
        this.currentQty = new SimpleStringProperty(currentQty);
        this.status = new SimpleStringProperty(status);
        this.importPrice = new SimpleStringProperty(importPrice);
    }

    public SimpleStringProperty batchIdProperty() { return batchId; }
    public SimpleStringProperty mfgDateProperty() { return mfgDate; }
    public SimpleStringProperty expDateProperty() { return expDate; }
    public SimpleStringProperty importDateProperty() { return importDate; }
    public SimpleStringProperty currentQtyProperty() { return currentQty; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty importPriceProperty() { return importPrice; } // THÊM HÀM NÀY
}