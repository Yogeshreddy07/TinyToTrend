/**
 * Profile Page JavaScript for TinyToTrend
 * Handles user profile display and management
 */

let currentUser = null;

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    checkAuthForProfile();
    loadUserData();
    loadOrders();
    loadStats();
});

function checkAuthForProfile() {
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('userName');
    const userEmail = localStorage.getItem('userEmail');
    
    if (!token) {
        window.location.href = '/login.html';
        return;
    }
    
    currentUser = {
        token: token,
        name: userName
    };
    
    // Update UI
    if (userName) {
        document.getElementById('userName').textContent = userName;
        document.getElementById('navUserName').textContent = userName;
        document.getElementById('avatarInitial').textContent = userName.charAt(0).toUpperCase();
    }
}

function loadUserData() {
    const userName = localStorage.getItem('userName');
    const userEmail = localStorage.getItem('userEmail');
    
    if (userName) {
        document.getElementById('profileName').value = userName;
    }
    if (userEmail) {
        document.getElementById('userEmail').textContent = userEmail;
        document.getElementById('profileEmail').value = userEmail;
    }
}

async function loadStats() {
    const token = localStorage.getItem('token');
    
    try {
        // Load cart count
        const cartResponse = await fetch(`${API_BASE_URL}/cart`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (cartResponse.ok) {
            const cart = await cartResponse.json();
            document.getElementById('totalCart').textContent = cart.length;
        }

        // Load orders count
        const ordersResponse = await fetch(`${API_BASE_URL}/orders/user`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (ordersResponse.ok) {
            const orders = await ordersResponse.json();
            document.getElementById('totalOrders').textContent = orders.length;
        }
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

async function loadOrders() {
    const token = localStorage.getItem('token');
    const ordersList = document.getElementById('ordersList');
    const recentOrders = document.getElementById('recentOrders');
    
    try {
        const response = await fetch(`${API_BASE_URL}/orders/user`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (!response.ok) throw new Error('Failed to load orders');
        
        const orders = await response.json();
        
        if (orders.length === 0) {
            ordersList.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-box-open"></i>
                    <h3>No orders yet</h3>
                    <p>Start shopping to see your orders here</p>
                </div>
            `;
            return;
        }
        
        const ordersHTML = orders.map(order => `
            <div class="order-card">
                <div class="order-header">
                    <div>
                        <div class="order-id">Order #${order.id}</div>
                        <div class="order-date">${new Date(order.createdAt).toLocaleDateString()}</div>
                    </div>
                    <span class="order-status status-${order.status.toLowerCase()}">${order.status}</span>
                </div>
                <div class="order-items">
                    ${order.items ? order.items.slice(0, 3).map(item => `
                        <div class="order-item">
                            <img src="${item.product.imageUrl}" alt="${item.product.name}">
                            <div class="order-item-info">
                                <div class="order-item-name">${item.product.name}</div>
                                <div class="order-item-qty">Qty: ${item.quantity}</div>
                            </div>
                            <div>₹${item.price}</div>
                        </div>
                    `).join('') : ''}
                </div>
                <div class="order-total">Total: ₹${order.totalAmount.toFixed(2)}</div>
            </div>
        `).join('');
        
        ordersList.innerHTML = ordersHTML;
        
        // Show recent orders on overview
        const recentHTML = orders.slice(0, 2).map(order => `
            <div class="order-card">
                <div class="order-header">
                    <div>
                        <div class="order-id">Order #${order.id}</div>
                        <div class="order-date">${new Date(order.createdAt).toLocaleDateString()}</div>
                    </div>
                    <span class="order-status status-${order.status.toLowerCase()}">${order.status}</span>
                </div>
                <div class="order-total">Total: ₹${order.totalAmount.toFixed(2)}</div>
            </div>
        `).join('');
        
        recentOrders.innerHTML = recentHTML;
        
    } catch (error) {
        console.error('Error loading orders:', error);
        ordersList.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-exclamation-triangle"></i>
                <h3>Failed to load orders</h3>
                <p>Please try again later</p>
            </div>
        `;
    }
}

function showSection(sectionId) {
    // Hide all sections
    document.querySelectorAll('.profile-section').forEach(section => {
        section.classList.remove('active');
    });
    
    // Show selected section
    document.getElementById(sectionId).classList.add('active');
    
    // Update active menu item
    document.querySelectorAll('.sidebar-menu a').forEach(link => {
        link.classList.remove('active');
    });
    event.target.closest('a').classList.add('active');
    
    // Load orders if orders section is shown
    if (sectionId === 'orders') {
        loadOrders();
    }
}

function updateProfile(event) {
    event.preventDefault();
    
    const successDiv = document.getElementById('updateSuccess');
    const errorDiv = document.getElementById('updateError');
    const name = document.getElementById('profileName').value;
    
    // Update localStorage
    localStorage.setItem('userName', name);
    
    // Update UI
    document.getElementById('userName').textContent = name;
    document.getElementById('navUserName').textContent = name;
    document.getElementById('avatarInitial').textContent = name.charAt(0).toUpperCase();
    
    successDiv.style.display = 'block';
    errorDiv.style.display = 'none';
    
    setTimeout(() => {
        successDiv.style.display = 'none';
    }, 3000);
}

function changePassword(event) {
    event.preventDefault();
    
    const successDiv = document.getElementById('passwordSuccess');
    const errorDiv = document.getElementById('passwordError');
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';
    
    if (newPassword !== confirmPassword) {
        errorDiv.textContent = 'Passwords do not match';
        errorDiv.style.display = 'block';
        return;
    }
    
    // In a real app, you would call an API here
    successDiv.style.display = 'block';
    document.getElementById('passwordForm').reset();
    
    setTimeout(() => {
        successDiv.style.display = 'none';
    }, 3000);
}
