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
            Dish updatedDish = dishRepository.updateIngredients(dishId, ingredients);
            return Optional.of(updatedDish);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating dish ingredients", e);
        }
    }
}