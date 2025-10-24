package com.TinyToTrend.service;

import com.TinyToTrend.model.*;
import com.TinyToTrend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * Create order from cart
     */
    @Transactional
    public Order createOrderFromCart(String email, String shippingAddress) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get cart items
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Calculate total and create order
        BigDecimal totalAmount = BigDecimal.ZERO;
        Order order = new Order(user, totalAmount, "PLACED", shippingAddress);
        order = orderRepository.save(order);
        
        // Create order items and reduce stock
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            
            // Check stock
            if (product.getStockQty() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            // Create order item
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            
            OrderItem orderItem = new OrderItem(
                order,
                product,
                cartItem.getQuantity(),
                product.getPrice()
            );
            order.addItem(orderItem);
            orderItemRepository.save(orderItem);
            
            // Reduce stock
            product.setStockQty(product.getStockQty() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        // Update order total
        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);
        
        // Clear cart
        cartItemRepository.deleteByUserId(user.getId());
        
        return order;
    }
    
    /**
     * Get user's orders
     */
    public List<Order> getUserOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    /**
     * Get order by ID
     */
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    /**
     * Update order status
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
    
    /**
     * Cancel order
     */
    @Transactional
    public Order cancelOrder(Long orderId, String email) {
        Order order = getOrderById(orderId);
        
        // Verify ownership
        if (!order.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }
        
        // Can only cancel if not shipped
        if ("SHIPPED".equals(order.getStatus()) || "DELIVERED".equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel order that has been shipped");
        }
        
        // Restore stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQty(product.getStockQty() + item.getQuantity());
            productRepository.save(product);
        }
        
        order.setStatus("CANCELLED");
        return orderRepository.save(order);
    }
    
    /**
     * Get all orders (Admin)
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
