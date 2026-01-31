package com.tinytotrend.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String search) {
        
        List<Product> products;
        
        if (category != null || gender != null || search != null) {
            products = productService.getProducts(category, gender, search);
        } else {
            products = productService.getAllProducts();
        }
        
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/gender/{gender}")
    public ResponseEntity<List<Product>> getProductsByGender(@PathVariable String gender) {
        List<Product> products = productService.getProductsByGender(gender);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String q) {
        List<Product> products = productService.searchProducts(q);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/in-stock")
    public ResponseEntity<List<Product>> getInStockProducts() {
        List<Product> products = productService.getInStockProducts();
        return ResponseEntity.ok(products);
    }
}
