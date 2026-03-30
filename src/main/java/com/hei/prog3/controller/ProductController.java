package com.hei.prog3.controller;

import com.hei.prog3.entity.Product;
import com.hei.prog3.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /products?page={page}&size={size}
     * Retourne la liste des produits paginée
     */
    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Product> products = productService.getProductList(page, size);
        return ResponseEntity.ok(products);
    }

    /**
     * GET
     * /products/search?productName={name}&categoryName={cat}&creationMin={min}&creationMax={max}
     * Recherche de produits par critères
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Instant creationMin,
            @RequestParam(required = false) Instant creationMax,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Product> products = productService.getProductsByCriteria(
                productName, categoryName, creationMin, creationMax, page, size);

        return ResponseEntity.ok(products);
    }
}