package com.hei.prog3.entity;

import java.time.Instant;
import java.util.List;

public class Product {
    private int id;
    private String name;
    private double price;
    private Instant creationDateTime;
    private List<Category> categories;

    public Product() {
    }

    public Product(int id, String name, double price, Instant creationDateTime) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.creationDateTime = creationDateTime;
    }

    public String getCategoryName() {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        return categories.get(0).getName();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Instant getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Instant creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price +
                ", creationDateTime=" + creationDateTime + ", categories=" + categories + "}";
    }
}