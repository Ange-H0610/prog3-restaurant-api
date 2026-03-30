package com.hei.prog3.repository;

import com.hei.prog3.entity.Dish;
import com.hei.prog3.entity.DishTypeEnum;
import com.hei.prog3.entity.Ingredient;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class DishRepository {
    private final DataSource dataSource;
    private final IngredientRepository ingredientRepository;

    public DishRepository(DataSource dataSource, IngredientRepository ingredientRepository) {
        this.dataSource = dataSource;
        this.ingredientRepository = ingredientRepository;
    }

    public Dish findDishById(Integer id) throws SQLException {
        String sql = "SELECT id, name, dish_type FROM dish WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Dish dish = mapDish(rs);
                    List<Ingredient> ingredients = ingredientRepository.findByDishId(dish.getId());
                    dish.setIngredients(ingredients);
                    return dish;
                }
            }
        }
        throw new RuntimeException("Dish with id=" + id + " not found");
    }

    public List<Dish> findDishesByIngredientName(String ingredientName) throws SQLException {
        String sql = "SELECT DISTINCT d.id, d.name, d.dish_type " +
                "FROM dish d " +
                "JOIN ingredient i ON d.id = i.id_dish " +
                "WHERE i.name ILIKE ? " +
                "ORDER BY d.id";

        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + ingredientName + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Dish dish = mapDish(rs);
                    dish.setIngredients(ingredientRepository.findByDishId(dish.getId()));
                    dishes.add(dish);
                }
            }
        }
        return dishes;
    }
    // À ajouter dans DishRepository.java

    public List<Dish> findAll() throws SQLException {
        String sql = "SELECT id, name, dish_type, selling_price FROM dish ORDER BY id";
        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Dish dish = mapDishWithPrice(rs);
                dish.setIngredients(ingredientRepository.findByDishId(dish.getId()));
                dishes.add(dish);
            }
        }
        return dishes;
    }

    public Optional<Dish> findById(int id) throws SQLException {
        String sql = "SELECT id, name, dish_type, selling_price FROM dish WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Dish dish = mapDishWithPrice(rs);
                    dish.setIngredients(ingredientRepository.findByDishId(dish.getId()));
                    return Optional.of(dish);
                }
            }
        }
        return Optional.empty();
    }

    public Dish updateIngredients(int dishId, List<Ingredient> ingredients) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Supprimer toutes les associations existantes
                String deleteSql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                    stmt.setInt(1, dishId);
                    stmt.executeUpdate();
                }

                // Ajouter les nouvelles associations
                String insertSql = "INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit) " +
                        "VALUES (?, ?, 1.0, 'KG')";

                for (Ingredient ingredient : ingredients) {
                    try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                        stmt.setInt(1, dishId);
                        stmt.setInt(2, ingredient.getId());
                        stmt.executeUpdate();
                    }
                }

                conn.commit();
                return findById(dishId).orElseThrow(() -> new SQLException("Dish not found"));
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private Dish mapDishWithPrice(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setId(rs.getInt("id"));
        dish.setName(rs.getString("name"));
        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
        dish.setSellingPrice(rs.getObject("selling_price") != null ? rs.getDouble("selling_price") : null);
        return dish;
    }

    public Dish saveDish(Dish dishToSave) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Dish savedDish;
                if (dishToSave.getId() == 0) {
                    savedDish = insertDish(conn, dishToSave);
                } else {
                    savedDish = updateDish(conn, dishToSave);
                }

                // Mettre à jour les associations avec les ingrédients
                updateDishIngredients(conn, savedDish.getId(), dishToSave.getIngredients());

                conn.commit();
                savedDish.setIngredients(ingredientRepository.findByDishId(savedDish.getId()));
                return savedDish;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private Dish insertDish(Connection conn, Dish dish) throws SQLException {
        String sql = "INSERT INTO dish (name, dish_type) VALUES (?, ?) RETURNING id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dish.getName());
            stmt.setString(2, dish.getDishType().name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dish.setId(rs.getInt(1));
                }
            }
        }
        return dish;
    }

    private Dish updateDish(Connection conn, Dish dish) throws SQLException {
        String sql = "UPDATE dish SET name = ?, dish_type = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dish.getName());
            stmt.setString(2, dish.getDishType().name());
            stmt.setInt(3, dish.getId());
            stmt.executeUpdate();
        }
        return dish;
    }

    private void updateDishIngredients(Connection conn, int dishId, List<Ingredient> ingredients)
            throws SQLException {
        // D'abord, dissocier tous les ingrédients actuels
        String detachSql = "UPDATE ingredient SET id_dish = NULL WHERE id_dish = ?";
        try (PreparedStatement stmt = conn.prepareStatement(detachSql)) {
            stmt.setInt(1, dishId);
            stmt.executeUpdate();
        }

        // Ensuite, associer les nouveaux ingrédients
        if (ingredients != null && !ingredients.isEmpty()) {
            String attachSql = "UPDATE ingredient SET id_dish = ? WHERE id = ?";
            for (Ingredient ingredient : ingredients) {
                try (PreparedStatement stmt = conn.prepareStatement(attachSql)) {
                    stmt.setInt(1, dishId);
                    stmt.setInt(2, ingredient.getId());
                    stmt.executeUpdate();
                }
            }
        }
    }

    private Dish mapDish(ResultSet rs) throws SQLException {
        return new Dish(
                rs.getInt("id"),
                rs.getString("name"),
                DishTypeEnum.valueOf(rs.getString("dish_type")));
    }
}