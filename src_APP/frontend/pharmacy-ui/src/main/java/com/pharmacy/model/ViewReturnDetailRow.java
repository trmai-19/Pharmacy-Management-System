package com.pharmacy.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ViewReturnDetailRow {
    private final SimpleStringProperty tensanpham;
    private final SimpleStringProperty malo;
    private final SimpleIntegerProperty sl;
    private final SimpleDoubleProperty dongiatra;
    private final SimpleDoubleProperty thanhtien;

    public ViewReturnDetailRow(String tensanpham, String malo, int sl, double dongiatra, double thanhtien) {
        this.tensanpham = new SimpleStringProperty(tensanpham);
        this.malo = new SimpleStringProperty(malo);
        this.sl = new SimpleIntegerProperty(sl);
        this.dongiatra = new SimpleDoubleProperty(dongiatra);
        this.thanhtien = new SimpleDoubleProperty(thanhtien);
    }

    public String getTensanpham() { return tensanpham.get(); }
    public SimpleStringProperty tensanphamProperty() { return tensanpham; }

    public String getMalo() { return malo.get(); }
    public SimpleStringProperty maloProperty() { return malo; }

    public int getSl() { return sl.get(); }
    public SimpleIntegerProperty slProperty() { return sl; }

    public double getDongiatra() { return dongiatra.get(); }
    public SimpleDoubleProperty dongiatraProperty() { return dongiatra; }

    public double getThanhtien() { return thanhtien.get(); }
    public SimpleDoubleProperty thanhtienProperty() { return thanhtien; }
}