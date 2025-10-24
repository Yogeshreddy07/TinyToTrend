package com.TinyToTrend.service;

import com.TinyToTrend.model.CartItem;
import com.TinyToTrend.model.Product;
import com.TinyToTrend.model.User;
import com.TinyToTrend.repository.CartItemRepository;
import com.TinyToTrend.repository.ProductRepository;
import com.TinyToTrend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * Add item to cart or update quantity if already exists
     */
    @Transactional
    public CartItem addToCart(String email, Long productId, Integer quantity) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check stock availability
        if (product.getStockQty() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStockQty());
        }
        
        // Check if product already in cart
        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProductId(user, productId);
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            
            if (product.getStockQty() < newQuantity) {
                throw new RuntimeException("Insufficient stock. Available: " + product.getStockQty());
            }
            
            item.setQuantity(newQuantity);
            return cartItemRepository.save(item);
        } else {
            // Add new item
            CartItem newItem = new CartItem(user, product, quantity);
            return cartItemRepository.save(newItem);
        }
    }
    
    /**
     * Get user's cart items
     */
    public List<CartItem> getUserCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return cartItemRepository.findByUser(user);
    }
    
    /**
     * Update cart item quantity
     */
    @Transactional
    public CartItem updateCartItem(Long cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }
        
        // Check stock
        if (item.getProduct().getStockQty() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + item.getProduct().getStockQty());
        }
        
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }
    
    /**
     * Remove item from cart
     */
    @Transactional
    public void removeCartItem(Long cartItemId, String email) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        // Verify ownership
        if (!item.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }
        
        cartItemRepository.delete(item);
    }
    
    /**
     * Clear user's cart
     */
    @Transactional
    public void clearCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        cartItemRepository.deleteByUserId(user.getId());
    }
    
    /**
     * Get cart total count
     */
    public int getCartCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<CartItem> items = cartItemRepository.findByUser(user);
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }
}
