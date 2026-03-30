package com.hei.prog3.entity;

import java.util.List;

public class Dish {
    private int id;
    private String name;
    private DishTypeEnum dishType;
    private Double sellingPrice;
    private List<Ingredient> ingredients;
    private List<DishIngredient> requiredQuantities;

    public Dish() {
    }

    public Dish(int id, String name, DishTypeEnum dishType) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
    }

    public Dish(String name, DishTypeEnum dishType) {
        this.name = name;
        this.dishType = dishType;
    }

    /**
     * Calcule le coût du plat (somme des prix des ingrédients * quantités)
     */
    public double getDishCost() {
        if (requiredQuantities == null || requiredQuantities.isEmpty()) {
            return 0.0;
        }

        return requiredQuantities.stream()
                .mapToDouble(qi -> qi.getIngredient().getPrice() * qi.getQuantityRequired())
                .sum();
    }

    /**
     * Calcule la marge brute du plat
     * 
     * @throws IllegalStateException si le prix de vente est null
     */
    public double getGrossMargin() {
        if (sellingPrice == null) {
            throw new IllegalStateException("Le prix de vente du plat est null");
        }
        return sellingPrice - getDishCost();
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

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<DishIngredient> getRequiredQuantities() {
        return requiredQuantities;
    }

    public void setRequiredQuantities(List<DishIngredient> requiredQuantities) {
        this.requiredQuantities = requiredQuantities;
    }

    @Override
    public String toString() {
        return "Dish{id=" + id + ", name='" + name + "', dishType=" + dishType +
                ", sellingPrice=" + sellingPrice + ", cost=" + getDishCost() + "}";
    }
}