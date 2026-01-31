/**
 * Cart Page JavaScript for TinyToTrend
 * Handles cart display and operations
 */

let token = localStorage.getItem("token");
let userName = localStorage.getItem("userName") || "Profile";

// Initialize on page load
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("navUserName").textContent = userName;
    fetchCart();
});

async function fetchCart() {
    if (!token) {
        window.location.href = "/login.html";
        return;
    }

    const res = await fetch(API_BASE_URL + "/cart", {
        headers: { "Authorization": "Bearer " + token }
    });
    let cart = await res.json();
    updateCartView(cart);
}

function updateCartView(cart) {
    document.getElementById("cartCount").textContent = cart.length;
    let ul = document.getElementById("cartList");
    ul.innerHTML = "";

    if (!cart.length) {
        document.getElementById("emptyCart").style.display = "";
        document.getElementById("priceSection").style.display = "none";
        return;
    } else {
        document.getElementById("emptyCart").style.display = "none";
        document.getElementById("priceSection").style.display = "";
    }

    let subtotal = 0;
    cart.forEach(item => {
        subtotal += (item.product.price * item.quantity);
        ul.innerHTML += `
            <li class="cart-item">
                <div class="cart-item-img">
                    <img src="${item.product.imageUrl || 'https://placehold.co/100x120/EEE/222?text=No+Image'}" alt="${item.product.name}">
                </div>
                <div class="cart-item-details">
                    <div class="cart-item-name">${item.product.name}</div>
                    <div class="cart-item-meta">${item.product.category} • ${item.product.genderTag}</div>
                    <div class="cart-item-price">${formatPrice(item.product.price)}</div>
                    <div class="cart-item-qty">
                        <button class="qty-btn" onclick="updateQuantity(${item.id}, ${item.quantity - 1})" ${item.quantity == 1 ? 'disabled' : ''}>-</button>
                        <span class="qty-num">${item.quantity}</span>
                        <button class="qty-btn" onclick="updateQuantity(${item.id}, ${item.quantity + 1})">+</button>
                    </div>
                    <div class="item-actions">
                        <button class="remove-btn" onclick="removeItem(${item.id})">
                            <i class="fas fa-trash"></i>
                            Remove
                        </button>
                    </div>
                </div>
            </li>
        `;
    });

    document.getElementById("summarySubtotal").textContent = formatPrice(subtotal);
    document.getElementById("summaryTotal").textContent = formatPrice(subtotal); // Delivery always Free
    document.getElementById("checkoutBtn").disabled = false;
}

async function updateQuantity(cartItemId, qty) {
    if (qty < 1) return;
    await fetch(API_BASE_URL + "/cart/" + cartItemId, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ quantity: qty })
    });
    fetchCart();
}

async function removeItem(cartItemId) {
    await fetch(API_BASE_URL + "/cart/" + cartItemId, {
        method: "DELETE",
        headers: { "Authorization": "Bearer " + token }
    });
    fetchCart();
}

function goCheckout() {
    window.location.href = "/checkout.html";
}
