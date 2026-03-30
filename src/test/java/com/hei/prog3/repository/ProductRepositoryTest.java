package com.hei.prog3.repository;

import com.hei.prog3.config.TestDataSourceConfig;
import com.hei.prog3.entity.Product;
import org.junit.jupiter.api.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductRepositoryTest {

    private DataSource dataSource;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    @BeforeAll
    void setup() throws Exception {
        dataSource = TestDataSourceConfig.getDataSource();
        categoryRepository = new CategoryRepository(dataSource);
        productRepository = new ProductRepository(dataSource, categoryRepository);

        // Nettoyer les données existantes
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM product_category");
            stmt.execute("DELETE FROM product");
            stmt.execute("ALTER SEQUENCE product_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE product_category_id_seq RESTART WITH 1");
        }

        // Insérer les données de test
        insertTestData();
    }

    private void insertTestData() throws Exception {
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {

            // Insérer les produits
            stmt.execute("INSERT INTO product (id, name, price, creation_datetime) VALUES " +
                    "(1, 'Laptop Dell XPS', 4500.00, '2024-01-15 09:30:00'), " +
                    "(2, 'iPhone 13', 5200.00, '2024-02-01 14:10:00'), " +
                    "(3, 'Casque Sony WH1000', 890.50, '2024-02-10 16:45:00'), " +
                    "(4, 'Clavier Logitech', 180.00, '2024-03-05 11:20:00'), " +
                    "(5, 'Ecran Samsung 27\"', 1200.00, '2024-03-18 08:00:00')");

            // Insérer les catégories
            stmt.execute("INSERT INTO product_category (id, name, product_id) VALUES " +
                    "(1, 'Informatique', 1), " +
                    "(2, 'Téléphonie', 2), " +
                    "(3, 'Audio', 3), " +
                    "(4, 'Accessoires', 4), " +
                    "(5, 'Informatique', 5), " +
                    "(6, 'Bureau', 5), " +
                    "(7, 'Mobile', 2)");
        }
    }

    @Test
    void testGetAllCategories() throws Exception {
        List<com.hei.prog3.entity.Category> categories = categoryRepository.getAllCategories();
        assertEquals(7, categories.size());
        assertEquals("Informatique", categories.get(0).getName());
    }

    @Test
    void testGetProductList_Page1Size10() throws Exception {
        List<Product> products = productRepository.getProductList(1, 10);
        assertEquals(5, products.size());
        assertEquals("Laptop Dell XPS", products.get(0).getName());
    }

    @Test
    void testGetProductList_Page1Size5() throws Exception {
        List<Product> products = productRepository.getProductList(1, 5);
        assertEquals(5, products.size());
    }

    @Test
    void testGetProductList_Page1Size3() throws Exception {
        List<Product> products = productRepository.getProductList(1, 3);
        assertEquals(3, products.size());
        assertEquals("Laptop Dell XPS", products.get(0).getName());
        assertEquals("iPhone 13", products.get(1).getName());
        assertEquals("Casque Sony WH1000", products.get(2).getName());
    }

    @Test
    void testGetProductList_Page2Size2() throws Exception {
        List<Product> products = productRepository.getProductList(2, 2);
        assertEquals(2, products.size());
        assertEquals("Clavier Logitech", products.get(0).getName());
        assertEquals("Ecran Samsung 27\"", products.get(1).getName());
    }

    @Test
    void testGetProductsByCriteria_WithProductName() throws Exception {
        List<Product> products = productRepository.getProductsByCriteria("Dell", null, null, null);
        assertEquals(1, products.size());
        assertEquals("Laptop Dell XPS", products.get(0).getName());
    }

    @Test
    void testGetProductsByCriteria_WithCategoryName() throws Exception {
        List<Product> products = productRepository.getProductsByCriteria(null, "info", null, null);
        assertEquals(2, products.size());
    }

    @Test
    void testGetProductsByCriteria_WithMultipleCriteria() throws Exception {
        List<Product> products = productRepository.getProductsByCriteria("Samsung", "bureau", null, null);
        assertEquals(1, products.size());
        assertEquals("Ecran Samsung 27\"", products.get(0).getName());
    }

    @Test
    void testGetProductsByCriteria_WithDateRange() throws Exception {
        Instant creationMin = LocalDateTime.of(2024, 2, 1, 0, 0).toInstant(ZoneOffset.UTC);
        Instant creationMax = LocalDateTime.of(2024, 3, 1, 0, 0).toInstant(ZoneOffset.UTC);

        List<Product> products = productRepository.getProductsByCriteria(null, null, creationMin, creationMax);
        assertEquals(2, products.size());
    }

    @Test
    void testGetProductsByCriteria_WithPagination() throws Exception {
        List<Product> products = productRepository.getProductsByCriteria(
                null, "informatique", null, null, 1, 10);
        assertEquals(2, products.size());
    }

    @Test
    void testGetProductsByCriteria_EmptyResult() throws Exception {
        List<Product> products = productRepository.getProductsByCriteria("Nokia", null, null, null);
        assertEquals(0, products.size());
    }
}