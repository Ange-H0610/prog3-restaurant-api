package com.hei.prog3.repository;

import com.hei.prog3.entity.Category;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class CategoryRepository {
    private final DataSource dataSource;

    public CategoryRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Category> getAllCategories() throws SQLException {
        String sql = "SELECT id, name FROM product_category ORDER BY id";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
        }
        return categories;
    }

    public List<Category> findByProductId(int productId) throws SQLException {
        String sql = "SELECT id, name FROM product_category WHERE product_id = ?";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapCategory(rs));
                }
            }
        }
        return categories;
    }

    private Category mapCategory(ResultSet rs) throws SQLException {
        return new Category(rs.getInt("id"), rs.getString("name"));
    }
}