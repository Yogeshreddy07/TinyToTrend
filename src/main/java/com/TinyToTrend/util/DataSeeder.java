package com.TinyToTrend.util;

import com.TinyToTrend.model.Product;
import com.TinyToTrend.model.User;
import com.TinyToTrend.repository.ProductRepository;
import com.TinyToTrend.repository.UserRepository;
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
    public void run(String... args) throws Exception {
        seedUsers();
        seedProducts();
    }
    
    private void seedUsers() {
        // Seed admin user
        if (!userRepository.existsByEmail("admin@tinytotrend.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@tinytotrend.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            
            System.out.println("✅ Admin user created: admin@tinytotrend.com / admin123");
        }
        
        // Seed test user
        if (!userRepository.existsByEmail("test@tinytotrend.com")) {
            User testUser = new User();
            testUser.setName("Test User");
            testUser.setEmail("test@tinytotrend.com");
            testUser.setPassword(passwordEncoder.encode("test123"));
            testUser.setRole("USER");
            userRepository.save(testUser);
            
            System.out.println("✅ Test user created: test@tinytotrend.com / test123");
        }
    }
    
    private void seedProducts() {
        if (productRepository.count() == 0) {
            // Men's Products
            productRepository.save(new Product(
                "Men's Classic Cotton T-Shirt",
                "Premium quality 100% cotton t-shirt. Comfortable, breathable, and perfect for everyday wear.",
                new BigDecimal("29.99"),
                "men",
                "men",
                "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400",
                100
            ));
            
            productRepository.save(new Product(
                "Men's Slim Fit Denim Jeans",
                "Classic slim fit jeans with stretch comfort. Perfect for casual and semi-formal occasions.",
                new BigDecimal("49.99"),
                "men",
                "men",
                "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400",
                75
            ));
            
            productRepository.save(new Product(
                "Men's Casual Shirt",
                "Stylish casual shirt with modern fit. Great for office or weekend outings.",
                new BigDecimal("39.99"),
                "men",
                "men",
                "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=400",
                50
            ));
            
            productRepository.save(new Product(
                "Men's Sports Jacket",
                "Lightweight sports jacket with water-resistant fabric. Perfect for outdoor activities.",
                new BigDecimal("79.99"),
                "men",
                "men",
                "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400",
                30
            ));
            
            // Women's Products
            productRepository.save(new Product(
                "Women's Summer Dress",
                "Elegant summer dress with floral print. Light, comfortable, and perfect for warm weather.",
                new BigDecimal("59.99"),
                "women",
                "women",
                "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=400",
                60
            ));
            
            productRepository.save(new Product(
                "Women's Denim Jacket",
                "Classic denim jacket with vintage wash. A wardrobe essential for every season.",
                new BigDecimal("69.99"),
                "women",
                "women",
                "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400",
                40
            ));
            
            productRepository.save(new Product(
                "Women's Yoga Pants",
                "High-waist yoga pants with moisture-wicking fabric. Perfect for workouts and lounging.",
                new BigDecimal("44.99"),
                "women",
                "women",
                "https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=400",
                80
            ));
            
            productRepository.save(new Product(
                "Women's Formal Blouse",
                "Elegant formal blouse with pleated details. Perfect for office and formal occasions.",
                new BigDecimal("49.99"),
                "women",
                "women",
                "https://images.unsplash.com/photo-1564859228273-274232fdb516?w=400",
                45
            ));
            
            // Boys' Products
            productRepository.save(new Product(
                "Boys' Graphic Print T-Shirt",
                "Cool graphic print t-shirt for boys. Soft cotton fabric with vibrant colors.",
                new BigDecimal("19.99"),
                "boys",
                "boys",
                "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=400",
                90
            ));
            
            productRepository.save(new Product(
                "Boys' Cargo Shorts",
                "Comfortable cargo shorts with multiple pockets. Perfect for active kids.",
                new BigDecimal("24.99"),
                "boys",
                "boys",
                "https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?w=400",
                70
            ));
            
            productRepository.save(new Product(
                "Boys' Hooded Sweatshirt",
                "Cozy hooded sweatshirt with soft fleece lining. Great for cool weather.",
                new BigDecimal("34.99"),
                "boys",
                "boys",
                "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=400",
                55
            ));
            
            // Girls' Products
            productRepository.save(new Product(
                "Girls' Princess Dress",
                "Beautiful princess-style dress with tulle layers. Perfect for parties and special occasions.",
                new BigDecimal("39.99"),
                "girls",
                "girls",
                "https://images.unsplash.com/photo-1518831959646-742c3a14ebf7?w=400",
                50
            ));
            
            productRepository.save(new Product(
                "Girls' Denim Overalls",
                "Cute denim overalls with adjustable straps. Comfortable and stylish for everyday wear.",
                new BigDecimal("29.99"),
                "girls",
                "girls",
                "https://images.unsplash.com/photo-1596783074918-c84cb06531ca?w=400",
                65
            ));
            
            productRepository.save(new Product(
                "Girls' Printed Leggings",
                "Colorful printed leggings with stretchy fabric. Perfect for play and comfort.",
                new BigDecimal("16.99"),
                "girls",
                "girls",
                "https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?w=400",
                85
            ));
            
            System.out.println("✅ Sample products seeded: " + productRepository.count() + " products");
        }
    }
}
