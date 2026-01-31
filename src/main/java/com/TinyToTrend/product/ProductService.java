package com.tinytotrend.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getProducts(String category, String gender, String search) {
        return productRepository.findWithFilters(category, gender, search);
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public List<Product> getProductsByGender(String gender) {
        return productRepository.findByGenderTag(gender);
    }
    
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    public List<Product> getInStockProducts() {
        return productRepository.findByStockQtyGreaterThan(0);
    }
    
    public Product createProduct(ProductDTO dto, MultipartFile image) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setGenderTag(dto.getGenderTag());
        product.setStockQty(dto.getStockQty());
        
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            product.setImageUrl(imageUrl);
        } else {
            product.setImageUrl("/uploads/default-product.jpg");
        }
        
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, ProductDTO dto, MultipartFile image) {
        Product product = getProductById(id);
        
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setGenderTag(dto.getGenderTag());
        product.setStockQty(dto.getStockQty());
        
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            product.setImageUrl(imageUrl);
        } else if (dto.getImageUrl() != null) {
            product.setImageUrl(dto.getImageUrl());
        }
        
        return productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
    
    public Product updateStock(Long id, Integer quantity) {
        Product product = getProductById(id);
        product.setStockQty(quantity);
        return productRepository.save(product);
    }
    
    private String saveImage(MultipartFile image) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename != null ? 
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newFilename = UUID.randomUUID().toString() + extension;
            
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            return "/uploads/" + newFilename;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
    }
}
