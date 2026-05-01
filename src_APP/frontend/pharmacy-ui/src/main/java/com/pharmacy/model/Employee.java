package com.pharmacy.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Employee {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty position;
    private final StringProperty phone;
    private final StringProperty status;

    public Employee(String id, String name, String position, String phone, String status) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.position = new SimpleStringProperty(position);
        this.phone = new SimpleStringProperty(phone);
        this.status = new SimpleStringProperty(status);
    }

    public String getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getPosition() { return position.get(); }
    public String getPhone() { return phone.get(); }
    public String getStatus() { return status.get(); }

    public StringProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty positionProperty() { return position; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty statusProperty() { return status; }
}