package com.TinyToTrend.TinyToTrend;

import com.TinyToTrend.TinyToTrend.model.*;
import com.TinyToTrend.TinyToTrend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@SpringBootApplication
public class TinyToTrendApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(TinyToTrendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Only add sample data if database is empty
        if (userRepository.count() == 0) {
            System.out.println("🚀 Inserting sample data...");
            insertSampleData();
            System.out.println("✅ Sample data inserted successfully!");
        } else {
            System.out.println("📊 Database already contains data, skipping sample data insertion.");
        }
    }

    private void insertSampleData() {
        // Create Categories
        Category kidsCategory = new Category("Kids", "Clothing for children aged 2-12");
        Category boysCategory = new Category("Boys", "Clothing for boys aged 13-18");
        Category girlsCategory = new Category("Girls", "Clothing for girls aged 13-18");
        Category womenCategory = new Category("Women", "Clothing for women");
        
        categoryRepository.save(kidsCategory);
        categoryRepository.save(boysCategory);
        categoryRepository.save(girlsCategory);
        categoryRepository.save(womenCategory);

        // Create Users
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@tinytotrend.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setRole(User.Role.ADMIN);
        userRepository.save(adminUser);

        User customerUser = new User();
        customerUser.setUsername("customer1");
        customerUser.setEmail("customer1@example.com");
        customerUser.setPassword(passwordEncoder.encode("password123"));
        customerUser.setFirstName("Jane");
        customerUser.setLastName("Smith");
        customerUser.setRole(User.Role.USER);
        userRepository.save(customerUser);

        // Create Products
        Product product1 = new Product(
            "Kids Rainbow T-Shirt",
            "Colorful cotton t-shirt perfect for kids",
            new BigDecimal("19.99"),
            50,
            kidsCategory
        );
        product1.setImageUrl("/images/kids-rainbow-tshirt.jpg");
        productRepository.save(product1);

        Product product2 = new Product(
            "Women's Summer Dress",
            "Light and comfortable summer dress",
            new BigDecimal("39.99"),
            25,
            womenCategory
        );
        product2.setImageUrl("/images/women-summer-dress.jpg");
        productRepository.save(product2);

        Product product3 = new Product(
            "Boys Denim Jeans",
            "Durable denim jeans for active boys",
            new BigDecimal("29.99"),
            30,
            boysCategory
        );
        product3.setImageUrl("/images/boys-denim-jeans.jpg");
        productRepository.save(product3);

        System.out.println("📦 Created " + categoryRepository.count() + " categories");
        System.out.println("👥 Created " + userRepository.count() + " users");
        System.out.println("🛍️ Created " + productRepository.count() + " products");
    }
}
