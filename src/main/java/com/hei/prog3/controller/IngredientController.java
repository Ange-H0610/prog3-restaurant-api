package com.hei.prog3.controller;

import com.hei.prog3.entity.Ingredient;
import com.hei.prog3.entity.StockValue;
import com.hei.prog3.service.IngredientService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    /**
     * GET /ingredients
     * Retourne la liste de tous les ingrédients
     */
    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    /**
     * GET /ingredients/{id}
     * Retourne un ingrédient par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable int id) {
        return ingredientService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body("Ingredient.id=" + id + " is not found"));
    }

    /**
     * GET /ingredients/{id}/stock?at={temporal}&unit={unit}
     * Retourne la valeur du stock à un instant donné
     */
    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getStockValue(
            @PathVariable int id,
            @RequestParam Instant at,
            @RequestParam String unit) {

        // Vérifier les paramètres obligatoires
        if (at == null) {
            return ResponseEntity.status(400)
                    .body("Either mandatory query parameter `at` or `unit` is not provided.");
        }

        if (unit == null || unit.isEmpty()) {
            return ResponseEntity.status(400)
                    .body("Either mandatory query parameter `at` or `unit` is not provided.");
        }

        return ingredientService.findStockValue(id, at, unit)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body("Ingredient.id=" + id + " is not found"));
    }
}