package com.pharmacy.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Batch {
    private final StringProperty batchId;      // Mã lô (VD: L001)
    private final StringProperty mfgDate;      // Ngày sản xuất
    private final StringProperty expDate;      // Hạn sử dụng
    private final StringProperty importDate;   // Ngày nhập kho
    private final StringProperty currentQty;   // Số lượng hiện tại của lô này
    private final StringProperty status;       // Tình trạng (Tốt, Cận date, Hết hạn)

    public Batch(String batchId, String mfgDate, String expDate, String importDate, String currentQty, String status) {
        this.batchId = new SimpleStringProperty(batchId);
        this.mfgDate = new SimpleStringProperty(mfgDate);
        this.expDate = new SimpleStringProperty(expDate);
        this.importDate = new SimpleStringProperty(importDate);
        this.currentQty = new SimpleStringProperty(currentQty);
        this.status = new SimpleStringProperty(status);
    }

    // Getters
    public String getBatchId() { return batchId.get(); }
    public String getMfgDate() { return mfgDate.get(); }
    public String getExpDate() { return expDate.get(); }
    public String getImportDate() { return importDate.get(); }
    public String getCurrentQty() { return currentQty.get(); }
    public String getStatus() { return status.get(); }

    // Property getters cho TableView
    public StringProperty batchIdProperty() { return batchId; }
    public StringProperty mfgDateProperty() { return mfgDate; }
    public StringProperty expDateProperty() { return expDate; }
    public StringProperty importDateProperty() { return importDate; }
    public StringProperty currentQtyProperty() { return currentQty; }
    public StringProperty statusProperty() { return status; }
}