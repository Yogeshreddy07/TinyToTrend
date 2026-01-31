/**
 * Login Page JavaScript for TinyToTrend
 * Handles user and admin login
 */

// Tab Switching
function switchTab(tab) {
    const userForm = document.getElementById('userLoginForm');
    const adminForm = document.getElementById('adminLoginForm');
    const tabs = document.querySelectorAll('.auth-tab');
    
    tabs.forEach(t => t.classList.remove('active'));
    
    if (tab === 'user') {
        userForm.style.display = 'block';
        adminForm.style.display = 'none';
        tabs[0].classList.add('active');
    } else {
        userForm.style.display = 'none';
        adminForm.style.display = 'block';
        tabs[1].classList.add('active');
    }
}

// User Login
async function handleLogin(event) {
    event.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const errorDiv = document.getElementById('loginError');
    const btn = document.getElementById('loginBtn');
    
    errorDiv.style.display = 'none';
    btn.disabled = true;
    btn.textContent = 'LOGGING IN...';
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.error || 'Login failed');
        }
        
        // Save user data
        localStorage.setItem('token', data.token);
        localStorage.setItem('userId', data.userId);
        localStorage.setItem('userName', data.name);
        localStorage.setItem('userRole', data.role);
        
        // Redirect based on role
        if (data.role === 'ADMIN') {
            window.location.href = '/admin/dashboard.html';
        } else {
            window.location.href = '/';
        }
        
    } catch (error) {
        console.error('Login error:', error);
        errorDiv.textContent = error.message || 'Invalid email or password';
        errorDiv.style.display = 'block';
        btn.disabled = false;
        btn.textContent = 'LOGIN';
    }
}

// Admin Login
async function handleAdminLogin(event) {
    event.preventDefault();
    
    const email = document.getElementById('adminEmail').value;
    const password = document.getElementById('adminPassword').value;
    const errorDiv = document.getElementById('adminLoginError');
    const btn = document.getElementById('adminLoginBtn');
    
    errorDiv.style.display = 'none';
    btn.disabled = true;
    btn.textContent = 'LOGGING IN...';
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.error || 'Login failed');
        }
        
        if (data.role !== 'ADMIN') {
            throw new Error('Access denied. Admin credentials required.');
        }
        
        localStorage.setItem('token', data.token);
        localStorage.setItem('userId', data.userId);
        localStorage.setItem('userName', data.name);
        localStorage.setItem('userRole', data.role);
        
        window.location.href = '/admin/dashboard.html';
        
    } catch (error) {
        console.error('Admin login error:', error);
        errorDiv.textContent = error.message || 'Invalid admin credentials';
        errorDiv.style.display = 'block';
        btn.disabled = false;
        btn.textContent = 'ADMIN LOGIN';
    }
}

// Check if already logged in
document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('userRole');
    
    if (token) {
        if (role === 'ADMIN') {
            window.location.href = '/admin/dashboard.html';
        } else {
            window.location.href = '/';
        }
    }
});
