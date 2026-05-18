package com.pharmacy.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.text.DecimalFormat;

public class ReturnDetailRow {
    private final SimpleStringProperty tensanpham, malo;
    private final SimpleIntegerProperty sl;
    private final SimpleDoubleProperty dongia, thanhtien;

    public ReturnDetailRow(String tensanpham, String malo, int sl, double dongia, double thanhtien) {
        this.tensanpham = new SimpleStringProperty(tensanpham);
        this.malo = new SimpleStringProperty(malo);
        this.sl = new SimpleIntegerProperty(sl);
        this.dongia = new SimpleDoubleProperty(dongia);
        this.thanhtien = new SimpleDoubleProperty(thanhtien);
    }

    public String getTensanpham() { return tensanpham.get(); }
    public SimpleStringProperty tensanphamProperty() { return tensanpham; }

    public String getMalo() { return malo.get(); }
    public SimpleStringProperty maloProperty() { return malo; }

    public int getSl() { return sl.get(); }
    public SimpleIntegerProperty slProperty() { return sl; }

    public double getDongia() { return dongia.get(); }
    public SimpleDoubleProperty dongiaProperty() { return dongia; }
    public String getFormattedDongia() { return new DecimalFormat("#,### đ").format(dongia.get()); }

    public double getThanhtien() { return thanhtien.get(); }
    public SimpleDoubleProperty thanhtienProperty() { return thanhtien; }
    public String getFormattedThanhtien() { return new DecimalFormat("#,### đ").format(thanhtien.get()); }
}