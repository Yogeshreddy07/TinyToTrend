package com.tinytotrend.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(Authentication authentication) {
        String email = authentication.getName();
        List<CartItem> cartItems = cartService.getCartItems(email);
        return ResponseEntity.ok(cartItems);
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            Authentication authentication,
            @RequestBody Map<String, Object> request) {
        
        try {
            String email = authentication.getName();
            Long productId = Long.valueOf(request.get("productId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            CartItem item = cartService.addToCart(email, productId, quantity);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateQuantity(
            Authentication authentication,
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> request) {
        
        try {
            String email = authentication.getName();
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            CartItem item = cartService.updateQuantity(email, itemId, quantity);
            if (item == null) {
                return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
            }
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> removeFromCart(
            Authentication authentication,
            @PathVariable Long itemId) {
        
        try {
            String email = authentication.getName();
            cartService.removeFromCart(email, itemId);
            return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication authentication) {
        try {
            String email = authentication.getName();
            cartService.clearCart(email);
            return ResponseEntity.ok(Map.of("message", "Cart cleared"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
