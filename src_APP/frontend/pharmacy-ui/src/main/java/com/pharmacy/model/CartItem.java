package com.pharmacy.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.text.DecimalFormat;

public class CartItem {
    private final SimpleStringProperty masp;
    private final SimpleStringProperty malo;
    private final SimpleStringProperty productName;
    private final SimpleIntegerProperty quantity;
    private final double price;

    public CartItem(String masp, String malo, String name, int qty, double price) {
        this.masp = new SimpleStringProperty(masp);
        this.malo = new SimpleStringProperty(malo);
        this.productName = new SimpleStringProperty(name + " (" + malo + ")"); 
        this.quantity = new SimpleIntegerProperty(qty);
        this.price = price;
    }

    public String getMasp() { return masp.get(); }
    public String getMalo() { return malo.get(); }
    public String getProductName() { return productName.get(); }
    public SimpleStringProperty productNameProperty() { return productName; }
    
    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int qty) { this.quantity.set(qty); }
    public SimpleIntegerProperty quantityProperty() { return quantity; }
    
    public double getPrice() { return price; }
    public double getTotalPrice() { return price * getQuantity(); }
    
    public String getFormattedPrice() { return new DecimalFormat("#,### đ").format(price); }
    public String getFormattedTotal() { return new DecimalFormat("#,### đ").format(getTotalPrice()); }
}