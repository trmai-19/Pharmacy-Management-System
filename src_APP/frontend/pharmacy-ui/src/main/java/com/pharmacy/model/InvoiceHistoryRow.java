package com.pharmacy.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.text.DecimalFormat;
import java.util.List;

public class InvoiceHistoryRow {
    private final SimpleStringProperty mahd, ngayban, trangthai;
    private final SimpleDoubleProperty tongtien, tienthanhtoan;
    private final SimpleIntegerProperty diemsudung;
    
    // THÊM CÁI NÀY ĐỂ CHỨA CHI TIẾT
    private final List<InvoiceItemRow> items; 

    public InvoiceHistoryRow(String mahd, String ngayban, double tongtien, int diemsudung, double tienthanhtoan, String trangthai, List<InvoiceItemRow> items) {
        this.mahd = new SimpleStringProperty(mahd);
        this.ngayban = new SimpleStringProperty(ngayban);
        this.tongtien = new SimpleDoubleProperty(tongtien);
        this.diemsudung = new SimpleIntegerProperty(diemsudung);
        this.tienthanhtoan = new SimpleDoubleProperty(tienthanhtoan);
        this.trangthai = new SimpleStringProperty(trangthai);
        this.items = items;
    }

    public String getMahd() { return mahd.get(); }
    public SimpleStringProperty mahdProperty() { return mahd; }

    public String getNgayban() { return ngayban.get(); }
    public SimpleStringProperty ngaybanProperty() { return ngayban; }

    public double getTongtien() { return tongtien.get(); }
    public String getFormattedTongTien() { return new DecimalFormat("#,### đ").format(tongtien.get()); }

    public int getDiemsudung() { return diemsudung.get(); }
    public SimpleIntegerProperty diemsudungProperty() { return diemsudung; }

    public double getTienthanhtoan() { return tienthanhtoan.get(); }
    public String getFormattedTienThanhToan() { return new DecimalFormat("#,### đ").format(tienthanhtoan.get()); }

    public String getTrangthai() { return trangthai.get(); }
    public SimpleStringProperty trangthaiProperty() { return trangthai; }
    
    public List<InvoiceItemRow> getItems() { return items; }
}