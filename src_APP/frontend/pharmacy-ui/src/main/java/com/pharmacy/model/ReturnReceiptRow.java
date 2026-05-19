package com.pharmacy.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.text.DecimalFormat;

public class ReturnReceiptRow {
    private final SimpleStringProperty maptKh, ngaytra, mahd, tenkh, sanPhamTomTat, lydotra;
    private final SimpleIntegerProperty tongSl, diemhoan;
    private final SimpleDoubleProperty tongtienhoan;

    public ReturnReceiptRow(String maptKh, String ngaytra, String mahd, String tenkh, String sanPhamTomTat, int tongSl, String lydotra, double tongtienhoan, int diemhoan) {
        this.maptKh = new SimpleStringProperty(maptKh);
        this.ngaytra = new SimpleStringProperty(ngaytra);
        this.mahd = new SimpleStringProperty(mahd);
        this.tenkh = new SimpleStringProperty(tenkh);
        this.sanPhamTomTat = new SimpleStringProperty(sanPhamTomTat);
        this.tongSl = new SimpleIntegerProperty(tongSl);
        this.lydotra = new SimpleStringProperty(lydotra);
        this.tongtienhoan = new SimpleDoubleProperty(tongtienhoan);
        this.diemhoan = new SimpleIntegerProperty(diemhoan);
    }

    public String getMaptKh() { return maptKh.get(); }
    public SimpleStringProperty maptKhProperty() { return maptKh; }

    public String getNgaytra() { return ngaytra.get(); }
    public SimpleStringProperty ngaytraProperty() { return ngaytra; }

    public String getMahd() { return mahd.get(); }
    public SimpleStringProperty mahdProperty() { return mahd; }

    public String getTenkh() { return tenkh.get(); }
    public SimpleStringProperty tenkhProperty() { return tenkh; }

    public String getSanPhamTomTat() { return sanPhamTomTat.get(); }
    public SimpleStringProperty sanPhamTomTatProperty() { return sanPhamTomTat; }

    public int getTongSl() { return tongSl.get(); }
    public SimpleIntegerProperty tongSlProperty() { return tongSl; }

    public String getLydotra() { return lydotra.get(); }
    public SimpleStringProperty lydotraProperty() { return lydotra; }

    public double getTongtienhoan() { return tongtienhoan.get(); }
    public SimpleDoubleProperty tongtienhoanProperty() { return tongtienhoan; }
    public String getFormattedTongTien() { return new DecimalFormat("#,### đ").format(tongtienhoan.get()); }

    public int getDiemhoan() { return diemhoan.get(); }
    public SimpleIntegerProperty diemhoanProperty() { return diemhoan; }
}