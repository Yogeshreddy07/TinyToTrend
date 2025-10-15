package com.TinyToTrend.TinyToTrend.service;

import com.TinyToTrend.TinyToTrend.model.Product;
import java.util.List;

/**
 * Service interface defining all product-related business operations
 * This follows the separation of concerns principle
 */
public interface ProductService {
    
    /**
     * Get all products from database
     * @return List of all products
     */
    List<Product> getAllProducts();
    
    /**
     * Get a single product by its ID
     * @param id - Product ID to search for
     * @return Product if found
     * @throws ProductNotFoundException if product doesn't exist
     */
    Product getProductById(Long id);
    
    /**
     * Create a new product
     * @param product - Product object to save
     * @return Saved product with generated ID
     */
    Product createProduct(Product product);
    
    /**
     * Update an existing product
     * @param id - ID of product to update
     * @param productDetails - New product data
     * @return Updated product
     * @throws ProductNotFoundException if product doesn't exist
     */
    Product updateProduct(Long id, Product productDetails);
    
    /**
     * Delete a product by ID
     * @param id - ID of product to delete
     * @throws ProductNotFoundException if product doesn't exist
     */
    void deleteProduct(Long id);
    
    /**
     * Get products by category ID
     * @param categoryId - Category ID to filter by
     * @return List of products in that category
     */
    List<Product> getProductsByCategory(Long categoryId);
}
