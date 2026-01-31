/**
 * Wishlist Page JavaScript for TinyToTrend
 * Handles wishlist display and operations
 */

let currentUser = null;

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    checkAuthForWishlist();
    loadWishlist();
    updateCartCount();
});

function checkAuthForWishlist() {
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('userName');
    
    if (!token) {
        window.location.href = '/login.html';
        return;
    }
    
    currentUser = { token, name: userName };
    if (userName) {
        document.getElementById('userName').textContent = userName;
    }
}

async function loadWishlist() {
    const token = localStorage.getItem('token');
    const loading = document.getElementById('loading');
    const grid = document.getElementById('wishlistGrid');
    const empty = document.getElementById('emptyWishlist');
    
    try {
        const response = await fetch(`${API_BASE_URL}/wishlist`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (!response.ok) throw new Error('Failed to load wishlist');
        
        const wishlist = await response.json();
        
        loading.style.display = 'none';
        
        if (wishlist.length === 0) {
            empty.style.display = 'block';
            grid.style.display = 'none';
        } else {
            empty.style.display = 'none';
            grid.style.display = 'grid';
            displayWishlist(wishlist);
        }
        
        document.getElementById('itemCount').textContent = wishlist.length;
        document.getElementById('wishlistCount').textContent = wishlist.length;
        
    } catch (error) {
        console.error('Error loading wishlist:', error);
        loading.innerHTML = `
            <i class="fas fa-exclamation-triangle"></i>
            <p>Failed to load wishlist. Please try again.</p>
        `;
    }
}

function displayWishlist(wishlist) {
    const grid = document.getElementById('wishlistGrid');
    
    grid.innerHTML = wishlist.map(item => `
        <div class="wishlist-item">
            <div class="item-image">
                <img src="${item.product.imageUrl || 'https://via.placeholder.com/400x500?text=No+Image'}" 
                     alt="${item.product.name}"
                     onerror="this.src='https://via.placeholder.com/400x500?text=No+Image'">
                <button class="remove-btn" onclick="removeFromWishlist(${item.id})" title="Remove from wishlist">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="item-info">
                <div class="item-brand">${item.product.category}</div>
                <div class="item-name">${item.product.name}</div>
                <div class="item-price">₹${item.product.price.toFixed(2)}</div>
                <button class="add-to-bag-btn" onclick="moveToCart(${item.product.id}, ${item.id})">
                    <i class="fas fa-shopping-bag"></i> Move to Bag
                </button>
            </div>
        </div>
    `).join('');
}

async function removeFromWishlist(wishlistItemId) {
    const token = localStorage.getItem('token');
    
    try {
        const response = await fetch(`${API_BASE_URL}/wishlist/${wishlistItemId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (!response.ok) throw new Error('Failed to remove item');
        
        showToast('Removed from wishlist');
        loadWishlist();
        
    } catch (error) {
        console.error('Error removing from wishlist:', error);
        showToast('Failed to remove item');
    }
}

async function moveToCart(productId, wishlistItemId) {
    const token = localStorage.getItem('token');
    
    try {
        // Add to cart
        const cartResponse = await fetch(`${API_BASE_URL}/cart`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                productId: productId,
                quantity: 1
            })
        });
        
        if (!cartResponse.ok) throw new Error('Failed to add to cart');
        
        // Remove from wishlist
        await fetch(`${API_BASE_URL}/wishlist/${wishlistItemId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        showToast('Moved to bag successfully!');
        loadWishlist();
        updateCartCount();
        
    } catch (error) {
        console.error('Error moving to cart:', error);
        showToast('Failed to move to bag');
    }
}
