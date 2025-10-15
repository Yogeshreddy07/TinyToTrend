package com.TinyToTrend.TinyToTrend.exception;

/**
 * Custom exception thrown when a product is not found
 * This helps us return proper HTTP status codes (404 Not Found)
 */
public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
    
    public ProductNotFoundException(String message) {
        super(message);
    }
}
