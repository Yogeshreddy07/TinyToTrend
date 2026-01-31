package com.tinytotrend.order;

import com.tinytotrend.cart.CartItem;
import com.tinytotrend.cart.CartItemRepository;
import com.tinytotrend.user.User;
import com.tinytotrend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Order> getUserOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }
    
    public Order getOrderById(Long orderId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to order");
        }
        
        return order;
    }
    
    @Transactional
    public Order createOrder(String email, String shippingAddress, String paymentMethod) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        BigDecimal total = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Order order = new Order(user, total, shippingAddress, paymentMethod);
        
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(
                    order,
                    cartItem.getProduct(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getPrice()
            );
            order.addItem(orderItem);
        }
        
        order = orderRepository.save(order);
        
        cartItemRepository.deleteByUserId(user.getId());
        
        return order;
    }
    
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
}
