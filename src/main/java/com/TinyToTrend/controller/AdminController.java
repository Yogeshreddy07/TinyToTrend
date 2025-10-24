package com.TinyToTrend.controller;

import com.TinyToTrend.model.Order;
import com.TinyToTrend.model.Product;
import com.TinyToTrend.model.User;
import com.TinyToTrend.repository.OrderRepository;
import com.TinyToTrend.repository.ProductRepository;
import com.TinyToTrend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    // ==================== PRODUCTS ====================

    /**
     * GET /api/admin/products - Get all products
     */
    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to fetch products: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/products/{id} - Get single product
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/admin/products - Create new product
     */
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> request) {
        try {
            Product product = new Product();
            product.setName(request.get("name").toString());
            product.setDescription(request.get("description").toString());
            product.setCategory(request.get("category").toString());
            product.setPrice(new BigDecimal(request.get("price").toString()));
            product.setStockQty(Integer.parseInt(request.get("stockQty").toString()));
            product.setGenderTag(request.get("genderTag").toString());
            
            if (request.containsKey("imageUrl") && request.get("imageUrl") != null) {
                product.setImageUrl(request.get("imageUrl").toString());
            }

            Product savedProduct = productRepository.save(product);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Product created successfully",
                    "product", savedProduct
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to create product: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/products/{id} - Update product
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, 
                                          @RequestBody Map<String, Object> request) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (request.containsKey("name")) {
                product.setName(request.get("name").toString());
            }
            if (request.containsKey("description")) {
                product.setDescription(request.get("description").toString());
            }
            if (request.containsKey("category")) {
                product.setCategory(request.get("category").toString());
            }
            if (request.containsKey("price")) {
                product.setPrice(new BigDecimal(request.get("price").toString()));
            }
            if (request.containsKey("stockQty")) {
                product.setStockQty(Integer.parseInt(request.get("stockQty").toString()));
            }
            if (request.containsKey("genderTag")) {
                product.setGenderTag(request.get("genderTag").toString());
            }
            if (request.containsKey("imageUrl")) {
                product.setImageUrl(request.get("imageUrl").toString());
            }

            Product updatedProduct = productRepository.save(product);

            return ResponseEntity.ok(Map.of(
                    "message", "Product updated successfully",
                    "product", updatedProduct
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to update product: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/products/{id} - Delete product
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            productRepository.delete(product);

            return ResponseEntity.ok(Map.of(
                    "message", "Product deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to delete product: " + e.getMessage()));
        }
    }

    // ==================== ORDERS ====================

    /**
     * GET /api/admin/orders - Get all orders
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to fetch orders: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/orders/{id} - Get single order
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/orders/{id}/status - Update order status
     */
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id,
                                              @RequestBody Map<String, String> request) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            String status = request.get("status");
            order.setStatus(status);
            orderRepository.save(order);

            return ResponseEntity.ok(Map.of(
                    "message", "Order status updated",
                    "order", order
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== USERS ====================

    /**
     * GET /api/admin/users - Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to fetch users: " + e.getMessage()));
        }
    }

    /**
     * GET /api/admin/users/{id} - Get single user
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/users/{id} - Delete user
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Don't allow deleting admin users
            if ("ADMIN".equals(user.getRole())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot delete admin users"));
            }

            userRepository.delete(user);

            return ResponseEntity.ok(Map.of(
                    "message", "User deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }

    // ==================== STATISTICS ====================

    /**
     * GET /api/admin/stats - Get dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStatistics() {
        try {
            long totalProducts = productRepository.count();
            long totalUsers = userRepository.count();
            long totalOrders = orderRepository.count();

            // Calculate total sales
            List<Order> orders = orderRepository.findAll();
            BigDecimal totalSales = orders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return ResponseEntity.ok(Map.of(
                    "totalProducts", totalProducts,
                    "totalUsers", totalUsers,
                    "totalOrders", totalOrders,
                    "totalSales", totalSales
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to fetch statistics: " + e.getMessage()));
        }
    }
}
