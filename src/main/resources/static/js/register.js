/**
 * Register Page JavaScript for TinyToTrend
 * Handles user registration
 */

async function handleRegister(event) {
    event.preventDefault();
    
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const terms = document.getElementById('terms').checked;
    
    const errorDiv = document.getElementById('registerError');
    const successDiv = document.getElementById('registerSuccess');
    const btn = document.getElementById('registerBtn');
    
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
    
    btn.disabled = true;
    btn.textContent = 'CREATING ACCOUNT...';
    
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
        
        successDiv.textContent = 'Registration successful! Redirecting to login...';
        successDiv.style.display = 'block';
        
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 2000);
        
    } catch (error) {
        console.error('Registration error:', error);
        errorDiv.textContent = error.message || 'Registration failed. Please try again.';
        errorDiv.style.display = 'block';
        btn.disabled = false;
        btn.textContent = 'CREATE ACCOUNT';
    }
}

// Redirect if already logged in
document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (token) {
        window.location.href = '/';
    }
});
