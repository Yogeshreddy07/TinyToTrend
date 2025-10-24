package com.TinyToTrend.controller;

import com.TinyToTrend.model.CartItem;
import com.TinyToTrend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * Helper method to get authenticated user's email
     */
    private String getAuthenticatedUserEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
    
    /**
     * POST /api/cart - Add item to cart
     */
    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request) {
        try {
            String email = getAuthenticatedUserEmail();
            Long productId = Long.valueOf(request.get("productId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            CartItem item = cartService.addToCart(email, productId, quantity);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Item added to cart",
                    "cartItem", item
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * GET /api/cart - Get user's cart
     */
    @GetMapping
    public ResponseEntity<?> getCart() {
        try {
            String email = getAuthenticatedUserEmail();
            List<CartItem> cart = cartService.getUserCart(email);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * PUT /api/cart/{id} - Update cart item quantity
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        try {
            Integer quantity = request.get("quantity");
            CartItem item = cartService.updateCartItem(id, quantity);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Cart updated",
                    "cartItem", item
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/cart/{id} - Remove item from cart
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long id) {
        try {
            String email = getAuthenticatedUserEmail();
            cartService.removeCartItem(id, email);
            return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/cart - Clear entire cart
     */
    @DeleteMapping
    public ResponseEntity<?> clearCart() {
        try {
            String email = getAuthenticatedUserEmail();
            cartService.clearCart(email);
            return ResponseEntity.ok(Map.of("message", "Cart cleared"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * GET /api/cart/count - Get cart item count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getCartCount() {
        try {
            String email = getAuthenticatedUserEmail();
            int count = cartService.getCartCount(email);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
