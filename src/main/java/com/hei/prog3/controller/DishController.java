
package com.hei.prog3.controller;

import com.hei.prog3.dto.DishCreateRequest;
import com.hei.prog3.entity.Dish;
import com.hei.prog3.entity.DishTypeEnum;
import com.hei.prog3.entity.Ingredient;
import com.hei.prog3.service.DishService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<Dish>> getDishes(
            @RequestParam(required = false) Double priceUnder,
            @RequestParam(required = false) Double priceOver,
            @RequestParam(required = false) String name) {

        List<Dish> dishes = dishService.findDishesWithFilters(priceUnder, priceOver, name);
        return ResponseEntity.ok(dishes);
    }

    @PostMapping
    public ResponseEntity<?> createDishes(@RequestBody List<DishCreateRequest> dishRequests) {
        try {

            if (dishRequests == null || dishRequests.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Request body cannot be empty");
            }

            List<Dish> dishesToCreate = dishRequests.stream()
                    .map(req -> {
                        Dish dish = new Dish();
                        dish.setName(req.getName());
                        dish.setDishType(req.getDishType());
                        dish.setSellingPrice(req.getSellingPrice());
                        return dish;
                    })
                    .collect(Collectors.toList());

            List<Dish> createdDishes = dishService.createDishes(dishesToCreate);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdDishes);

        } catch (RuntimeException e) {

            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating dishes: " + e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable int id,
            @RequestBody List<Ingredient> ingredients) {

        if (ingredients == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Request body is required");
        }

        if (dishService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Dish.id=" + id + " is not found");
        }

        Dish updatedDish = dishService.updateIngredients(id, ingredients)
                .orElseThrow(() -> new RuntimeException("Error updating dish ingredients"));

        return ResponseEntity.ok(updatedDish);
    }
}