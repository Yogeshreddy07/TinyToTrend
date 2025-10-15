package com.TinyToTrend.TinyToTrend.controller;

import com.TinyToTrend.TinyToTrend.exception.ProductNotFoundException;
import com.TinyToTrend.TinyToTrend.model.Product;
import com.TinyToTrend.TinyToTrend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Product CRUD operations
 * Handles HTTP requests and returns JSON responses
 */
@RestController // Combines @Controller + @ResponseBody (returns JSON automatically)
@RequestMapping("/api/products") // Base URL for all endpoints in this controller
@CrossOrigin(origins = "*") // Allow requests from any frontend (for development)
public class ProductController {
    
    // Inject the service layer
    @Autowired
    private ProductService productService;
    
    /**
     * GET /api/products
     * Returns all products as JSON array
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // 200 OK with product list
    }
    
    /**
     * GET /api/products/{id}
     * Returns a single product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product); // 200 OK with product data
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
    
    /**
     * POST /api/products
     * Creates a new product from JSON request body
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        try {
            Product savedProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct); // 201 Created
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }
    }
    
    /**
     * PUT /api/products/{id}
     * Updates an existing product with new data from JSON
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, 
                                               @Valid @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct); // 200 OK with updated product
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }
    }
    
    /**
     * DELETE /api/products/{id}
     * Deletes a product by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build(); // 204 No Content (successful deletion)
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
    
    /**
     * GET /api/products/category/{categoryId}
     * Returns all products in a specific category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        try {
            List<Product> products = productService.getProductsByCategory(categoryId);
            return ResponseEntity.ok(products); // 200 OK with filtered products
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }
    }
}
