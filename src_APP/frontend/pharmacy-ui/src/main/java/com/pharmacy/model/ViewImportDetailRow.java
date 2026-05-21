package com.pharmacy.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ViewImportDetailRow {
    private final SimpleStringProperty malo;
    private final SimpleIntegerProperty sl;
    private final SimpleDoubleProperty gianhap;
    private final SimpleDoubleProperty thanhtien;

    public ViewImportDetailRow(String malo, int sl, double gianhap, double thanhtien) {
        this.malo = new SimpleStringProperty(malo);
        this.sl = new SimpleIntegerProperty(sl);
        this.gianhap = new SimpleDoubleProperty(gianhap);
        this.thanhtien = new SimpleDoubleProperty(thanhtien);
    }

    public String getMalo() { return malo.get(); }
    public SimpleStringProperty maloProperty() { return malo; }

    public int getSl() { return sl.get(); }
    public SimpleIntegerProperty slProperty() { return sl; }

    public double getGianhap() { return gianhap.get(); }
    public SimpleDoubleProperty gianhapProperty() { return gianhap; }

    public double getThanhtien() { return thanhtien.get(); }
    public SimpleDoubleProperty thanhtienProperty() { return thanhtien; }
}