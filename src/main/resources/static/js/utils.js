/**
 * Utility Functions for TinyToTrend
 * Provides reusable helper functions across the application
 */

/**
 * Show a toast notification
 * @param {string} message - The message to display
 * @param {string} type - Type of toast: 'success', 'error', or 'info'
 */
function showToast(message, type = 'success') {
    // Remove existing toast if any
    const existingToast = document.querySelector('.toast');
    if (existingToast) {
        existingToast.remove();
    }
    
    // Create toast element
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    // Set icon based on type
    let icon = 'check-circle';
    if (type === 'error') icon = 'exclamation-circle';
    if (type === 'info') icon = 'info-circle';
    
    toast.innerHTML = `
        <i class="fas fa-${icon}"></i>
        <span>${message}</span>
    `;
    
    // Add to document
    document.body.appendChild(toast);
    
    // Show toast with animation
    setTimeout(() => toast.classList.add('show'), 100);
    
    // Hide and remove toast after delay
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

/**
 * Format price with Indian Rupee symbol
 * @param {number} price - The price to format
 * @returns {string} Formatted price string
 */
function formatPrice(price) {
    return '₹' + Number(price).toFixed(2);
}

/**
 * Get user data from localStorage
 * @returns {Object|null} User data object or null if not logged in
 */
function getCurrentUser() {
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('userName');
    const userId = localStorage.getItem('userId');
    const userRole = localStorage.getItem('userRole');
    const userEmail = localStorage.getItem('userEmail');
    
    if (!token) return null;
    
    return {
        token,
        name: userName,
        id: userId,
        role: userRole,
        email: userEmail
    };
}

/**
 * Clear all user data from localStorage and redirect to home
 */
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userId');
    localStorage.removeItem('userRole');
    window.location.href = '/';
}

/**
 * Toggle password visibility in input field
 * @param {string} inputId - The ID of the password input element
 */
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const icon = input.parentElement.querySelector('.toggle-password');
    
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
}

/**
 * Update the cart count badge in navigation
 */
async function updateCartCount() {
    const token = localStorage.getItem('token');
    const cartCountElement = document.getElementById('cartCount');
    
    if (!cartCountElement) return;
    
    if (!token) {
        cartCountElement.textContent = '0';
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/cart`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
            const cart = await response.json();
            const totalCount = cart.reduce((sum, item) => sum + item.quantity, 0);
            cartCountElement.textContent = totalCount;
        }
    } catch (error) {
        console.error('Error updating cart count:', error);
        cartCountElement.textContent = '0';
    }
}

/**
 * Update the wishlist count badge in navigation
 */
async function updateWishlistCount() {
    const token = localStorage.getItem('token');
    const wishlistCountElement = document.getElementById('wishlistCount');
    
    if (!wishlistCountElement) return;
    
    if (!token) {
        wishlistCountElement.style.display = 'none';
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/wishlist`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
            const wishlist = await response.json();
            const count = Array.isArray(wishlist) ? wishlist.length : 0;
            wishlistCountElement.textContent = count;
            wishlistCountElement.style.display = (count > 0) ? 'inline-block' : 'none';
        }
    } catch (error) {
        console.error('Error updating wishlist count:', error);
        wishlistCountElement.style.display = 'none';
    }
}

/**
 * Update user name display in navigation
 */
function updateUserDisplay() {
    const userName = localStorage.getItem('userName');
    const userNameElement = document.getElementById('userName');
    const navUserNameElement = document.getElementById('navUserName');
    
    if (userNameElement && userName) {
        userNameElement.textContent = userName;
    }
    if (navUserNameElement && userName) {
        navUserNameElement.textContent = userName;
    }
}

/**
 * Check if user is authenticated and update UI accordingly
 * @returns {Object|null} Current user object or null
 */
function checkAuth() {
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('userName');
    
    if (token && userName) {
        updateUserDisplay();
        return { token, name: userName };
    }
    
    return null;
}

/**
 * Debounce function to limit function calls
 * @param {Function} func - Function to debounce
 * @param {number} wait - Wait time in milliseconds
 * @returns {Function} Debounced function
 */
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

/**
 * Get placeholder image URL for missing product images
 * @param {string} text - Text to display on placeholder
 * @returns {string} Placeholder image URL
 */
function getPlaceholderImage(text = 'No Image') {
    return `https://via.placeholder.com/400x500?text=${encodeURIComponent(text)}`;
}
