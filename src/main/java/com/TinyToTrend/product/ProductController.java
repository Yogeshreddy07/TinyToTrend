package com.tinytotrend.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * Get all products with optional search, filter, sort, and pagination parameters.
     * All parameters are optional to maintain backward compatibility.
     * If no parameters are provided, returns all products (existing behavior).
     * If page/size are provided, returns paginated results with metadata.
     * 
     * @param category Filter by category (optional)
     * @param gender Filter by gender tag (optional)  
     * @param search Search by product name - case insensitive (optional)
     * @param sort Sort order: "priceAsc" or "priceDesc" (optional)
     * @param page Page number, 0-indexed (optional, default = 0)
     * @param size Items per page (optional, default = 12)
     * @return List of products or paginated response with metadata
     * 
     * Example: GET /api/products?search=shirt&category=MEN&sort=priceAsc&page=0&size=12
     */
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        
        // If pagination params are provided, return paginated response
        if (page != null || size != null) {
            int pageNum = (page != null) ? page : 0;
            int pageSize = (size != null) ? size : 12;
            
            Page<Product> productPage = productService.getProductsPaged(category, gender, search, sort, pageNum, pageSize);
            
            // Build response with pagination metadata
            Map<String, Object> response = new HashMap<>();
            response.put("content", productPage.getContent());
            response.put("page", productPage.getNumber());
            response.put("size", productPage.getSize());
            response.put("totalElements", productPage.getTotalElements());
            response.put("totalPages", productPage.getTotalPages());
            response.put("last", productPage.isLast());
            response.put("first", productPage.isFirst());
            
            return ResponseEntity.ok(response);
        }
        
        // Non-paginated response (existing behavior for backward compatibility)
        List<Product> products;
        
        // Check if any filter/search/sort parameters are provided
        if (category != null || gender != null || search != null || sort != null) {
            // Use the new method with sort support
            products = productService.getProductsWithSort(category, gender, search, sort);
        } else {
            // No parameters provided - return all products (existing behavior)
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
