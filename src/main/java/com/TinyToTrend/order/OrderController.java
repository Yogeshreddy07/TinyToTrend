package com.tinytotrend.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        String email = authentication.getName();
        List<Order> orders = orderService.getUserOrders(email);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(
            Authentication authentication,
            @PathVariable Long orderId) {
        
        try {
            String email = authentication.getName();
            Order order = orderService.getOrderById(orderId, email);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            Authentication authentication,
            @RequestBody Map<String, String> request) {
        
        try {
            String email = authentication.getName();
            String shippingAddress = request.get("shippingAddress");
            String paymentMethod = request.get("paymentMethod");
            
            Order order = orderService.createOrder(email, shippingAddress, paymentMethod);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
