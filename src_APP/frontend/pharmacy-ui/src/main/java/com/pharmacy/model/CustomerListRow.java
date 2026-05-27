package com.pharmacy.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import java.text.DecimalFormat;

public class CustomerListRow {
    private final SimpleStringProperty makh, tenkh, sdt, ngaysinh, hangtv;
    private final SimpleDoubleProperty tongdoanhthu, diemtichluy;

    // Đã thêm ngaysinh vào tham số
    public CustomerListRow(String makh, String tenkh, String sdt, String ngaysinh, String hangtv, double tongdoanhthu, double diemtichluy) {
        this.makh = new SimpleStringProperty(makh);
        this.tenkh = new SimpleStringProperty(tenkh);
        this.sdt = new SimpleStringProperty(sdt);
        this.ngaysinh = new SimpleStringProperty(ngaysinh);
        this.hangtv = new SimpleStringProperty(hangtv);
        this.tongdoanhthu = new SimpleDoubleProperty(tongdoanhthu);
        this.diemtichluy = new SimpleDoubleProperty(diemtichluy);
    }

    public String getMakh() { return makh.get(); }
    public SimpleStringProperty makhProperty() { return makh; }

    public String getTenkh() { return tenkh.get(); }
    public SimpleStringProperty tenkhProperty() { return tenkh; }

    public String getSdt() { return sdt.get(); }
    public SimpleStringProperty sdtProperty() { return sdt; }

    public String getNgaysinh() { return ngaysinh.get(); }
    public SimpleStringProperty ngaysinhProperty() { return ngaysinh; }

    public String getHangtv() { return hangtv.get(); }
    public SimpleStringProperty hangtvProperty() { return hangtv; }

    public double getTongdoanhthu() { return tongdoanhthu.get(); }
    public SimpleDoubleProperty tongdoanhthuProperty() { return tongdoanhthu; }

    public double getDiemtichluy() { return diemtichluy.get(); }
    public SimpleDoubleProperty diemtichluyProperty() { return diemtichluy; }

    public String getFormattedDoanhThu() { return new DecimalFormat("#,### đ").format(tongdoanhthu.get()); }
    public String getFormattedDiem() { return new DecimalFormat("#,###").format(diemtichluy.get()); }
}