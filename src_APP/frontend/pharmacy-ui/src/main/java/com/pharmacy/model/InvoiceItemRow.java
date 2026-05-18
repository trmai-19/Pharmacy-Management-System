package com.pharmacy.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.text.DecimalFormat;

public class InvoiceItemRow {
    private final SimpleStringProperty masp;
    private final SimpleStringProperty malo;
    private final SimpleIntegerProperty sl;
    private final SimpleDoubleProperty dongia;
    private final SimpleDoubleProperty thanhtien;

    public InvoiceItemRow(String masp, String malo, int sl, double dongia, double thanhtien) {
        this.masp = new SimpleStringProperty(masp);
        this.malo = new SimpleStringProperty(malo);
        this.sl = new SimpleIntegerProperty(sl);
        this.dongia = new SimpleDoubleProperty(dongia);
        this.thanhtien = new SimpleDoubleProperty(thanhtien);
    }

    public String getMasp() { return masp.get(); }
    public SimpleStringProperty maspProperty() { return masp; }

    public String getMalo() { return malo.get(); }
    public SimpleStringProperty maloProperty() { return malo; }

    public int getSl() { return sl.get(); }
    public SimpleIntegerProperty slProperty() { return sl; }

    public String getFormattedDongia() { return dongia.get() > 0 ? new DecimalFormat("#,### đ").format(dongia.get()) : "-"; }
    public String getFormattedThanhtien() { return thanhtien.get() > 0 ? new DecimalFormat("#,### đ").format(thanhtien.get()) : "-"; }
}