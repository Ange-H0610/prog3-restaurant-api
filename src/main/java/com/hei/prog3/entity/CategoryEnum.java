package com.hei.prog3.entity;

public enum CategoryEnum {
    VEGETABLE("Légume"),
    ANIMAL("Animal"),
    MARINE("Fruit de mer"),
    DAIRY("Produit laitier"),
    OTHER("Autre");

    private final String frenchName;

    CategoryEnum(String frenchName) {
        this.frenchName = frenchName;
    }

    public String getFrenchName() {
        return frenchName;
    }
}