package com.TinyToTrend.service;

import com.TinyToTrend.dto.ProductDTO;
import com.TinyToTrend.model.Product;
import com.TinyToTrend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // Directory to store uploaded images
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    
    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // Get products with filters
    public List<Product> getProducts(String category, String gender, String search) {
        return productRepository.findWithFilters(category, gender, search);
    }
    
    // Get product by ID
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    
    // Get products by category
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    // Get products by gender
    public List<Product> getProductsByGender(String gender) {
        return productRepository.findByGenderTag(gender);
    }
    
    // Search products by name
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    // Get products in stock
    public List<Product> getInStockProducts() {
        return productRepository.findByStockQtyGreaterThan(0);
    }
    
    // ==================== ADMIN METHODS ====================
    
    /**
     * Create a new product (Admin only)
     */
    public Product createProduct(ProductDTO dto, MultipartFile image) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setGenderTag(dto.getGenderTag());
        product.setStockQty(dto.getStockQty());
        
        // Handle image upload
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            product.setImageUrl(imageUrl);
        } else {
            // Default image if none provided
            product.setImageUrl("/uploads/default-product.jpg");
        }
        
        return productRepository.save(product);
    }
    
    /**
     * Update existing product (Admin only)
     */
    public Product updateProduct(Long id, ProductDTO dto, MultipartFile image) {
        Product product = getProductById(id);
        
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setGenderTag(dto.getGenderTag());
        product.setStockQty(dto.getStockQty());
        
        // Update image if provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            product.setImageUrl(imageUrl);
        }
        
        return productRepository.save(product);
    }
    
    /**
     * Delete product (Admin only)
     */
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
    
    /**
     * Update only stock quantity (Admin only)
     */
    public Product updateStock(Long id, Integer newStock) {
        Product product = getProductById(id);
        product.setStockQty(newStock);
        return productRepository.save(product);
    }
    
    /**
     * Save uploaded image to filesystem
     */
    private String saveImage(MultipartFile file) {
        try {
            // Create uploads directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return URL path
            return "/uploads/" + uniqueFilename;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
    }
}
