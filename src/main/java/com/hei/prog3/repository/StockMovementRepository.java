package com.hei.prog3.repository;

import com.hei.prog3.entity.*;
import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public class StockMovementRepository {
    private final DataSource dataSource;

    public StockMovementRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<StockMovement> findByIngredientId(int ingredientId) throws SQLException {
        String sql = "SELECT id, id_ingredient, quantity, type, unit, creation_datetime " +
                "FROM stock_movement WHERE id_ingredient = ? ORDER BY creation_datetime";

        List<StockMovement> movements = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ingredientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movements.add(mapStockMovement(rs));
                }
            }
        }
        return movements;
    }

    public List<StockMovement> findByIngredientIdUpTo(int ingredientId, Instant upTo) throws SQLException {
        String sql = "SELECT id, id_ingredient, quantity, type, unit, creation_datetime " +
                "FROM stock_movement WHERE id_ingredient = ? AND creation_datetime <= ? " +
                "ORDER BY creation_datetime";

        List<StockMovement> movements = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ingredientId);
            stmt.setTimestamp(2, Timestamp.from(upTo));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movements.add(mapStockMovement(rs));
                }
            }
        }
        return movements;
    }

    public StockMovement save(StockMovement movement) throws SQLException {
        if (movement.getId() == 0) {
            return insert(movement);
        }
        return movement;
    }

    private StockMovement insert(StockMovement movement) throws SQLException {
        String sql = "INSERT INTO stock_movement (id_ingredient, quantity, type, unit, creation_datetime) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movement.getIngredient().getId());
            stmt.setDouble(2, movement.getValue().getQuantity());
            stmt.setString(3, movement.getType().name());
            stmt.setString(4, movement.getValue().getUnit().name());
            stmt.setTimestamp(5, Timestamp.from(movement.getCreationDatetime()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    movement.setId(rs.getInt(1));
                }
            }
        }
        return movement;
    }

    private StockMovement mapStockMovement(ResultSet rs) throws SQLException {
        StockMovement movement = new StockMovement();
        movement.setId(rs.getInt("id"));

        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getInt("id_ingredient"));
        movement.setIngredient(ingredient);

        movement.setType(MovementTypeEnum.valueOf(rs.getString("type")));

        StockValue value = new StockValue(
                rs.getDouble("quantity"),
                Unit.valueOf(rs.getString("unit")));
        movement.setValue(value);
        movement.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());

        return movement;
    }
}