package com.hei.prog3.entity;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Ingredient {
    private int id;
    private String name;
    private double price;
    private CategoryEnum category;
    private Dish dish;
    private List<StockMovement> stockMovementList;

    public Ingredient() {
    }

    public Ingredient(int id, String name, double price, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Ingredient(String name, double price, CategoryEnum category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    /**
     * Calcule la valeur du stock à un instant donné
     */
    public StockValue getStockValueAt(Instant instant) {
        double quantity = 0.0;

        if (stockMovementList == null || stockMovementList.isEmpty()) {
            return new StockValue(quantity, Unit.KG);
        }

        // Trier les mouvements par date
        List<StockMovement> sortedMovements = stockMovementList.stream()
                .filter(m -> !m.getCreationDatetime().isAfter(instant))
                .sorted(Comparator.comparing(StockMovement::getCreationDatetime))
                .collect(Collectors.toList());

        for (StockMovement movement : sortedMovements) {
            if (movement.getType() == MovementTypeEnum.IN) {
                quantity += movement.getValue().getQuantity();
            } else if (movement.getType() == MovementTypeEnum.OUT) {
                quantity -= movement.getValue().getQuantity();
            }
        }

        return new StockValue(quantity, Unit.KG);
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

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public List<StockMovement> getStockMovementList() {
        return stockMovementList;
    }

    public void setStockMovementList(List<StockMovement> stockMovementList) {
        this.stockMovementList = stockMovementList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Ingredient that = (Ingredient) o;
        return name != null ? name.equalsIgnoreCase(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Ingredient{id=" + id + ", name='" + name + "', price=" + price +
                ", category=" + category + "}";
    }
}