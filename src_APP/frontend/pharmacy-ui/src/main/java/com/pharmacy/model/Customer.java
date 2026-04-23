package com.pharmacy.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty phone;
    private final StringProperty tier;          // Hạng: Thành viên, Bạc, Vàng, Kim Cương
    private final StringProperty points;        // Điểm tích lũy
    private final StringProperty totalSpent;    // Tổng chi tiêu
    private final StringProperty lastVisit;     // Lần mua gần nhất // GHI CHÚ Y TẾ (Cực kỳ quan trọng: Dị ứng, bệnh nền...)

    public Customer(String id, String name, String phone, String tier, String points, String totalSpent, String lastVisit) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.phone = new SimpleStringProperty(phone);
        this.tier = new SimpleStringProperty(tier);
        this.points = new SimpleStringProperty(points);
        this.totalSpent = new SimpleStringProperty(totalSpent);
        this.lastVisit = new SimpleStringProperty(lastVisit);
    }

    // Getters
    public String getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getPhone() { return phone.get(); }
    public String getTier() { return tier.get(); }
    public String getPoints() { return points.get(); }
    public String getTotalSpent() { return totalSpent.get(); }
    public String getLastVisit() { return lastVisit.get(); }

    // Property getters (Dùng cho TableView)
    public StringProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty tierProperty() { return tier; }
    public StringProperty pointsProperty() { return points; }
    public StringProperty totalSpentProperty() { return totalSpent; }
    public StringProperty lastVisitProperty() { return lastVisit; }
}