package com.TinyToTrend.TinyToTrend.service;

import com.TinyToTrend.TinyToTrend.exception.ProductNotFoundException;
import com.TinyToTrend.TinyToTrend.model.Product;
import com.TinyToTrend.TinyToTrend.model.Category;
import com.TinyToTrend.TinyToTrend.repository.ProductRepository;
import com.TinyToTrend.TinyToTrend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of ProductService interface
 * Contains all business logic for product operations
 */
@Service // This tells Spring this is a service component
public class ProductServiceImpl implements ProductService {
    
    // Inject the repository to interact with database
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Override
    public List<Product> getAllProducts() {
        // Simply return all products from database
        return productRepository.findAll();
    }
    
    @Override
    public Product getProductById(Long id) {
        // findById returns Optional<Product>, we handle if not found
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
    
    @Override
    public Product createProduct(Product product) {
        // Set creation timestamp
        product.setCreatedAt(LocalDateTime.now());
        
        // Validate that category exists (optional but good practice)
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + product.getCategory().getId()));
            product.setCategory(category);
        }
        
        // Save and return the product (JPA will assign ID automatically)
        return productRepository.save(product);
    }
    
    @Override
    public Product updateProduct(Long id, Product productDetails) {
        // First check if product exists
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        
        // Update fields with new values
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setImageUrl(productDetails.getImageUrl());
        existingProduct.setStockQuantity(productDetails.getStockQuantity());
        
        // Update category if provided
        if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
            Category category = categoryRepository.findById(productDetails.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + productDetails.getCategory().getId()));
            existingProduct.setCategory(category);
        }
        
        // Save and return updated product
        return productRepository.save(existingProduct);
    }
    
    @Override
    public void deleteProduct(Long id) {
        // Check if product exists before deleting
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        
        // Delete the product
        productRepository.delete(product);
    }
    
    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        // Find category first
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        
        // Return products in that category
        return productRepository.findByCategory(category);
    }
}
