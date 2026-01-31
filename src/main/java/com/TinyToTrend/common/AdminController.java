package com.tinytotrend.common;

import com.tinytotrend.order.Order;
import com.tinytotrend.order.OrderRepository;
import com.tinytotrend.product.Product;
import com.tinytotrend.product.ProductDTO;
import com.tinytotrend.product.ProductService;
import com.tinytotrend.user.User;
import com.tinytotrend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    // Dashboard Statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Product> products = productService.getAllProducts();
        List<User> users = userRepository.findAll();
        List<Order> orders = orderRepository.findAll();
        
        stats.put("totalProducts", products.size());
        stats.put("totalUsers", users.size());
        stats.put("totalOrders", orders.size());
        
        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", totalRevenue);
        
        long pendingOrders = orders.stream()
                .filter(o -> "PENDING".equals(o.getStatus()))
                .count();
        stats.put("pendingOrders", pendingOrders);
        
        long lowStockProducts = products.stream()
                .filter(p -> p.getStockQty() < 10)
                .count();
        stats.put("lowStockProducts", lowStockProducts);
        
        return ResponseEntity.ok(stats);
    }
    
    // Product Management
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("category") String category,
            @RequestParam("genderTag") String genderTag,
            @RequestParam("stockQty") Integer stockQty,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            ProductDTO dto = new ProductDTO(name, description, price, category, genderTag, stockQty);
            Product product = productService.createProduct(dto, image);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("category") String category,
            @RequestParam("genderTag") String genderTag,
            @RequestParam("stockQty") Integer stockQty,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            ProductDTO dto = new ProductDTO(name, description, price, category, genderTag, stockQty);
            Product product = productService.updateProduct(id, dto, image);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PatchMapping("/products/{id}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        try {
            Integer quantity = request.get("quantity");
            Product product = productService.updateStock(id, quantity);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Order Management
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(orders);
    }
    
    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        try {
            String status = request.get("status");
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            
            order.setStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // User Management
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
}
