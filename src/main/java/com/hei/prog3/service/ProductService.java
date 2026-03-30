package com.hei.prog3.service;

import com.hei.prog3.entity.Product;
import com.hei.prog3.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProductList(int page, int size) {
        try {
            return productRepository.getProductList(page, size);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving products", e);
        }
    }

    public List<Product> getProductsByCriteria(String productName, String categoryName,
            Instant creationMin, Instant creationMax,
            int page, int size) {
        try {
            return productRepository.getProductsByCriteria(
                    productName, categoryName, creationMin, creationMax, page, size);
        } catch (SQLException e) {
            throw new RuntimeException("Error searching products", e);
        }
    }
}