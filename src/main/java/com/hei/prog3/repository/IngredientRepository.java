package com.hei.prog3.repository;

import com.hei.prog3.entity.CategoryEnum;
import com.hei.prog3.entity.Ingredient;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class IngredientRepository {
    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Ingredient> findIngredients(int page, int size) throws SQLException {
        String sql = "SELECT id, name, price, category FROM ingredient " +
                "ORDER BY id LIMIT ? OFFSET ?";

        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, size);
            stmt.setInt(2, (page - 1) * size);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(mapIngredient(rs));
                }
            }
        }
        return ingredients;
    }

    public List<Ingredient> findByDishId(int dishId) throws SQLException {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id_dish = ?";
        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dishId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(mapIngredient(rs));
                }
            }
        }
        return ingredients;
    }

    public List<Ingredient> findByCriteria(String ingredientName, CategoryEnum category,
            String dishName, int page, int size) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT i.id, i.name, i.price, i.category " +
                        "FROM ingredient i " +
                        "LEFT JOIN dish d ON i.id_dish = d.id " +
                        "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (ingredientName != null && !ingredientName.isEmpty()) {
            sql.append("AND i.name ILIKE ? ");
            params.add("%" + ingredientName + "%");
        }

        if (category != null) {
            sql.append("AND i.category = ? ");
            params.add(category.name());
        }

        if (dishName != null && !dishName.isEmpty()) {
            sql.append("AND d.name ILIKE ? ");
            params.add("%" + dishName + "%");
        }

        sql.append("ORDER BY i.id LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page - 1) * size);

        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(mapIngredient(rs));
                }
            }
        }
        return ingredients;
    }
    // À ajouter dans IngredientRepository.java

    public List<Ingredient> findAll() throws SQLException {
        String sql = "SELECT id, name, price, category FROM ingredient ORDER BY id";
        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ingredients.add(mapIngredient(rs));
            }
        }
        return ingredients;
    }

    public Optional<Ingredient> findById(int id) throws SQLException {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapIngredient(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                List<Ingredient> savedIngredients = new ArrayList<>();

                for (Ingredient ingredient : newIngredients) {
                    // Vérifier si l'ingrédient existe déjà
                    if (existsByName(conn, ingredient.getName())) {
                        throw new RuntimeException("Ingredient already exists: " + ingredient.getName());
                    }
                    Ingredient saved = insertIngredient(conn, ingredient);
                    savedIngredients.add(saved);
                }

                conn.commit();
                return savedIngredients;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private boolean existsByName(Connection conn, String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ingredient WHERE name ILIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Ingredient insertIngredient(Connection conn, Ingredient ingredient) throws SQLException {
        String sql = "INSERT INTO ingredient (name, price, category) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ingredient.getName());
            stmt.setDouble(2, ingredient.getPrice());
            stmt.setString(3, ingredient.getCategory().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ingredient.setId(rs.getInt(1));
                }
            }
        }
        return ingredient;
    }

    private Ingredient mapIngredient(ResultSet rs) throws SQLException {
        return new Ingredient(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                CategoryEnum.valueOf(rs.getString("category")));
    }
}
