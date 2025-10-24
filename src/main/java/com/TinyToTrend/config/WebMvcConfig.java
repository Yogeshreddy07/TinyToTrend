package com.TinyToTrend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static resources
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/");
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Map URL paths to template names (without .html extension)
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/register.html").setViewName("register");
        registry.addViewController("/profile.html").setViewName("profile");
        registry.addViewController("/cart.html").setViewName("cart");
        registry.addViewController("/orders.html").setViewName("orders");
        registry.addViewController("/wishlist.html").setViewName("wishlist");
        registry.addViewController("/product-detail.html").setViewName("product-detail");
    }
}
