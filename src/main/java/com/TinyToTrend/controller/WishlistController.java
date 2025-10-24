package com.TinyToTrend.controller;

import com.TinyToTrend.model.Product;
import com.TinyToTrend.model.User;
import com.TinyToTrend.model.Wishlist;
import com.TinyToTrend.repository.ProductRepository;
import com.TinyToTrend.repository.UserRepository;
import com.TinyToTrend.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * GET /api/wishlist - Get user's wishlist
     */
    @GetMapping
    public ResponseEntity<?> getWishlist(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Wishlist> wishlist = wishlistRepository.findByUserOrderByCreatedAtDesc(user);
            
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to fetch wishlist: " + e.getMessage()));
        }
    }

    /**
     * POST /api/wishlist - Add product to wishlist
     */
    @PostMapping
    public ResponseEntity<?> addToWishlist(@RequestBody Map<String, Object> request, 
                                          Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Long productId = Long.parseLong(request.get("productId").toString());

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Check if already in wishlist
            if (wishlistRepository.existsByUserAndProduct(user, product)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Product already in wishlist"));
            }

            Wishlist wishlistItem = new Wishlist(user, product);
            wishlistRepository.save(wishlistItem);

            return ResponseEntity.ok(Map.of(
                    "message", "Product added to wishlist",
                    "wishlistItem", wishlistItem
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to add to wishlist: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/wishlist/{id} - Remove product from wishlist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long id, 
                                               Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Wishlist wishlistItem = wishlistRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Wishlist item not found"));

            // Check if the wishlist item belongs to the authenticated user
            if (!wishlistItem.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unauthorized access"));
            }

            wishlistRepository.delete(wishlistItem);

            return ResponseEntity.ok(Map.of(
                    "message", "Product removed from wishlist"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to remove from wishlist: " + e.getMessage()));
        }
    }

    /**
     * GET /api/wishlist/check/{productId} - Check if product is in wishlist
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<?> checkWishlist(@PathVariable Long productId, 
                                          Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            boolean isInWishlist = wishlistRepository.existsByUserAndProduct(user, product);

            return ResponseEntity.ok(Map.of(
                    "isInWishlist", isInWishlist
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to check wishlist: " + e.getMessage()));
        }
    }

    /**
     * GET /api/wishlist/count - Get wishlist count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getWishlistCount(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            long count = wishlistRepository.countByUser(user);

            return ResponseEntity.ok(Map.of(
                    "count", count
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to get wishlist count: " + e.getMessage()));
        }
    }
}
