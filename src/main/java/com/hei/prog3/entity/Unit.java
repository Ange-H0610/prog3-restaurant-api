package com.hei.prog3.entity;

public enum Unit {
    KG("Kilogramme"),
    L("Litre"),
    PCS("Pièce");

    private final String frenchName;

    Unit(String frenchName) {
        this.frenchName = frenchName;
    }

    public String getFrenchName() {
        return frenchName;
    }
}