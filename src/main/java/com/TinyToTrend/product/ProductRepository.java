package com.tinytotrend.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategory(String category);
    List<Product> findByGenderTag(String genderTag);
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:gender IS NULL OR p.genderTag = :gender) AND " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> findWithFilters(
            @Param("category") String category,
            @Param("gender") String gender,
            @Param("search") String search
    );
    
    List<Product> findByStockQtyGreaterThan(Integer qty);
}
