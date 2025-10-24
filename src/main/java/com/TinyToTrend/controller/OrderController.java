package com.TinyToTrend.controller;

import com.TinyToTrend.model.Order;
import com.TinyToTrend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * Helper method to get authenticated user's email
     */
    private String getAuthenticatedUserEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
    
    /**
     * POST /api/orders/checkout - Create order from cart
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, String> request) {
        try {
            String email = getAuthenticatedUserEmail();
            String shippingAddress = request.get("shippingAddress");
            
            if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Shipping address is required"));
            }
            
            Order order = orderService.createOrderFromCart(email, shippingAddress);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Order placed successfully",
                    "order", order
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * GET /api/orders/user - Get user's orders
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserOrders() {
        try {
            String email = getAuthenticatedUserEmail();
            List<Order> orders = orderService.getUserOrders(email);
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * GET /api/orders/{id} - Get order details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            String email = getAuthenticatedUserEmail();
            Order order = orderService.getOrderById(id);
            
            // Verify ownership
            if (!order.getUser().getEmail().equals(email)) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Unauthorized"));
            }
            
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/orders/{id} - Cancel order
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            String email = getAuthenticatedUserEmail();
            Order order = orderService.cancelOrder(id, email);
            return ResponseEntity.ok(Map.of(
                    "message", "Order cancelled successfully",
                    "order", order
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
