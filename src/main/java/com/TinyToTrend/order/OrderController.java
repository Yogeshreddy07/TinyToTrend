package com.tinytotrend.order;

import com.tinytotrend.order.payment.RazorpayService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private RazorpayService razorpayService;
    
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        String email = authentication.getName();
        List<Order> orders = orderService.getUserOrders(email);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(
            Authentication authentication,
            @PathVariable Long orderId) {
        
        try {
            String email = authentication.getName();
            Order order = orderService.getOrderById(orderId, email);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            Authentication authentication,
            @RequestBody Map<String, String> request) {
        
        try {
            String email = authentication.getName();
            String shippingAddress = request.get("shippingAddress");
            String paymentMethod = request.get("paymentMethod");
            
            Order order = orderService.createOrder(email, shippingAddress, paymentMethod);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Create a Razorpay payment order.
     * This endpoint creates an order in our DB and a corresponding Razorpay order.
     * Returns the data needed to initialize Razorpay checkout on frontend.
     */
    @PostMapping("/create-payment-order")
    public ResponseEntity<?> createPaymentOrder(
            Authentication authentication,
            @RequestBody Map<String, String> request) {
        
        try {
            String email = authentication.getName();
            String shippingAddress = request.get("shippingAddress");
            
            // Create order with CREATED status and get cart total
            Order order = orderService.createOrderForPayment(email, shippingAddress);
            
            // Create Razorpay order
            String receiptId = "order_" + order.getId();
            com.razorpay.Order razorpayOrder = razorpayService.createRazorpayOrder(
                    order.getTotalAmount(), 
                    receiptId
            );
            
            // Save Razorpay order ID to our order
            String razorpayOrderId = razorpayOrder.get("id");
            orderService.updateRazorpayOrderId(order.getId(), razorpayOrderId);
            
            // Return data for frontend Razorpay checkout
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("razorpayOrderId", razorpayOrderId);
            response.put("amount", order.getTotalAmount().multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
            response.put("currency", "INR");
            response.put("key", razorpayService.getKeyId());
            
            return ResponseEntity.ok(response);
            
        } catch (RazorpayException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create payment order: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verify Razorpay payment after successful payment on frontend.
     * Validates the payment signature and updates order status.
     */
    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(
            Authentication authentication,
            @RequestBody Map<String, String> request) {
        
        try {
            String email = authentication.getName();
            Long orderId = Long.valueOf(request.get("orderId"));
            String razorpayOrderId = request.get("razorpayOrderId");
            String razorpayPaymentId = request.get("razorpayPaymentId");
            String razorpaySignature = request.get("razorpaySignature");
            
            // Verify order belongs to user
            Order order = orderService.getOrderById(orderId, email);
            
            // Verify the Razorpay order ID matches
            if (!razorpayOrderId.equals(order.getRazorpayOrderId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Order ID mismatch"));
            }
            
            // Verify payment signature
            boolean isValid = razorpayService.verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
            
            if (isValid) {
                // Payment verified - update order status
                orderService.markOrderAsPaid(orderId, razorpayPaymentId);
                
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Payment verified successfully",
                        "orderId", orderId
                ));
            } else {
                // Payment verification failed
                orderService.markOrderAsFailed(orderId);
                
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Payment verification failed"
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get user's orders (for order history page)
     */
    @GetMapping("/user")
    public ResponseEntity<List<Order>> getUserOrdersAlternate(Authentication authentication) {
        String email = authentication.getName();
        List<Order> orders = orderService.getUserOrders(email);
        return ResponseEntity.ok(orders);
    }
}
