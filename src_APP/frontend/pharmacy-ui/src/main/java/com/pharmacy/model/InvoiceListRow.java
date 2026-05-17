package com.pharmacy.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.text.DecimalFormat;

public class InvoiceListRow {
    private final SimpleStringProperty mahd, ngayban, tenkh, sdt, trangthai;
    private final SimpleDoubleProperty tongtien;
    private final SimpleIntegerProperty diemsudung;

    public InvoiceListRow(String mahd, String ngayban, String tenkh, String sdt, double tongtien, String trangthai, int diemsudung) {
        this.mahd = new SimpleStringProperty(mahd);
        this.ngayban = new SimpleStringProperty(ngayban);
        this.tenkh = new SimpleStringProperty(tenkh);
        this.sdt = new SimpleStringProperty(sdt);
        this.tongtien = new SimpleDoubleProperty(tongtien);
        this.trangthai = new SimpleStringProperty(trangthai);
        this.diemsudung = new SimpleIntegerProperty(diemsudung);
    }

    public String getMahd() { return mahd.get(); }
    public SimpleStringProperty mahdProperty() { return mahd; }

    public String getNgayban() { return ngayban.get(); }
    public SimpleStringProperty ngaybanProperty() { return ngayban; }

    public String getTenkh() { return tenkh.get(); }
    public SimpleStringProperty tenkhProperty() { return tenkh; }

    public String getSdt() { return sdt.get(); }
    public SimpleStringProperty sdtProperty() { return sdt; }

    public String getTrangthai() { return trangthai.get(); }
    public SimpleStringProperty trangthaiProperty() { return trangthai; }

    public double getTongtien() { return tongtien.get(); }
    public SimpleDoubleProperty tongtienProperty() { return tongtien; }
    public String getFormattedTongTien() { return new DecimalFormat("#,### đ").format(tongtien.get()); }

    public int getDiemsudung() { return diemsudung.get(); }
    public SimpleIntegerProperty diemsudungProperty() { return diemsudung; }
}