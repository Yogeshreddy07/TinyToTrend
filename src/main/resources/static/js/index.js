/**
 * Index/Home Page JavaScript for TinyToTrend
 * Handles product display and main page functionality
 */

let currentUser = null;
let searchTimeout = null; // For debouncing search input

// ==================== PAGINATION STATE ====================
let currentPage = 0;
const pageSize = 12;
let isLastPage = false;
let isLoading = false;

// ==================== INITIALIZATION ====================
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    loadProductsWithPagination();
    setupEventListeners();
    setupFilterListeners();
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

// ==================== FILTER & SEARCH LISTENERS ====================
function setupFilterListeners() {
    // Debounced search input
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            // Clear previous timeout
            if (searchTimeout) {
                clearTimeout(searchTimeout);
            }
            // Debounce: wait 300ms after user stops typing
            searchTimeout = setTimeout(() => {
                resetPaginationAndLoad();
            }, 300);
        });
        
        // Also trigger on Enter key
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                if (searchTimeout) {
                    clearTimeout(searchTimeout);
                }
                resetPaginationAndLoad();
            }
        });
    }
    
    // Category filter
    const categoryFilter = document.getElementById('categoryFilter');
    if (categoryFilter) {
        categoryFilter.addEventListener('change', () => {
            resetPaginationAndLoad();
        });
    }
    
    // Sort filter
    const sortFilter = document.getElementById('sortFilter');
    if (sortFilter) {
        sortFilter.addEventListener('change', () => {
            resetPaginationAndLoad();
        });
    }
}

// ==================== CLEAR FILTERS ====================
function clearFilters() {
    const searchInput = document.getElementById('searchInput');
    const categoryFilter = document.getElementById('categoryFilter');
    const sortFilter = document.getElementById('sortFilter');
    
    if (searchInput) searchInput.value = '';
    if (categoryFilter) categoryFilter.value = '';
    if (sortFilter) sortFilter.value = '';
    
    // Reset pagination and reload
    resetPaginationAndLoad();
}

/**
 * Reset pagination state and load first page of products.
 * Called when filters change or are cleared.
 */
function resetPaginationAndLoad() {
    currentPage = 0;
    isLastPage = false;
    loadProductsWithPagination(true); // true = replace existing products
}

// ==================== PRODUCTS ====================
/**
 * Load products with pagination support.
 * @param {boolean} replace - If true, replace existing products; if false, append
 */
async function loadProductsWithPagination(replace = true) {
    if (isLoading) return;
    
    const productsGrid = document.getElementById('productsGrid');
    const loadMoreBtn = document.getElementById('loadMoreBtn');
    const loadMoreContainer = document.getElementById('loadMoreContainer');
    
    // Show loading state
    if (replace) {
        productsGrid.innerHTML = `
            <div class="loading">
                <i class="fas fa-spinner fa-spin"></i>
                <p>Loading products...</p>
            </div>
        `;
    } else if (loadMoreBtn) {
        loadMoreBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Loading...';
        loadMoreBtn.classList.add('loading');
    }
    
    isLoading = true;
    
    try {
        // Build URL with filters and pagination
        const url = buildProductsUrl();
        const response = await fetch(url);
        
        if (!response.ok) {
            throw new Error('Failed to load products');
        }
        
        const data = await response.json();
        
        // Handle paginated response
        const products = data.content;
        isLastPage = data.last;
        
        if (replace) {
            displayProducts(products);
        } else {
            appendProducts(products);
        }
        
        // Update Load More button visibility
        updateLoadMoreButton();
        
    } catch (error) {
        console.error('Error loading products:', error);
        if (replace) {
            productsGrid.innerHTML = `
                <div class="loading">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>Failed to load products. Please try again.</p>
                </div>
            `;
        }
    } finally {
        isLoading = false;
        if (loadMoreBtn) {
            loadMoreBtn.innerHTML = '<i class="fas fa-plus"></i> Load More Products';
            loadMoreBtn.classList.remove('loading');
        }
    }
}

/**
 * Build the products API URL with current filters and pagination.
 */
function buildProductsUrl() {
    const searchInput = document.getElementById('searchInput');
    const categoryFilter = document.getElementById('categoryFilter');
    const sortFilter = document.getElementById('sortFilter');
    
    const search = searchInput ? searchInput.value.trim() : '';
    const category = categoryFilter ? categoryFilter.value : '';
    const sort = sortFilter ? sortFilter.value : '';
    
    // Build query parameters
    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (category) params.append('category', category);
    if (sort) params.append('sort', sort);
    
    // Always add pagination params
    params.append('page', currentPage);
    params.append('size', pageSize);
    
    return `${API_BASE_URL}/products?${params.toString()}`;
}

/**
 * Load more products (called by Load More button).
 */
function loadMoreProducts() {
    if (isLastPage || isLoading) return;
    
    currentPage++;
    loadProductsWithPagination(false); // false = append products
}

/**
 * Update the visibility of the Load More button.
 */
function updateLoadMoreButton() {
    const loadMoreContainer = document.getElementById('loadMoreContainer');
    const loadMoreBtn = document.getElementById('loadMoreBtn');
    
    if (loadMoreContainer) {
        if (isLastPage) {
            loadMoreContainer.style.display = 'none';
        } else {
            loadMoreContainer.style.display = 'flex';
        }
    }
    
    if (loadMoreBtn) {
        loadMoreBtn.disabled = isLastPage;
    }
}

/**
 * Load products with current filter values from the UI (legacy support).
 * Builds query parameters dynamically based on active filters.
 */
async function loadProductsWithFilters() {
    resetPaginationAndLoad();
}

/**
 * Load products with an optional category filter.
 * Maintains backward compatibility with existing filterByCategory function.
 */
async function loadProducts(category = '') {
    // Update the category dropdown if specified
    if (category) {
        const categoryFilter = document.getElementById('categoryFilter');
        if (categoryFilter) {
            categoryFilter.value = category;
        }
    }
    
    // Reset pagination and load
    currentPage = 0;
    isLastPage = false;
    await loadProductsWithPagination(true);
}

/**
 * Fetch products from the API and display them (legacy - kept for backward compatibility).
 * Reusable function for both filtered and unfiltered requests.
 */
async function fetchAndDisplayProducts(url) {
    // Redirect to paginated version
    resetPaginationAndLoad();
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
    
    productsGrid.innerHTML = generateProductCards(products);
}

/**
 * Append products to the existing grid (for Load More).
 * @param {Array} products - Array of product objects to append
 */
function appendProducts(products) {
    const productsGrid = document.getElementById('productsGrid');
    
    if (products.length === 0) {
        return;
    }
    
    // Create a temporary container and append new cards
    const newCardsHtml = generateProductCards(products);
    productsGrid.insertAdjacentHTML('beforeend', newCardsHtml);
}

/**
 * Generate HTML for product cards.
 * @param {Array} products - Array of product objects
 * @returns {string} HTML string for product cards
 */
function generateProductCards(products) {
    return products.map(product => `
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
