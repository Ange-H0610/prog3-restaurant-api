package com.hei.prog3.entity;

public class DishIngredient {
    private Ingredient ingredient;
    private double quantityRequired;
    private Unit unit;

    public DishIngredient() {
    }

    public DishIngredient(Ingredient ingredient, double quantityRequired, Unit unit) {
        this.ingredient = ingredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public double getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(double quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}