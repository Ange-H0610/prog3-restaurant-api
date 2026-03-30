package com.hei.prog3.entity;

import java.time.Instant;

public class StockMovement {
    private int id;
    private Ingredient ingredient;
    private MovementTypeEnum type;
    private StockValue value;
    private Instant creationDatetime;

    public StockMovement() {
    }

    public StockMovement(int id, Ingredient ingredient, MovementTypeEnum type,
            StockValue value, Instant creationDatetime) {
        this.id = id;
        this.ingredient = ingredient;
        this.type = type;
        this.value = value;
        this.creationDatetime = creationDatetime;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public MovementTypeEnum getType() {
        return type;
    }

    public void setType(MovementTypeEnum type) {
        this.type = type;
    }

    public StockValue getValue() {
        return value;
    }

    public void setValue(StockValue value) {
        this.value = value;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }
}