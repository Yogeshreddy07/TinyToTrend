/**
 * API Utility Module for TinyToTrend
 * Provides reusable fetch wrappers for all API calls
 */

const API_BASE_URL = 'http://localhost:8080/api';

/**
 * Get the authentication token from localStorage
 * @returns {string|null} The JWT token or null if not logged in
 */
function getToken() {
    return localStorage.getItem('token');
}

/**
 * Get authorization headers for authenticated requests
 * @returns {Object} Headers object with Authorization if token exists
 */
function getAuthHeaders() {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json'
    };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

/**
 * Make a GET request to the API
 * @param {string} endpoint - API endpoint (without base URL)
 * @param {boolean} requiresAuth - Whether the request requires authentication
 * @returns {Promise<any>} Response data
 */
async function apiGet(endpoint, requiresAuth = false) {
    const headers = requiresAuth ? getAuthHeaders() : { 'Content-Type': 'application/json' };
    
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'GET',
        headers: headers
    });
    
    if (!response.ok) {
        const error = await response.json().catch(() => ({ error: 'Request failed' }));
        throw new Error(error.error || 'Request failed');
    }
    
    return response.json();
}

/**
 * Make a POST request to the API
 * @param {string} endpoint - API endpoint (without base URL)
 * @param {Object} data - Request body data
 * @param {boolean} requiresAuth - Whether the request requires authentication
 * @returns {Promise<any>} Response data
 */
async function apiPost(endpoint, data, requiresAuth = false) {
    const headers = requiresAuth ? getAuthHeaders() : { 'Content-Type': 'application/json' };
    
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(data)
    });
    
    if (!response.ok) {
        const error = await response.json().catch(() => ({ error: 'Request failed' }));
        throw new Error(error.error || 'Request failed');
    }
    
    return response.json();
}

/**
 * Make a PUT request to the API
 * @param {string} endpoint - API endpoint (without base URL)
 * @param {Object} data - Request body data
 * @param {boolean} requiresAuth - Whether the request requires authentication
 * @returns {Promise<any>} Response data
 */
async function apiPut(endpoint, data, requiresAuth = true) {
    const headers = getAuthHeaders();
    
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'PUT',
        headers: headers,
        body: JSON.stringify(data)
    });
    
    if (!response.ok) {
        const error = await response.json().catch(() => ({ error: 'Request failed' }));
        throw new Error(error.error || 'Request failed');
    }
    
    return response.json();
}

/**
 * Make a DELETE request to the API
 * @param {string} endpoint - API endpoint (without base URL)
 * @param {boolean} requiresAuth - Whether the request requires authentication
 * @returns {Promise<Response>} Response object
 */
async function apiDelete(endpoint, requiresAuth = true) {
    const headers = getAuthHeaders();
    
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'DELETE',
        headers: headers
    });
    
    if (!response.ok) {
        const error = await response.json().catch(() => ({ error: 'Request failed' }));
        throw new Error(error.error || 'Request failed');
    }
    
    return response;
}

/**
 * Check if user is authenticated
 * @returns {boolean} True if token exists
 */
function isAuthenticated() {
    return !!getToken();
}

/**
 * Redirect to login if not authenticated
 * @param {string} returnUrl - URL to return to after login
 */
function requireAuth(returnUrl = window.location.pathname) {
    if (!isAuthenticated()) {
        window.location.href = `/login.html?return=${encodeURIComponent(returnUrl)}`;
        return false;
    }
    return true;
}
