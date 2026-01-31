package com.tinytotrend.common;

import com.tinytotrend.product.Product;
import com.tinytotrend.product.ProductRepository;
import com.tinytotrend.user.User;
import com.tinytotrend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Create admin user if not exists
        if (!userRepository.existsByEmail("admin@tinytotrend.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@tinytotrend.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("Admin user created: admin@tinytotrend.com / admin123");
        }
        
        // Create test user if not exists
        if (!userRepository.existsByEmail("user@test.com")) {
            User user = new User();
            user.setName("Test User");
            user.setEmail("user@test.com");
            user.setPassword(passwordEncoder.encode("test123"));
            user.setRole("USER");
            userRepository.save(user);
            System.out.println("Test user created: user@test.com / test123");
        }
        
        // Seed sample products if none exist
        if (productRepository.count() == 0) {
            seedProducts();
            System.out.println("Sample products created");
        }
    }
    
    private void seedProducts() {
        Product[] products = {
            new Product("Classic White T-Shirt", "Comfortable cotton t-shirt for everyday wear", 
                new BigDecimal("29.99"), "Tops", "Unisex", "/uploads/default-product.jpg", 100),
            new Product("Blue Denim Jeans", "Classic fit denim jeans with a modern look", 
                new BigDecimal("59.99"), "Bottoms", "Male", "/uploads/default-product.jpg", 75),
            new Product("Floral Summer Dress", "Lightweight dress perfect for summer days", 
                new BigDecimal("79.99"), "Dresses", "Female", "/uploads/default-product.jpg", 50),
            new Product("Leather Jacket", "Premium leather jacket with a timeless design", 
                new BigDecimal("199.99"), "Outerwear", "Unisex", "/uploads/default-product.jpg", 30),
            new Product("Running Sneakers", "Lightweight and comfortable for running and training", 
                new BigDecimal("89.99"), "Footwear", "Unisex", "/uploads/default-product.jpg", 120),
            new Product("Wool Sweater", "Warm and cozy sweater for cold weather", 
                new BigDecimal("69.99"), "Tops", "Unisex", "/uploads/default-product.jpg", 60),
            new Product("Silk Blouse", "Elegant silk blouse for professional settings", 
                new BigDecimal("99.99"), "Tops", "Female", "/uploads/default-product.jpg", 40),
            new Product("Chino Pants", "Versatile chino pants for casual or smart-casual looks", 
                new BigDecimal("49.99"), "Bottoms", "Male", "/uploads/default-product.jpg", 80)
        };
        
        for (Product product : products) {
            productRepository.save(product);
        }
    }
}
