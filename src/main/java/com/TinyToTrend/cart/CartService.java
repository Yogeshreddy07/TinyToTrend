package com.tinytotrend.cart;

import com.tinytotrend.product.Product;
import com.tinytotrend.product.ProductRepository;
import com.tinytotrend.user.User;
import com.tinytotrend.user.UserRepository;
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
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<CartItem> getCartItems(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartItemRepository.findByUserId(user.getId());
    }
    
    public CartItem addToCart(String email, Long productId, Integer quantity) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(user.getId(), productId);
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartItemRepository.save(item);
        } else {
            CartItem cartItem = new CartItem(user, product, quantity);
            return cartItemRepository.save(cartItem);
        }
    }
    
    public CartItem updateQuantity(String email, Long cartItemId, Integer quantity) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }
        
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }
        
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }
    
    public void removeFromCart(String email, Long cartItemId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }
        
        cartItemRepository.delete(cartItem);
    }
    
    @Transactional
    public void clearCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartItemRepository.deleteByUserId(user.getId());
    }
}
