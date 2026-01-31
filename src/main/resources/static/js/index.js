/**
 * Index/Home Page JavaScript for TinyToTrend
 * Handles product display and main page functionality
 */

let currentUser = null;

// ==================== INITIALIZATION ====================
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    loadProducts();
    setupEventListeners();
    updateCartCount();
    updateWishlistCount();
});

// ==================== AUTHENTICATION ====================
function checkAuth() {
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('userName');
    
    if (token && userName) {
        currentUser = {
            token: token,
            name: userName
        };
        document.getElementById('userName').textContent = userName;
    }
}

function setupEventListeners() {
    const profileBtn = document.getElementById('profileBtn');
    if (profileBtn) {
        profileBtn.addEventListener('click', () => {
            if (currentUser) {
                window.location.href = '/profile.html';
            } else {
                window.location.href = '/login.html';
            }
        });
    }
    
    const cartBtn = document.getElementById('cartBtn');
    if (cartBtn) {
        cartBtn.addEventListener('click', () => {
            window.location.href = '/cart.html';
        });
    }
}

// ==================== PRODUCTS ====================
async function loadProducts(category = '') {
    const productsGrid = document.getElementById('productsGrid');
    
    productsGrid.innerHTML = `
        <div class="loading">
            <i class="fas fa-spinner fa-spin"></i>
            <p>Loading products...</p>
        </div>
    `;
    
    try {
        let url = `${API_BASE_URL}/products`;
        if (category) {
            url += `?category=${category}`;
        }
        
        const response = await fetch(url);
        
        if (!response.ok) {
            throw new Error('Failed to load products');
        }
        
        const products = await response.json();
        displayProducts(products);
        
    } catch (error) {
        console.error('Error loading products:', error);
        productsGrid.innerHTML = `
            <div class="loading">
                <i class="fas fa-exclamation-triangle"></i>
                <p>Failed to load products. Please try again.</p>
            </div>
        `;
    }
}

function displayProducts(products) {
    const productsGrid = document.getElementById('productsGrid');
    
    if (products.length === 0) {
        productsGrid.innerHTML = `
            <div class="loading">
                <i class="fas fa-shopping-bag"></i>
                <p>No products found</p>
            </div>
        `;
        return;
    }
    
    productsGrid.innerHTML = products.map(product => `
        <div class="product-card" onclick="viewProduct(${product.id})">
            <div class="product-image">
                <img src="${product.imageUrl || 'https://via.placeholder.com/400x500?text=No+Image'}" 
                     alt="${product.name}"
                     onerror="this.src='https://via.placeholder.com/400x500?text=No+Image'">
            </div>
            <div class="product-info">
                <div class="product-brand">${product.category.toUpperCase()}</div>
                <div class="product-name">${product.name}</div>
                <div class="product-price">
                    <span class="price-current">₹${product.price.toFixed(2)}</span>
                </div>
                <button class="add-to-bag" onclick="event.stopPropagation(); addToCart(${product.id})">
                    <i class="fas fa-shopping-bag"></i> ADD TO BAG
                </button>
            </div>
        </div>
    `).join('');
}

function filterByCategory(category) {
    loadProducts(category);
    scrollToProducts();
}

function scrollToProducts() {
    document.getElementById('products').scrollIntoView({ behavior: 'smooth' });
}

function viewProduct(productId) {
    window.location.href = `/product-detail.html?id=${productId}`;
}

// ==================== CART ====================
async function addToCart(productId) {
    const token = localStorage.getItem('token');
    
    if (!token) {
        alert('Please login to add items to cart');
        window.location.href = '/login.html';
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/cart`, {
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
        
        if (!response.ok) {
            throw new Error('Failed to add to cart');
        }
        
        showToast('Added to bag successfully!', 'success');
        updateCartCount();
        
    } catch (error) {
        console.error('Error adding to cart:', error);
        showToast('Failed to add to bag', 'error');
    }
}
