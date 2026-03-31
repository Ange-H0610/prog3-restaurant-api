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

    /**
     * Récupère tous les plats
     */
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

    /**
     * Récupère un plat par son ID
     */
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

    /**
     * Récupère un plat par son ID sans charger les ingrédients
     */
    public Optional<Dish> findByIdBasic(int id) throws SQLException {
        String sql = "SELECT id, name, dish_type, selling_price FROM dish WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapDishWithPrice(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recherche les plats par nom d'ingrédient
     */
    public List<Dish> findDishesByIngredientName(String ingredientName) throws SQLException {
        String sql = "SELECT DISTINCT d.id, d.name, d.dish_type, d.selling_price " +
                "FROM dish d " +
                "JOIN dish_ingredient di ON d.id = di.id_dish " +
                "JOIN ingredient i ON di.id_ingredient = i.id " +
                "WHERE i.name ILIKE ? " +
                "ORDER BY d.id";

        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + ingredientName + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Dish dish = mapDishWithPrice(rs);
                    dish.setIngredients(ingredientRepository.findByDishId(dish.getId()));
                    dishes.add(dish);
                }
            }
        }
        return dishes;
    }

    /**
     * Recherche les plats avec filtres (prix et nom)
     */
    public List<Dish> findDishesWithFilters(Double priceUnder, Double priceOver, String name) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT id, name, dish_type, selling_price FROM dish WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (priceUnder != null) {
            sql.append("AND selling_price < ? ");
            params.add(priceUnder);
        }

        if (priceOver != null) {
            sql.append("AND selling_price > ? ");
            params.add(priceOver);
        }

        if (name != null && !name.isEmpty()) {
            sql.append("AND name ILIKE ? ");
            params.add("%" + name + "%");
        }

        sql.append("ORDER BY id");

        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Dish dish = mapDishWithPrice(rs);
                    dish.setIngredients(ingredientRepository.findByDishId(dish.getId()));
                    dishes.add(dish);
                }
            }
        }
        return dishes;
    }

    /**
     * Vérifie si un plat existe déjà par son nom
     */
    public boolean existsByName(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dish WHERE name ILIKE ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Crée plusieurs plats en une seule transaction
     */
    public List<Dish> createDishes(List<Dish> dishes) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                List<Dish> savedDishes = new ArrayList<>();

                for (Dish dish : dishes) {
                    // Vérifier si le nom existe déjà
                    if (existsByName(dish.getName())) {
                        throw new SQLException("Dish.name=" + dish.getName() + " already exists");
                    }
                    Dish saved = insertDish(conn, dish);
                    savedDishes.add(saved);
                }

                conn.commit();
                return savedDishes;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Sauvegarde un plat (insert ou update)
     */
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
                if (dishToSave.getIngredients() != null) {
                    updateDishIngredients(conn, savedDish.getId(), dishToSave.getIngredients());
                }

                conn.commit();
                savedDish.setIngredients(ingredientRepository.findByDishId(savedDish.getId()));
                return savedDish;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Met à jour les ingrédients d'un plat (associer/dissocier)
     */
    public Optional<Dish> updateIngredients(int dishId, List<Ingredient> ingredients) throws SQLException {
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
                if (ingredients != null && !ingredients.isEmpty()) {
                    String insertSql = "INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit) "
                            +
                            "VALUES (?, ?, 1.0, 'KG')";

                    for (Ingredient ingredient : ingredients) {
                        // Vérifier si l'ingrédient existe
                        if (ingredientRepository.findById(ingredient.getId()).isPresent()) {
                            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                                stmt.setInt(1, dishId);
                                stmt.setInt(2, ingredient.getId());
                                stmt.executeUpdate();
                            }
                        }
                    }
                }

                conn.commit();
                return findById(dishId);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Insère un nouveau plat
     */
    private Dish insertDish(Connection conn, Dish dish) throws SQLException {
        String sql = "INSERT INTO dish (name, dish_type, selling_price) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dish.getName());
            stmt.setString(2, dish.getDishType().name());
            if (dish.getSellingPrice() != null) {
                stmt.setDouble(3, dish.getSellingPrice());
            } else {
                stmt.setNull(3, Types.DOUBLE);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dish.setId(rs.getInt(1));
                }
            }
        }
        return dish;
    }

    /**
     * Met à jour un plat existant
     */
    private Dish updateDish(Connection conn, Dish dish) throws SQLException {
        String sql = "UPDATE dish SET name = ?, dish_type = ?, selling_price = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dish.getName());
            stmt.setString(2, dish.getDishType().name());
            if (dish.getSellingPrice() != null) {
                stmt.setDouble(3, dish.getSellingPrice());
            } else {
                stmt.setNull(3, Types.DOUBLE);
            }
            stmt.setInt(4, dish.getId());
            stmt.executeUpdate();
        }
        return dish;
    }

    /**
     * Met à jour les associations plat-ingrédients
     */
    private void updateDishIngredients(Connection conn, int dishId, List<Ingredient> ingredients)
            throws SQLException {
        // Supprimer toutes les associations existantes
        String deleteSql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, dishId);
            stmt.executeUpdate();
        }

        // Ajouter les nouvelles associations
        if (ingredients != null && !ingredients.isEmpty()) {
            String insertSql = "INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit) " +
                    "VALUES (?, ?, 1.0, 'KG')";

            for (Ingredient ingredient : ingredients) {
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setInt(1, dishId);
                    stmt.setInt(2, ingredient.getId());
                    stmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Map un ResultSet vers un objet Dish
     */
    private Dish mapDish(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setId(rs.getInt("id"));
        dish.setName(rs.getString("name"));
        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
        return dish;
    }

    /**
     * Map un ResultSet vers un objet Dish avec prix de vente
     */
    private Dish mapDishWithPrice(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setId(rs.getInt("id"));
        dish.setName(rs.getString("name"));
        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
        Object priceObj = rs.getObject("selling_price");
        dish.setSellingPrice(priceObj != null ? ((Number) priceObj).doubleValue() : null);
        return dish;
    }
}