package com.tinytotrend.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    // Paginated version of findWithFilters
    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:gender IS NULL OR p.genderTag = :gender) AND " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> findWithFiltersPaged(
            @Param("category") String category,
            @Param("gender") String gender,
            @Param("search") String search,
            Pageable pageable
    );
    
    // Search, filter with sorting by price ascending
    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:gender IS NULL OR p.genderTag = :gender) AND " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY p.price ASC")
    List<Product> findWithFiltersSortByPriceAsc(
            @Param("category") String category,
            @Param("gender") String gender,
            @Param("search") String search
    );
    
    // Paginated version with price ascending
    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:gender IS NULL OR p.genderTag = :gender) AND " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY p.price ASC")
    Page<Product> findWithFiltersSortByPriceAscPaged(
            @Param("category") String category,
            @Param("gender") String gender,
            @Param("search") String search,
            Pageable pageable
    );
    
    // Search, filter with sorting by price descending
    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:gender IS NULL OR p.genderTag = :gender) AND " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY p.price DESC")
    List<Product> findWithFiltersSortByPriceDesc(
            @Param("category") String category,
            @Param("gender") String gender,
            @Param("search") String search
    );
    
    // Paginated version with price descending
    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:gender IS NULL OR p.genderTag = :gender) AND " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY p.price DESC")
    Page<Product> findWithFiltersSortByPriceDescPaged(
            @Param("category") String category,
            @Param("gender") String gender,
            @Param("search") String search,
            Pageable pageable
    );
    
    List<Product> findByStockQtyGreaterThan(Integer qty);
}
