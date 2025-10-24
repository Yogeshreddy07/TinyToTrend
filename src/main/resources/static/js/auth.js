// ==================== CONFIGURATION ====================
const API_BASE_URL = 'http://localhost:8080/api';

// ==================== TAB SWITCHING ====================
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

// ==================== PASSWORD TOGGLE ====================
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

// ==================== USER LOGIN ====================
async function handleLogin(event) {
    event.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const errorDiv = document.getElementById('loginError');
    const btn = document.getElementById('loginBtn');
    const btnText = btn.querySelector('.btn-text');
    const btnLoader = btn.querySelector('.btn-loader');
    
    // Hide previous errors
    errorDiv.style.display = 'none';
    
    // Show loading
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline-block';
    btn.disabled = true;
    
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
            // Redirect to previous page or home
            const returnUrl = new URLSearchParams(window.location.search).get('return') || '/';
            window.location.href = returnUrl;
        }
        
    } catch (error) {
        console.error('Login error:', error);
        errorDiv.textContent = error.message || 'Invalid email or password';
        errorDiv.style.display = 'block';
        
        // Reset button
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
        btn.disabled = false;
    }
}

// ==================== ADMIN LOGIN ====================
async function handleAdminLogin(event) {
    event.preventDefault();
    
    const email = document.getElementById('adminEmail').value;
    const password = document.getElementById('adminPassword').value;
    const errorDiv = document.getElementById('adminLoginError');
    const btn = document.getElementById('adminLoginBtn');
    const btnText = btn.querySelector('.btn-text');
    const btnLoader = btn.querySelector('.btn-loader');
    
    // Hide previous errors
    errorDiv.style.display = 'none';
    
    // Show loading
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline-block';
    btn.disabled = true;
    
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
        
        // Check if user is actually an admin
        if (data.role !== 'ADMIN') {
            throw new Error('Access denied. Admin credentials required.');
        }
        
        // Save admin data
        localStorage.setItem('token', data.token);
        localStorage.setItem('userId', data.userId);
        localStorage.setItem('userName', data.name);
        localStorage.setItem('userRole', data.role);
        
        // Redirect to admin dashboard
        window.location.href = '/admin/dashboard.html';
        
    } catch (error) {
        console.error('Admin login error:', error);
        errorDiv.textContent = error.message || 'Invalid admin credentials';
        errorDiv.style.display = 'block';
        
        // Reset button
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
        btn.disabled = false;
    }
}

// ==================== USER REGISTRATION ====================
async function handleRegister(event) {
    event.preventDefault();
    
    const name = document.getElementById('name').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const terms = document.getElementById('terms').checked;
    
    const errorDiv = document.getElementById('registerError');
    const successDiv = document.getElementById('registerSuccess');
    const btn = document.getElementById('registerBtn');
    const btnText = btn.querySelector('.btn-text');
    const btnLoader = btn.querySelector('.btn-loader');
    
    // Hide previous messages
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';
    
    // Validation
    if (password !== confirmPassword) {
        errorDiv.textContent = 'Passwords do not match';
        errorDiv.style.display = 'block';
        return;
    }
    
    if (!terms) {
        errorDiv.textContent = 'Please accept the terms and conditions';
        errorDiv.style.display = 'block';
        return;
    }
    
    // Show loading
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline-block';
    btn.disabled = true;
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: name,
                email: email,
                password: password
            })
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.error || 'Registration failed');
        }
        
        // Show success message
        successDiv.textContent = 'Registration successful! Redirecting to login...';
        successDiv.style.display = 'block';
        
        // Redirect to login after 2 seconds
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 2000);
        
    } catch (error) {
        console.error('Registration error:', error);
        errorDiv.textContent = error.message || 'Registration failed. Please try again.';
        errorDiv.style.display = 'block';
        
        // Reset button
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
        btn.disabled = false;
    }
}

// ==================== CHECK IF ALREADY LOGGED IN ====================
document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('userRole');
    
    // If already logged in, redirect appropriately
    if (token && window.location.pathname.includes('login.html')) {
        if (role === 'ADMIN') {
            window.location.href = '/admin/dashboard.html';
        } else {
            window.location.href = '/';
        }
    }
});
