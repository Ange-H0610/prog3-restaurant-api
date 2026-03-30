package com.hei.prog3.repository;

import com.hei.prog3.entity.Category;
import com.hei.prog3.entity.Product;
import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public class ProductRepository {
    private final DataSource dataSource;
    private final CategoryRepository categoryRepository;

    public ProductRepository(DataSource dataSource, CategoryRepository categoryRepository) {
        this.dataSource = dataSource;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getProductList(int page, int size) throws SQLException {
        String sql = "SELECT id, name, price, creation_datetime FROM product " +
                "ORDER BY id LIMIT ? OFFSET ?";

        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, size);
            stmt.setInt(2, (page - 1) * size);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = mapProduct(rs);
                    product.setCategories(categoryRepository.findByProductId(product.getId()));
                    products.add(product);
                }
            }
        }
        return products;
    }

    public List<Product> getProductsByCriteria(String productName, String categoryName,
            Instant creationMin, Instant creationMax) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT p.id, p.name, p.price, p.creation_datetime " +
                        "FROM product p " +
                        "LEFT JOIN product_category pc ON p.id = pc.product_id " +
                        "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (productName != null && !productName.isEmpty()) {
            sql.append("AND p.name ILIKE ? ");
            params.add("%" + productName + "%");
        }

        if (categoryName != null && !categoryName.isEmpty()) {
            sql.append("AND pc.name ILIKE ? ");
            params.add("%" + categoryName + "%");
        }

        if (creationMin != null) {
            sql.append("AND p.creation_datetime >= ? ");
            params.add(Timestamp.from(creationMin));
        }

        if (creationMax != null) {
            sql.append("AND p.creation_datetime <= ? ");
            params.add(Timestamp.from(creationMax));
        }

        sql.append("ORDER BY p.id");

        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = mapProduct(rs);
                    product.setCategories(categoryRepository.findByProductId(product.getId()));
                    products.add(product);
                }
            }
        }
        return products;
    }

    public List<Product> getProductsByCriteria(String productName, String categoryName,
            Instant creationMin, Instant creationMax,
            int page, int size) throws SQLException {
        // D'abord filtrer, puis paginer
        List<Product> allFiltered = getProductsByCriteria(productName, categoryName,
                creationMin, creationMax);

        int start = (page - 1) * size;
        int end = Math.min(start + size, allFiltered.size());

        if (start >= allFiltered.size()) {
            return new ArrayList<>();
        }

        return allFiltered.subList(start, end);
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getDouble("price"));
        product.setCreationDateTime(rs.getTimestamp("creation_datetime").toInstant());
        return product;
    }
}