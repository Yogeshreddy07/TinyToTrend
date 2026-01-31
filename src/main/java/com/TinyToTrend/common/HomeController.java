package com.tinytotrend.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/login.html")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register.html")
    public String register() {
        return "register";
    }
    
    @GetMapping("/product-detail.html")
    public String productDetail() {
        return "product-detail";
    }
    
    @GetMapping("/cart.html")
    public String cart() {
        return "cart";
    }
    
    @GetMapping("/checkout.html")
    public String checkout() {
        return "checkout";
    }
    
    @GetMapping("/profile.html")
    public String profile() {
        return "profile";
    }
    
    @GetMapping("/wishlist.html")
    public String wishlist() {
        return "wishlist";
    }
    
    @GetMapping("/admin/dashboard.html")
    public String adminDashboard() {
        return "admin/dashboard";
    }
}
