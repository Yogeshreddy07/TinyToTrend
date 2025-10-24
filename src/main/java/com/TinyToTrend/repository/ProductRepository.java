package com.TinyToTrend.repository;

import com.TinyToTrend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find by category
    List<Product> findByCategory(String category);
    
    // Find by gender tag
    List<Product> findByGenderTag(String genderTag);
    
    // Search by name (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Advanced filter query
    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:gender IS NULL OR p.genderTag = :gender) AND " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> findWithFilters(
            @Param("category") String category,
            @Param("gender") String gender,
            @Param("search") String search
    );
    
    // Find products in stock
    List<Product> findByStockQtyGreaterThan(Integer qty);
}
