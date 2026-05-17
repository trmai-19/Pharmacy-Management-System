package com.pharmacy.model;

import javafx.beans.property.*;
import java.text.DecimalFormat;

public class ReturnCartItem {
    private final SimpleStringProperty tensanpham, malo, masp;
    private final SimpleIntegerProperty sl;
    private final SimpleDoubleProperty dongiahoan, thanhtien;

    public ReturnCartItem(String masp, String tensanpham, String malo, int sl, double dongiahoan) {
        this.masp = new SimpleStringProperty(masp);
        this.tensanpham = new SimpleStringProperty(tensanpham);
        this.malo = new SimpleStringProperty(malo);
        this.sl = new SimpleIntegerProperty(sl);
        this.dongiahoan = new SimpleDoubleProperty(dongiahoan);
        this.thanhtien = new SimpleDoubleProperty(dongiahoan * sl);
    }

    public String getMasp() { return masp.get(); }
    public String getMalo() { return malo.get(); }
    public int getSl() { return sl.get(); }
    public double getThanhtien() { return thanhtien.get(); }

    public SimpleStringProperty tensanphamProperty() { return tensanpham; }
    public SimpleStringProperty maloProperty() { return malo; }
    public SimpleIntegerProperty slProperty() { return sl; }
    
    public String getFormattedDongia() { return new DecimalFormat("#,### đ").format(dongiahoan.get()); }
    public String getFormattedThanhtien() { return new DecimalFormat("#,### đ").format(thanhtien.get()); }
}