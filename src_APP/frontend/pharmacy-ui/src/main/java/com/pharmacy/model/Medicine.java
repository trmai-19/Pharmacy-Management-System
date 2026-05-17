package com.pharmacy.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;

public class Medicine {
    private final SimpleStringProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty unit;
    private final SimpleStringProperty ingredient;
    private final SimpleStringProperty usage;
    private final SimpleStringProperty category; // MỚI: Thêm Danh mục
    private final SimpleDoubleProperty price;

    public Medicine(String id, String name, String unit, String ingredient, String usage, String category, double price) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.unit = new SimpleStringProperty(unit);
        this.ingredient = new SimpleStringProperty(ingredient);
        this.usage = new SimpleStringProperty(usage);
        this.category = new SimpleStringProperty(category);
        this.price = new SimpleDoubleProperty(price);
    }

    public String getId() { return id.get(); }
    public StringProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public String getUnit() { return unit.get(); }
    public StringProperty unitProperty() { return unit; }

    public String getIngredient() { return ingredient.get(); }
    public StringProperty ingredientProperty() { return ingredient; }

    public String getUsage() { return usage.get(); }
    public StringProperty usageProperty() { return usage; }

    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }

    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }
}