package com.pharmacy.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class BatchSelectRow {
    private final SimpleStringProperty malo;
    private final SimpleStringProperty nsx;
    private final SimpleStringProperty hsd;
    private final SimpleStringProperty trangthai;
    private final SimpleIntegerProperty sl;

    public BatchSelectRow(String malo, String nsx, String hsd, int sl, String trangthai) {
        this.malo = new SimpleStringProperty(malo);
        this.nsx = new SimpleStringProperty(nsx);
        this.hsd = new SimpleStringProperty(hsd);
        this.sl = new SimpleIntegerProperty(sl);
        this.trangthai = new SimpleStringProperty(trangthai);
    }

    public String getMalo() { return malo.get(); }
    public SimpleStringProperty maloProperty() { return malo; }

    public String getNsx() { return nsx.get(); }
    public SimpleStringProperty nsxProperty() { return nsx; }

    public String getHsd() { return hsd.get(); }
    public SimpleStringProperty hsdProperty() { return hsd; }

    public int getSl() { return sl.get(); }
    public SimpleIntegerProperty slProperty() { return sl; }

    public String getTrangthai() { return trangthai.get(); }
    public SimpleStringProperty trangthaiProperty() { return trangthai; }
}