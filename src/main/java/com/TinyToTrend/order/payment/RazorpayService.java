package com.tinytotrend.order.payment;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;

/**
 * Service class for Razorpay payment integration.
 * Handles order creation and payment signature verification.
 */
@Service
public class RazorpayService {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayService.class);

    private static final String KEY_ID = "rzp_test_SEIWPsO0oWZMrA";
    private static final String KEY_SECRET = "k7MDZZZfZFQmtzYJabrnvEZb";

    private final RazorpayClient razorpayClient;

    public RazorpayService() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(KEY_ID, KEY_SECRET);
        logger.info("RazorpayService initialized with KEY_ID: {}", KEY_ID);
    }

    /**
     * Get the Razorpay Key ID for frontend use.
     * Only KEY_ID is exposed, never the secret.
     */
    public String getKeyId() {
        return KEY_ID;
    }

    /**
     * Create a Razorpay order for payment.
     *
     * @param amount    Amount in INR (will be converted to paise)
     * @param receiptId Unique receipt/order ID from our system
     * @return Razorpay Order object containing order_id
     * @throws RazorpayException if order creation fails
     */
    public Order createRazorpayOrder(BigDecimal amount, String receiptId) throws RazorpayException {
        // Convert amount to paise (smallest currency unit for INR)
        int amountInPaise = amount.multiply(BigDecimal.valueOf(100)).intValue();

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receiptId);
        orderRequest.put("payment_capture", 1); // Auto capture payment

        logger.info("Creating Razorpay order - Amount: {} paise, Receipt: {}", amountInPaise, receiptId);

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        String orderId = razorpayOrder.get("id");
        logger.info("Razorpay order created successfully - Order ID: {}", orderId);

        return razorpayOrder;
    }

    /**
     * Verify Razorpay payment signature to ensure payment authenticity.
     * Uses HMAC SHA256 algorithm as per Razorpay documentation.
     *
     * @param razorpayOrderId   The Razorpay order ID
     * @param razorpayPaymentId The Razorpay payment ID
     * @param razorpaySignature The signature received from Razorpay
     * @return true if signature is valid, false otherwise
     */
    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            // Generate expected signature
            String payload = razorpayOrderId + "|" + razorpayPaymentId;
            
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            boolean isValid = Utils.verifyPaymentSignature(options, KEY_SECRET);

            logger.info("Payment signature verification - Order: {}, Payment: {}, Valid: {}", 
                    razorpayOrderId, razorpayPaymentId, isValid);

            return isValid;
        } catch (Exception e) {
            logger.error("Error verifying payment signature: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Generate HMAC SHA256 signature manually (alternative method).
     */
    private String generateHmacSha256(String data, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            logger.error("Error generating HMAC: {}", e.getMessage());
            return null;
        }
    }
}
