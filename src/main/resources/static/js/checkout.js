/**
 * Checkout Page JavaScript for TinyToTrend
 * Handles checkout form and order placement
 */

let token = localStorage.getItem("token");
let userName = localStorage.getItem("userName") || "Profile";
let subtotal = 0;

// Initialize on page load
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("navUserName").textContent = userName;
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

// Place order API
async function placeOrder(e) {
    e.preventDefault();
    const name = document.getElementById("name").value.trim();
    const address = document.getElementById("address").value.trim();
    const mobile = document.getElementById("mobile").value.trim();
    if (!name || !address || !mobile) return;
    const orderBtn = document.getElementById("orderBtn");
    orderBtn.disabled = true;
    orderBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Placing...';
    document.getElementById("orderStatus").innerHTML = "";

    try {
        // Place order POST /api/orders
        const res = await fetch(API_BASE_URL + "/orders", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                shippingAddress: address,
                mobile
            })
        });
        if (!res.ok) throw new Error("Server error");
        const data = await res.json();
        document.getElementById("orderStatus").innerHTML =
            `<div class="order-status-success"><i class="fas fa-check-circle"></i> Order placed successfully! <br>Your Order ID: <b>${data.orderId || ""}</b> <br><br><a href="/orders.html" style="color:var(--white);text-decoration:underline;">View Orders</a></div>`;
        orderBtn.style.display = "none";
        // Optionally, clear form
        document.getElementById("checkoutForm").reset();
    } catch (err) {
        document.getElementById("orderStatus").innerHTML =
            `<div class="order-status-fail"><i class="fas fa-exclamation-triangle"></i> Failed to place order. Please try again.</div>`;
        orderBtn.disabled = false;
        orderBtn.innerHTML = 'PLACE ORDER';
    }
}
