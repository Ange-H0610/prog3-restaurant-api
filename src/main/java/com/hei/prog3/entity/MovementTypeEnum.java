package com.hei.prog3.entity;

public enum MovementTypeEnum {
    IN("Entrée"),
    OUT("Sortie");

    private final String frenchName;

    MovementTypeEnum(String frenchName) {
        this.frenchName = frenchName;
    }

    public String getFrenchName() {
        return frenchName;
    }
}