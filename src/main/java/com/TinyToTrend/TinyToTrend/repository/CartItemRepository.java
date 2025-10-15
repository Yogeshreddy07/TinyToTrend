package com.TinyToTrend.TinyToTrend.repository;

import com.TinyToTrend.TinyToTrend.model.CartItem;
import com.TinyToTrend.TinyToTrend.model.User;
import com.TinyToTrend.TinyToTrend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    void deleteByUser(User user);
}
