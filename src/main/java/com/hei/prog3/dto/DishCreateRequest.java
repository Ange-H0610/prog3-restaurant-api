
package com.hei.prog3.dto;

import com.hei.prog3.entity.DishTypeEnum;

public class DishCreateRequest {
    private String name;
    private DishTypeEnum dishType;
    private Double sellingPrice;

    // Constructeurs
    public DishCreateRequest() {
    }

    public DishCreateRequest(String name, DishTypeEnum dishType, Double sellingPrice) {
        this.name = name;
        this.dishType = dishType;
        this.sellingPrice = sellingPrice;
    }

    // Getters et Setters
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
}