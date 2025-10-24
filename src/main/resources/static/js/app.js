// ==================== CONFIGURATION ====================
const API_BASE_URL = 'http://localhost:8080/api';
let currentUser = null;
let cartItems = [];

// ==================== INITIALIZATION ====================
document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

function initApp() {
    // Check if user is logged in
    checkAuth();
    
    // Load products
    loadProducts();
    
    // Setup event listeners
    setupEventListeners();
    
    // Load cart count
    updateCartCount();
}

// ==================== AUTHENTICATION ====================
function checkAuth() {
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('userName');
    const userRole = localStorage.getItem('userRole');
    
    if (token && userName) {
        currentUser = {
            token: token,
            name: userName,
            role: userRole
        };
        
        // Update UI for logged-in user
        document.getElementById('userName').textContent = userName;
        document.getElementById('loginPrompt').style.display = 'none';
        document.getElementById('userInfo').style.display = 'block';
        document.getElementById('dropdownUserName').textContent = userName;
        document.getElementById('logoutBtn').style.display = 'block';
        
        // Load user's cart if logged in
        if (userRole === 'USER') {
            loadCart();
        }
    }
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userName');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    currentUser = null;
    window.location.href = '/';
}

// ==================== EVENT LISTENERS ====================
function setupEventListeners() {
    // Profile dropdown
    const profileBtn = document.getElementById('profileBtn');
    const dropdown = document.getElementById('profileDropdown');
    
    if (profileBtn && dropdown) {
        profileBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            dropdown.classList.toggle('active');
        });
        
        // Close dropdown when clicking outside
        document.addEventListener('click', (e) => {
            if (!dropdown.contains(e.target) && !profileBtn.contains(e.target)) {
                dropdown.classList.remove('active');
            }
        });
    }
    
    // Search functionality
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', debounce((e) => {
            searchProducts(e.target.value);
        }, 500));
    }
    
    // Category filter
    const categoryFilter = document.getElementById('categoryFilter');
    if (categoryFilter) {
        categoryFilter.addEventListener('change', (e) => {
            loadProducts(e.target.value);
        });
    }
    
    // Cart button
    const cartBtn = document.getElementById('cartBtn');
    if (cartBtn) {
        cartBtn.addEventListener('click', () => {
            window.location.href = '/cart.html';
        });
    }
}

// ==================== PRODUCTS ====================
async function loadProducts(category = '', search = '') {
    const productsGrid = document.getElementById('productsGrid');
    
    if (!productsGrid) return;
    
    // Show loading
    productsGrid.innerHTML = `
        <div class="loading">
            <i class="fas fa-spinner fa-spin"></i>
            <p>Loading products...</p>
        </div>
    `;
    
    try {
        let url = `${API_BASE_URL}/products`;
        const params = new URLSearchParams();
        
        if (category) params.append('category', category);
        if (search) params.append('search', search);
        
        if (params.toString()) {
            url += '?' + params.toString();
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
            <div class="error-message">
                <i class="fas fa-exclamation-triangle"></i>
                <p>Failed to load products. Please try again.</p>
                <button class="btn btn-primary" onclick="loadProducts()">Retry</button>
            </div>
        `;
    }
}

function displayProducts(products) {
    const productsGrid = document.getElementById('productsGrid');
    
    if (!productsGrid) return;
    
    if (products.length === 0) {
        productsGrid.innerHTML = `
            <div class="no-products">
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
                <button class="wishlist-btn" onclick="event.stopPropagation(); addToWishlist(${product.id})">
                    <i class="far fa-heart"></i>
                </button>
            </div>
            <div class="product-info">
                <div class="product-brand">${product.category.toUpperCase()}</div>
                <div class="product-name">${product.name}</div>
                <div class="product-price">
                    <span class="price-current">₹${product.price.toFixed(2)}</span>
                </div>
                <div class="product-rating">
                    <span class="stars">★★★★☆</span>
                    <span>(4.2)</span>
                </div>
                <button class="add-to-bag" onclick="event.stopPropagation(); addToCart(${product.id})">
                    <i class="fas fa-shopping-bag"></i> ADD TO BAG
                </button>
            </div>
        </div>
    `).join('');
}

function searchProducts(query) {
    loadProducts('', query);
}

function filterByCategory(category) {
    document.getElementById('categoryFilter').value = category;
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
    // Check if user is logged in
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
        
        const result = await response.json();
        
        // Show success message
        showToast('Added to bag successfully!', 'success');
        
        // Update cart count
        updateCartCount();
        
    } catch (error) {
        console.error('Error adding to cart:', error);
        showToast('Failed to add to bag', 'error');
    }
}

async function loadCart() {
    const token = localStorage.getItem('token');
    
    if (!token) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/cart`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!response.ok) {
            throw new Error('Failed to load cart');
        }
        
        cartItems = await response.json();
        updateCartCount();
        
    } catch (error) {
        console.error('Error loading cart:', error);
    }
}

function updateCartCount() {
    const cartCountElement = document.getElementById('cartCount');
    
    if (!cartCountElement) return;
    
    const token = localStorage.getItem('token');
    
    if (!token) {
        cartCountElement.textContent = '0';
        return;
    }
    
    fetch(`${API_BASE_URL}/cart`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => response.json())
    .then(items => {
        const totalCount = items.reduce((sum, item) => sum + item.quantity, 0);
        cartCountElement.textContent = totalCount;
    })
    .catch(error => {
        console.error('Error updating cart count:', error);
        cartCountElement.textContent = '0';
    });
}

// ==================== WISHLIST ====================
async function addToWishlist(productId) {
    const token = localStorage.getItem('token');
    
    if (!token) {
        alert('Please login to add to wishlist');
        window.location.href = '/login.html';
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/wishlist`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                productId: productId
            })
        });
        
        if (!response.ok) {
            throw new Error('Failed to add to wishlist');
        }
        
        showToast('Added to wishlist!', 'success');
        
    } catch (error) {
        console.error('Error adding to wishlist:', error);
        showToast('Failed to add to wishlist', 'error');
    }
}

function showOrders() {
    window.location.href = '/orders.html';
}

function showWishlist() {
    window.location.href = '/wishlist.html';
}

function showProfile() {
    window.location.href = '/profile.html';
}

// ==================== UTILITY FUNCTIONS ====================
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function showToast(message, type = 'info') {
    // Remove existing toast
    const existingToast = document.querySelector('.toast');
    if (existingToast) {
        existingToast.remove();
    }
    
    // Create toast
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
        <span>${message}</span>
    `;
    
    document.body.appendChild(toast);
    
    // Show toast
    setTimeout(() => toast.classList.add('show'), 100);
    
    // Hide and remove toast
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// ==================== TOAST STYLES (Add to CSS) ====================
const toastStyles = `
.toast {
    position: fixed;
    top: 80px;
    right: 20px;
    background: white;
    padding: 15px 20px;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    display: flex;
    align-items: center;
    gap: 12px;
    transform: translateX(400px);
    transition: transform 0.3s;
    z-index: 10000;
}

.toast.show {
    transform: translateX(0);
}

.toast-success {
    border-left: 4px solid var(--success);
}

.toast-error {
    border-left: 4px solid var(--error);
}

.toast i {
    font-size: 20px;
}

.toast-success i {
    color: var(--success);
}

.toast-error i {
    color: var(--error);
}
`;

// Add toast styles to head
const styleElement = document.createElement('style');
styleElement.textContent = toastStyles;
document.head.appendChild(styleElement);
