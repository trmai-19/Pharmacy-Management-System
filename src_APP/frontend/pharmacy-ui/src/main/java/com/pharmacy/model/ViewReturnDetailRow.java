package com.pharmacy.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ViewReturnDetailRow {
    private final SimpleStringProperty malo;
    private final SimpleIntegerProperty sl;
    private final SimpleDoubleProperty dongiatra;
    private final SimpleDoubleProperty thanhtien;

    public ViewReturnDetailRow(String malo, int sl, double dongiatra, double thanhtien) {
        this.malo = new SimpleStringProperty(malo);
        this.sl = new SimpleIntegerProperty(sl);
        this.dongiatra = new SimpleDoubleProperty(dongiatra);
        this.thanhtien = new SimpleDoubleProperty(thanhtien);
    }

    public String getMalo() { return malo.get(); }
    public SimpleStringProperty maloProperty() { return malo; }

    public int getSl() { return sl.get(); }
    public SimpleIntegerProperty slProperty() { return sl; }

    public double getDongiatra() { return dongiatra.get(); }
    public SimpleDoubleProperty dongiatraProperty() { return dongiatra; }

    public double getThanhtien() { return thanhtien.get(); }
    public SimpleDoubleProperty thanhtienProperty() { return thanhtien; }
}