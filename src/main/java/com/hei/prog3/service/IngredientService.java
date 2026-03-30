package com.hei.prog3.service;

import com.hei.prog3.entity.*;
import com.hei.prog3.repository.IngredientRepository;
import com.hei.prog3.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final StockMovementRepository stockMovementRepository;

    public IngredientService(IngredientRepository ingredientRepository,
            StockMovementRepository stockMovementRepository) {
        this.ingredientRepository = ingredientRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<Ingredient> getAllIngredients() {
        try {
            List<Ingredient> ingredients = ingredientRepository.findAll();
            // Charger les mouvements de stock pour chaque ingrédient
            for (Ingredient ingredient : ingredients) {
                List<StockMovement> movements = stockMovementRepository.findByIngredientId(ingredient.getId());
                ingredient.setStockMovementList(movements);
            }
            return ingredients;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving ingredients", e);
        }
    }

    public Optional<Ingredient> findById(int id) {
        try {
            Optional<Ingredient> ingredient = ingredientRepository.findById(id);
            if (ingredient.isPresent()) {
                List<StockMovement> movements = stockMovementRepository.findByIngredientId(id);
                ingredient.get().setStockMovementList(movements);
            }
            return ingredient;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving ingredient", e);
        }
    }

    public Optional<StockValue> findStockValue(int ingredientId, Instant at, String unit) {
        try {
            Optional<Ingredient> ingredient = ingredientRepository.findById(ingredientId);
            if (ingredient.isPresent()) {
                List<StockMovement> movements = stockMovementRepository.findByIngredientIdUpTo(ingredientId, at);
                ingredient.get().setStockMovementList(movements);
                StockValue stockValue = ingredient.get().getStockValueAt(at);
                return Optional.of(stockValue);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating stock", e);
        }
    }
}