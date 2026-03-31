package com.hei.prog3.service;

import com.hei.prog3.entity.Dish;
import com.hei.prog3.entity.Ingredient;
import com.hei.prog3.repository.DishRepository;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.*;

@Service
public class DishService {
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<Dish> getAllDishes() {
        try {
            return dishRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving dishes", e);
        }
    }

    public Optional<Dish> findById(int id) {
        try {
            return dishRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving dish", e);
        }
    }

    public Optional<Dish> updateIngredients(int dishId, List<Ingredient> ingredients) {
        try {
            // Correction: dishRepository.updateIngredients retourne Optional<Dish>
            return dishRepository.updateIngredients(dishId, ingredients);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating dish ingredients", e);
        }
    }

    public List<Dish> createDishes(List<Dish> dishes) {
        try {
            return dishRepository.createDishes(dishes);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating dishes: " + e.getMessage(), e);
        }
    }

    public List<Dish> findDishesWithFilters(Double priceUnder, Double priceOver, String name) {
        try {
            return dishRepository.findDishesWithFilters(priceUnder, priceOver, name);
        } catch (SQLException e) {
            throw new RuntimeException("Error searching dishes: " + e.getMessage(), e);
        }
    }
}