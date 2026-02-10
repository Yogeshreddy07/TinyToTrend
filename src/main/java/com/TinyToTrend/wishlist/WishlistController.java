package com.tinytotrend.wishlist;

import com.tinytotrend.product.Product;
import com.tinytotrend.product.ProductRepository;
import com.tinytotrend.user.User;
import com.tinytotrend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    
    @Autowired
    private WishlistRepository wishlistRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<List<Wishlist>> getWishlist(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Wishlist> wishlist = wishlistRepository.findByUserId(user.getId());
        return ResponseEntity.ok(wishlist);
    }
    
    @PostMapping
    public ResponseEntity<?> addToWishlist(
            Authentication authentication,
            @RequestBody Map<String, Object> request) {
        
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Long productId = Long.valueOf(request.get("productId").toString());
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            
            if (wishlistRepository.existsByUserIdAndProductId(user.getId(), productId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product already in wishlist"));
            }
            
            Wishlist wishlist = new Wishlist(user, product);
            wishlistRepository.save(wishlist);
            
            return ResponseEntity.ok(Map.of("message", "Product added to wishlist", "id", wishlist.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{wishlistId}")
    @Transactional
    public ResponseEntity<?> removeFromWishlist(
            Authentication authentication,
            @PathVariable Long wishlistId) {
        
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Wishlist wishlist = wishlistRepository.findById(wishlistId)
                    .orElseThrow(() -> new RuntimeException("Wishlist item not found"));
            
            if (!wishlist.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Not authorized to delete this item"));
            }
            
            wishlistRepository.deleteById(wishlistId);
            return ResponseEntity.ok(Map.of("message", "Wishlist item removed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/check/{productId}")
    public ResponseEntity<?> checkInWishlist(
            Authentication authentication,
            @PathVariable Long productId) {
        
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            boolean exists = wishlistRepository.existsByUserIdAndProductId(user.getId(), productId);
            return ResponseEntity.ok(Map.of("inWishlist", exists));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
