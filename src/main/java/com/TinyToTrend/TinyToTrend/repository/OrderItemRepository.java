package com.TinyToTrend.TinyToTrend.repository;

import com.TinyToTrend.TinyToTrend.model.OrderItem;
import com.TinyToTrend.TinyToTrend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}
