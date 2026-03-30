package com.hei.prog3.controller;

import com.hei.prog3.entity.Dish;
import com.hei.prog3.entity.Ingredient;
import com.hei.prog3.service.DishService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    /**
     * GET /dishes
     * Retourne la liste de tous les plats avec leurs ingrédients
     */
    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes() {
        List<Dish> dishes = dishService.getAllDishes();
        return ResponseEntity.ok(dishes);
    }

    /**
     * PUT /dishes/{id}/ingredients
     * Modifie les ingrédients d'un plat (associer/dissocier)
     */
    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable int id,
            @RequestBody List<Ingredient> ingredients) {

        // Vérifier que le corps de la requête n'est pas vide
        if (ingredients == null) {
            return ResponseEntity.status(400)
                    .body("Request body is required");
        }

        // Vérifier si le plat existe
        if (dishService.findById(id).isEmpty()) {
            return ResponseEntity.status(404)
                    .body("Dish.id=" + id + " is not found");
        }

        // Mettre à jour les ingrédients
        Dish updatedDish = dishService.updateIngredients(id, ingredients)
                .orElseThrow(() -> new RuntimeException("Error updating dish ingredients"));

        return ResponseEntity.ok(updatedDish);
    }
}