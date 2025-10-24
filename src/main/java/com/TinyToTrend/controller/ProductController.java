package com.TinyToTrend.controller;

import com.TinyToTrend.model.Product;
import com.TinyToTrend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * GET /api/products - Get all products with optional filters
     * Query params: category, gender, search
     * Example: /api/products?category=men&search=shirt
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String search) {
        
        List<Product> products;
        
        if (category != null || gender != null || search != null) {
            // Apply filters
            products = productService.getProducts(category, gender, search);
        } else {
            // Get all products
            products = productService.getAllProducts();
        }
        
        return ResponseEntity.ok(products);
    }
    
    /**
     * GET /api/products/{id} - Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/products/category/{category} - Get products by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
    
    /**
     * GET /api/products/gender/{gender} - Get products by gender
     */
    @GetMapping("/gender/{gender}")
    public ResponseEntity<List<Product>> getProductsByGender(@PathVariable String gender) {
        List<Product> products = productService.getProductsByGender(gender);
        return ResponseEntity.ok(products);
    }
    
    /**
     * GET /api/products/search?q=keyword - Search products
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String q) {
        List<Product> products = productService.searchProducts(q);
        return ResponseEntity.ok(products);
    }
    
    /**
     * GET /api/products/instock - Get products in stock
     */
    @GetMapping("/instock")
    public ResponseEntity<List<Product>> getInStockProducts() {
        List<Product> products = productService.getInStockProducts();
        return ResponseEntity.ok(products);
    }
}
