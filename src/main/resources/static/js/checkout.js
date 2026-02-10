/**
 * Checkout Page JavaScript for TinyToTrend
 * Handles checkout form and Razorpay payment integration
 */

let token = localStorage.getItem("token");
let userName = localStorage.getItem("userName") || "Profile";
let userEmail = localStorage.getItem("userEmail") || "";
let subtotal = 0;

// Initialize on page load
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("navUserName").textContent = userName;
    
    // Pre-fill name if available
    if (userName && userName !== "Profile") {
        document.getElementById("name").value = userName;
    }
    
    fetchCartAndShowSummary();
});

async function fetchCartAndShowSummary() {
    if (!token) {
        window.location.href = "/login.html";
        return;
    }
    
    const res = await fetch(API_BASE_URL + "/cart", {
        headers: { "Authorization": "Bearer " + token }
    });
    let cart = await res.json();
    document.getElementById("cartCount").textContent = cart.length;
    const summaryList = document.getElementById("summaryItemList");
    summaryList.innerHTML = "";

    subtotal = 0;
    cart.forEach(item => {
        let img = item.product.imageUrl || 'https://placehold.co/100x120/EEE/222?text=No+Image';
        subtotal += (item.product.price * item.quantity);
        summaryList.innerHTML += `
            <li class="summary-item">
                <div class="item-img"><img src="${img}" alt="${item.product.name}"></div>
                <div class="item-info">
                    <div class="item-title">${item.product.name}</div>
                    <div class="item-meta">${item.product.category} • ${item.product.genderTag}</div>
                    <div class="item-price">${formatPrice(item.product.price)} × ${item.quantity}</div>
                </div>
            </li>
        `;
    });
    document.getElementById("summarySubtotal").textContent = formatPrice(subtotal);
    document.getElementById("summaryTotal").textContent = formatPrice(subtotal);

    // Empty cart handling
    if (!cart.length) {
        document.getElementById("orderBtn").disabled = true;
        document.getElementById("orderBtn").textContent = "Your cart is empty";
    }
}

// Place order with Razorpay payment
async function placeOrder(e) {
    e.preventDefault();
    
    const name = document.getElementById("name").value.trim();
    const address = document.getElementById("address").value.trim();
    const mobile = document.getElementById("mobile").value.trim();
    
    if (!name || !address || !mobile) {
        showOrderError("Please fill in all required fields");
        return;
    }
    
    const orderBtn = document.getElementById("orderBtn");
    orderBtn.disabled = true;
    orderBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
    document.getElementById("orderStatus").innerHTML = "";

    try {
        // Step 1: Create payment order on backend
        const shippingAddress = `${name}\n${address}\nMobile: ${mobile}`;
        
        const createOrderRes = await fetch(API_BASE_URL + "/orders/create-payment-order", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ shippingAddress })
        });
        
        if (!createOrderRes.ok) {
            const errorData = await createOrderRes.json();
            throw new Error(errorData.error || "Failed to create order");
        }
        
        const orderData = await createOrderRes.json();
        
        // Step 2: Open Razorpay checkout popup
        openRazorpayCheckout(orderData, name, mobile);
        
    } catch (err) {
        console.error("Order creation error:", err);
        showOrderError(err.message || "Failed to create order. Please try again.");
        orderBtn.disabled = false;
        orderBtn.innerHTML = 'PLACE ORDER';
    }
}

/**
 * Open Razorpay checkout popup
 */
function openRazorpayCheckout(orderData, customerName, customerMobile) {
    const options = {
        key: orderData.key,
        amount: orderData.amount,
        currency: orderData.currency || "INR",
        name: "TinyToTrend",
        description: "Order #" + orderData.orderId,
        order_id: orderData.razorpayOrderId,
        prefill: {
            name: customerName,
            email: userEmail,
            contact: customerMobile
        },
        theme: {
            color: "#ff3f6c"
        },
        handler: function(response) {
            // Payment successful - verify on backend
            verifyPayment(orderData.orderId, response);
        },
        modal: {
            ondismiss: function() {
                // User closed the popup without completing payment
                showOrderError("Payment cancelled. Your order is pending.");
                resetOrderButton();
            }
        }
    };
    
    const razorpay = new Razorpay(options);
    
    razorpay.on('payment.failed', function(response) {
        console.error("Payment failed:", response.error);
        showOrderError(`Payment failed: ${response.error.description}`);
        resetOrderButton();
    });
    
    razorpay.open();
}

/**
 * Verify payment with backend after successful Razorpay payment
 */
async function verifyPayment(orderId, razorpayResponse) {
    const orderBtn = document.getElementById("orderBtn");
    orderBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Verifying payment...';
    
    try {
        const verifyRes = await fetch(API_BASE_URL + "/orders/verify-payment", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                orderId: orderId.toString(),
                razorpayOrderId: razorpayResponse.razorpay_order_id,
                razorpayPaymentId: razorpayResponse.razorpay_payment_id,
                razorpaySignature: razorpayResponse.razorpay_signature
            })
        });
        
        const verifyData = await verifyRes.json();
        
        if (verifyRes.ok && verifyData.success) {
            // Payment verified successfully
            showOrderSuccess(orderId, razorpayResponse.razorpay_payment_id);
        } else {
            throw new Error(verifyData.error || "Payment verification failed");
        }
        
    } catch (err) {
        console.error("Payment verification error:", err);
        showOrderError("Payment verification failed. Please contact support with Order ID: " + orderId);
        resetOrderButton();
    }
}

/**
 * Show success message after successful payment
 */
function showOrderSuccess(orderId, paymentId) {
    document.getElementById("orderStatus").innerHTML = `
        <div class="order-status-success">
            <i class="fas fa-check-circle"></i> Payment Successful!
            <br><br>
            <strong>Order ID:</strong> ${orderId}<br>
            <strong>Payment ID:</strong> ${paymentId}
            <br><br>
            <a href="/profile.html" style="color:var(--white);text-decoration:underline;">View My Orders</a>
        </div>
    `;
    document.getElementById("orderBtn").style.display = "none";
    document.getElementById("checkoutForm").reset();
}

/**
 * Show error message
 */
function showOrderError(message) {
    document.getElementById("orderStatus").innerHTML = `
        <div class="order-status-fail">
            <i class="fas fa-exclamation-triangle"></i> ${message}
        </div>
    `;
}

/**
 * Reset order button to initial state
 */
function resetOrderButton() {
    const orderBtn = document.getElementById("orderBtn");
    orderBtn.disabled = false;
    orderBtn.innerHTML = 'PLACE ORDER';
}
