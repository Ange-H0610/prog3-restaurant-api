package com.hei.prog3.entity;

public enum DishTypeEnum {
    START("Entrée"),
    MAIN("Plat principal"),
    DESSERT("Dessert");

    private final String frenchName;

    DishTypeEnum(String frenchName) {
        this.frenchName = frenchName;
    }

    public String getFrenchName() {
        return frenchName;
    }
}