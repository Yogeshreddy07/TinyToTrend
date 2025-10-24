package com.TinyToTrend.dto;

import java.math.BigDecimal;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String genderTag;
    private String imageUrl;
    private Integer stockQty;
    
    // Constructors
    public ProductDTO() {}
    
    public ProductDTO(String name, String description, BigDecimal price, 
                      String category, String genderTag, Integer stockQty) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.genderTag = genderTag;
        this.stockQty = stockQty;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getGenderTag() {
        return genderTag;
    }
    
    public void setGenderTag(String genderTag) {
        this.genderTag = genderTag;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Integer getStockQty() {
        return stockQty;
    }
    
    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }
}
