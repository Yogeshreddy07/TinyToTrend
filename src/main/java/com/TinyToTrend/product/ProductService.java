package com.tinytotrend.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    
    /**
     * Get products with optional search, filter, and sort parameters.
     * All parameters are optional - if null/empty, they are ignored.
     * This maintains backward compatibility with existing functionality.
     * 
     * @param category Filter by category (optional)
     * @param gender Filter by gender tag (optional)
     * @param search Search by product name - case insensitive (optional)
     * @param sort Sort order: "priceAsc", "priceDesc", or null for default (optional)
     * @return List of products matching the criteria
     */
    public List<Product> getProductsWithSort(String category, String gender, String search, String sort) {
        // Normalize empty strings to null for consistent query behavior
        String normalizedCategory = (category != null && !category.isEmpty()) ? category : null;
        String normalizedGender = (gender != null && !gender.isEmpty()) ? gender : null;
        String normalizedSearch = (search != null && !search.isEmpty()) ? search : null;
        
        // Apply sorting based on sort parameter
        if ("priceAsc".equalsIgnoreCase(sort)) {
            return productRepository.findWithFiltersSortByPriceAsc(normalizedCategory, normalizedGender, normalizedSearch);
        } else if ("priceDesc".equalsIgnoreCase(sort)) {
            return productRepository.findWithFiltersSortByPriceDesc(normalizedCategory, normalizedGender, normalizedSearch);
        } else {
            // Default: no sorting (or use existing method)
            return productRepository.findWithFilters(normalizedCategory, normalizedGender, normalizedSearch);
        }
    }
    
    /**
     * Get products with pagination support.
     * 
     * @param category Filter by category (optional)
     * @param gender Filter by gender tag (optional)
     * @param search Search by product name - case insensitive (optional)
     * @param sort Sort order: "priceAsc", "priceDesc", or null for default (optional)
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @return Page of products matching the criteria
     */
    public Page<Product> getProductsPaged(String category, String gender, String search, String sort, int page, int size) {
        // Normalize empty strings to null for consistent query behavior
        String normalizedCategory = (category != null && !category.isEmpty()) ? category : null;
        String normalizedGender = (gender != null && !gender.isEmpty()) ? gender : null;
        String normalizedSearch = (search != null && !search.isEmpty()) ? search : null;
        
        Pageable pageable = PageRequest.of(page, size);
        
        // Apply sorting based on sort parameter
        if ("priceAsc".equalsIgnoreCase(sort)) {
            return productRepository.findWithFiltersSortByPriceAscPaged(normalizedCategory, normalizedGender, normalizedSearch, pageable);
        } else if ("priceDesc".equalsIgnoreCase(sort)) {
            return productRepository.findWithFiltersSortByPriceDescPaged(normalizedCategory, normalizedGender, normalizedSearch, pageable);
        } else {
            // Default: no sorting
            return productRepository.findWithFiltersPaged(normalizedCategory, normalizedGender, normalizedSearch, pageable);
        }
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
