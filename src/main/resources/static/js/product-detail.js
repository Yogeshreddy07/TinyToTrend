/**
 * Product Detail Page JavaScript for TinyToTrend
 * Handles product display and cart/wishlist operations
 */

let currentProduct = null;
let currentQuantity = 1;
let isInWishlist = false;

// Get product ID from URL parameter
function getProductId() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    const productId = getProductId();
    if (!productId) {
        showError();
        return;
    }
    
    checkAuthForProduct();
    loadProduct(productId);
    updateCounts();
});

function checkAuthForProduct() {
    const userName = localStorage.getItem('userName');
    if (userName) {
        document.getElementById('userName').textContent = userName;
    }
}

async function loadProduct(productId) {
    try {
        const response = await fetch(`${API_BASE_URL}/products/${productId}`);
        
        if (!response.ok) {
            throw new Error('Product not found');
        }
        
        const product = await response.json();
        currentProduct = product;
        displayProduct(product);
        
        // Check if in wishlist
        const token = localStorage.getItem('token');
        if (token) {
            checkWishlistStatus(productId);
        }
        
    } catch (error) {
        console.error('Error loading product:', error);
        showError();
    }
}

function displayProduct(product) {
    document.getElementById('loading').style.display = 'none';
    document.getElementById('productContent').style.display = 'block';
    
    // Update breadcrumb
    document.getElementById('breadcrumbCategory').textContent = product.category;
    document.getElementById('breadcrumbProduct').textContent = product.name;
    
    // Update product info
    document.getElementById('productImage').src = product.imageUrl || 'https://via.placeholder.com/500x500?text=No+Image';
    document.getElementById('productImage').alt = product.name;
    document.getElementById('productBrand').textContent = product.category.toUpperCase();
    document.getElementById('productName').textContent = product.name;
    document.getElementById('productPrice').textContent = `₹${product.price.toFixed(2)}`;
    document.getElementById('productDescription').textContent = product.description;
    
    // Update details
    document.getElementById('detailCategory').textContent = product.category;
    document.getElementById('detailGender').textContent = product.genderTag;
    document.getElementById('detailStock').textContent = `${product.stockQty} items`;
    
    // Update stock status
    const stockStatus = document.getElementById('stockStatus');
    if (product.stockQty > 0) {
        stockStatus.innerHTML = `
            <div class="stock-indicator stock-in"></div>
            <span>In Stock (${product.stockQty} available)</span>
        `;
        document.getElementById('addToCartBtn').disabled = false;
    } else {
        stockStatus.innerHTML = `
            <div class="stock-indicator stock-out"></div>
            <span>Out of Stock</span>
        `;
        document.getElementById('addToCartBtn').disabled = true;
        document.getElementById('qtyPlus').disabled = true;
    }
    
    // Update page title
    document.title = `${product.name} - TinyToTrend`;
}

function showError() {
    document.getElementById('loading').style.display = 'none';
    document.getElementById('errorState').style.display = 'block';
}

async function checkWishlistStatus(productId) {
    const token = localStorage.getItem('token');
    if (!token) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/wishlist`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
            const wishlist = await response.json();
            isInWishlist = wishlist.some(item => item.product.id == productId);
            updateWishlistButton();
        }
    } catch (error) {
        console.error('Error checking wishlist:', error);
    }
}

function updateWishlistButton() {
    const btn = document.getElementById('wishlistBtn');
    if (isInWishlist) {
        btn.classList.add('active');
        btn.innerHTML = '<i class="fas fa-heart"></i>';
    } else {
        btn.classList.remove('active');
        btn.innerHTML = '<i class="far fa-heart"></i>';
    }
}

async function toggleWishlist() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }
    
    try {
        if (isInWishlist) {
            // Remove from wishlist - need wishlist item ID
            const wishlistResponse = await fetch(`${API_BASE_URL}/wishlist`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            
            if (wishlistResponse.ok) {
                const wishlist = await wishlistResponse.json();
                const wishlistItem = wishlist.find(item => item.product.id == currentProduct.id);
                
                if (wishlistItem) {
                    await fetch(`${API_BASE_URL}/wishlist/${wishlistItem.id}`, {
                        method: 'DELETE',
                        headers: { 'Authorization': `Bearer ${token}` }
                    });
                    
                    isInWishlist = false;
                    showToast('Removed from wishlist', 'success');
                }
            }
        } else {
            // Add to wishlist
            const response = await fetch(`${API_BASE_URL}/wishlist`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ productId: currentProduct.id })
            });
            
            if (response.ok) {
                isInWishlist = true;
                showToast('Added to wishlist', 'success');
            } else {
                throw new Error('Failed to add to wishlist');
            }
        }
        
        updateWishlistButton();
        updateCounts();
        
    } catch (error) {
        console.error('Error toggling wishlist:', error);
        showToast('Failed to update wishlist', 'error');
    }
}

function changeQuantity(change) {
    const newQuantity = currentQuantity + change;
    
    if (newQuantity < 1 || newQuantity > currentProduct.stockQty) {
        return;
    }
    
    currentQuantity = newQuantity;
    document.getElementById('quantity').textContent = currentQuantity;
    
    // Update button states
    document.getElementById('qtyMinus').disabled = currentQuantity <= 1;
    document.getElementById('qtyPlus').disabled = currentQuantity >= currentProduct.stockQty;
}

async function addToCart() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }
    
    const btn = document.getElementById('addToCartBtn');
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Adding...';
    
    try {
        const response = await fetch(`${API_BASE_URL}/cart`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                productId: currentProduct.id,
                quantity: currentQuantity
            })
        });
        
        if (response.ok) {
            showToast(`Added ${currentQuantity} item(s) to cart`, 'success');
            updateCounts();
        } else {
            throw new Error('Failed to add to cart');
        }
        
    } catch (error) {
        console.error('Error adding to cart:', error);
        showToast('Failed to add to cart', 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-shopping-bag"></i> Add to Cart';
    }
}

async function updateCounts() {
    const token = localStorage.getItem('token');
    if (!token) return;
    
    try {
        // Update cart count
        const cartResponse = await fetch(`${API_BASE_URL}/cart`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (cartResponse.ok) {
            const cart = await cartResponse.json();
            const cartCount = cart.reduce((sum, item) => sum + item.quantity, 0);
            document.getElementById('cartCount').textContent = cartCount;
        }
        
        // Update wishlist count
        const wishlistResponse = await fetch(`${API_BASE_URL}/wishlist`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (wishlistResponse.ok) {
            const wishlist = await wishlistResponse.json();
            document.getElementById('wishlistCount').textContent = wishlist.length;
        }
        
    } catch (error) {
        console.error('Error updating counts:', error);
    }
}
