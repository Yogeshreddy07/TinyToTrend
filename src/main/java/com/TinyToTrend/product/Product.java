package com.tinytotrend.product;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    private String category;
    
    @Column(name = "gender_tag")
    private String genderTag;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "stock_qty", nullable = false)
    private Integer stockQty;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public Product() {}
    
    public Product(String name, String description, BigDecimal price, 
                   String category, String genderTag, String imageUrl, Integer stockQty) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.genderTag = genderTag;
        this.imageUrl = imageUrl;
        this.stockQty = stockQty;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getGenderTag() { return genderTag; }
    public void setGenderTag(String genderTag) { this.genderTag = genderTag; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Integer getStockQty() { return stockQty; }
    public void setStockQty(Integer stockQty) { this.stockQty = stockQty; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
