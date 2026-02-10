/**
 * Admin Dashboard JavaScript for TinyToTrend
 * Handles admin panel functionality
 */

let ADMIN_TOKEN = localStorage.getItem('token');
let ADMIN_ROLE = localStorage.getItem('userRole');

// Check admin authentication
if (!ADMIN_TOKEN || ADMIN_ROLE !== "ADMIN") {
    window.location.href = "/login.html?return=/admin/dashboard.html";
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    fetchAdminStats();
});

function logout() {
    localStorage.clear(); 
    window.location.href = "/login.html";
}

// Sidebar navigation
function showSection(section, el) {
    ['dashboard','products','orders','users','analytics','settings'].forEach(id=>{
        document.getElementById(id+'Section').style.display = (id === section) ? '' : 'none';
    });
    document.querySelectorAll('.sidebar-links a').forEach(link=>link.classList.remove('active'));
    if(el) el.classList.add('active');
    // Refresh data when switching sections
    if(section==='dashboard') fetchAdminStats();
    if(section==='products') listProducts();
    if(section==='orders') listOrders();
    if(section==='users') listUsers();
    if(section==='analytics') fetchAnalytics();
}

// --- DASHBOARD DATA ---
async function fetchAdminStats() {
    // initialize defaults so UI doesn't show placeholders for long
    try {
        document.getElementById('statProducts').innerText = '0';
        document.getElementById('statUsers').innerText = '0';
        document.getElementById('statOrders').innerText = '0';
        document.getElementById('statSales').innerText = '₹0.00';
    } catch (e) {
        // ignore if elements missing
    }

    try {
        let [productsRes, usersRes, ordersRes] = await Promise.all([
            fetch(API_BASE_URL+'/admin/products',{headers:{'Authorization':'Bearer '+ADMIN_TOKEN}}),
            fetch(API_BASE_URL+'/admin/users',{headers:{'Authorization':'Bearer '+ADMIN_TOKEN}}),
            fetch(API_BASE_URL+'/admin/orders',{headers:{'Authorization':'Bearer '+ADMIN_TOKEN}})
        ]);
        if (!productsRes.ok || !usersRes.ok || !ordersRes.ok) {
            // fallback to aggregated stats endpoint
            const statsRes = await fetch(API_BASE_URL + '/admin/stats', { headers: { 'Authorization': 'Bearer ' + ADMIN_TOKEN } });
            if (statsRes.ok) {
                const s = await statsRes.json();
                document.getElementById('statProducts').innerText = s.totalProducts || 0;
                document.getElementById('statUsers').innerText = s.totalUsers || 0;
                document.getElementById('statOrders').innerText = s.totalOrders || 0;
                document.getElementById('statSales').innerText = '₹' + (s.totalRevenue ? parseFloat(s.totalRevenue).toFixed(2) : '0.00');
                return;
            }
        }

        let products = await productsRes.json();
        let users = await usersRes.json();
        let orders = await ordersRes.json();
        let sales = orders.reduce((t,o)=>t+parseFloat(o.totalAmount || 0),0);
        document.getElementById('statProducts').innerText = products.length;
        document.getElementById('statUsers').innerText = users.length;
        document.getElementById('statOrders').innerText = orders.length;
        document.getElementById('statSales').innerText = "₹"+sales.toFixed(2);
    } catch (error) {
        console.error('Error fetching admin stats:', error);
        // final fallback to aggregated endpoint
        try {
            const statsRes = await fetch(API_BASE_URL + '/admin/stats', { headers: { 'Authorization': 'Bearer ' + ADMIN_TOKEN } });
            if (statsRes.ok) {
                const s = await statsRes.json();
                document.getElementById('statProducts').innerText = s.totalProducts || 0;
                document.getElementById('statUsers').innerText = s.totalUsers || 0;
                document.getElementById('statOrders').innerText = s.totalOrders || 0;
                document.getElementById('statSales').innerText = '₹' + (s.totalRevenue ? parseFloat(s.totalRevenue).toFixed(2) : '0.00');
            }
        } catch (e) {
            console.error('Fallback stats failed:', e);
        }
    }
}

// --- PRODUCTS ---
async function listProducts() {
    let tb = document.getElementById("productsTable").querySelector('tbody');
    tb.innerHTML = `<tr><td colspan="7">Loading...</td></tr>`;
    let res = await fetch(API_BASE_URL+'/admin/products',{headers:{'Authorization':'Bearer '+ADMIN_TOKEN}});
    let products = await res.json();
    tb.innerHTML = products.map(prod => `
        <tr>
            <td>${prod.id}</td>
            <td>${prod.name}</td>
            <td>${prod.category}</td>
            <td>₹${prod.price}</td>
            <td>${prod.stockQty}</td>
            <td>${prod.genderTag}</td>
            <td>
                <button class="action-btn" onclick="editProduct(${prod.id})">Edit</button>
                <button class="action-btn delete" onclick="deleteProduct(${prod.id})">Delete</button>
            </td>
        </tr>
    `).join('');
}

async function deleteProduct(productId) {
    if (!confirm("Delete this product?")) return;
    let res = await fetch(API_BASE_URL+'/admin/products/'+productId, {
        method:'DELETE',
        headers:{'Authorization':'Bearer '+ADMIN_TOKEN}
    });
    if(res.ok) {
        listProducts();
        fetchAdminStats();
    }
}

function openProductModal(edit=false) {
    document.getElementById('productModalBg').style.display = 'flex';
    if(!edit){
        document.getElementById('productForm').reset();
        document.getElementById('modalTitle').innerText = "Add Product";
        document.getElementById('productId').value = "";
    }
}

function closeProductModal() {
    document.getElementById('productModalBg').style.display = 'none';
}

async function editProduct(id) {
    openProductModal(true);
    document.getElementById('modalTitle').innerText = "Edit Product";
    let res = await fetch(API_BASE_URL+'/admin/products/'+id,{headers:{'Authorization':'Bearer '+ADMIN_TOKEN}});
    let prod = await res.json();
    document.getElementById('productId').value = prod.id;
    document.getElementById('prodName').value = prod.name;
    document.getElementById('prodDesc').value = prod.description;
    document.getElementById('prodCategory').value = prod.category;
    document.getElementById('prodPrice').value = prod.price;
    document.getElementById('prodQty').value = prod.stockQty;
    document.getElementById('prodGenderTag').value = prod.genderTag;
    document.getElementById('prodImage').value = prod.imageUrl||"";
}

async function submitProduct(event) {
    event.preventDefault();
    let id = document.getElementById('productId').value;
    let body = {
        name: document.getElementById('prodName').value,
        description: document.getElementById('prodDesc').value,
        category: document.getElementById('prodCategory').value,
        price: parseFloat(document.getElementById('prodPrice').value),
        stockQty: parseInt(document.getElementById('prodQty').value),
        genderTag: document.getElementById('prodGenderTag').value,
        imageUrl: document.getElementById('prodImage').value
    };
    let url = API_BASE_URL+'/admin/products' + (id ? '/'+id : '');
    let method = id ? "PUT" : "POST";
    let res = await fetch(url, {
        method: method,
        headers: { 'Authorization':'Bearer '+ADMIN_TOKEN, 'Content-Type':'application/json' },
        body: JSON.stringify(body)
    });
    if (res.ok) {
        closeProductModal();
        listProducts();
        fetchAdminStats();
    }
}

// --- ORDERS ---
async function listOrders() {
    let tb = document.getElementById("ordersTable").querySelector('tbody');
    tb.innerHTML = `<tr><td colspan="7">Loading...</td></tr>`;
    let res = await fetch(API_BASE_URL+'/admin/orders',{headers:{'Authorization':'Bearer '+ADMIN_TOKEN}});
    let orders = await res.json();
    tb.innerHTML = orders.map(order => {
        // Determine payment badge class
        let paymentStatus = order.paymentStatus || "PENDING";
        let badgeClass = "pending";
        if (paymentStatus === "PAID") badgeClass = "paid";
        else if (paymentStatus === "FAILED") badgeClass = "failed";
        
        return `
            <tr>
                <td>${order.id}</td>
                <td>${order.user ? order.user.email : "--"}</td>
                <td>₹${order.totalAmount || 0}</td>
                <td>${order.status}</td>
                <td><span class="payment-badge ${badgeClass}">${paymentStatus}</span></td>
                <td>${order.paymentId || "--"}</td>
                <td>${order.createdAt ? new Date(order.createdAt).toLocaleDateString() : ""}</td>
            </tr>
        `;
    }).join('');
}

// --- ANALYTICS ---
async function fetchAnalytics() {
    const container = document.getElementById('analyticsContent');
    if (!container) return;
    container.innerHTML = 'Loading analytics...';

    try {
        const res = await fetch(API_BASE_URL + '/admin/stats', { headers: { 'Authorization': 'Bearer ' + ADMIN_TOKEN } });
        if (!res.ok) throw new Error('Failed to fetch analytics');
        const s = await res.json();
        container.innerHTML = `
            <div style="display:flex;gap:20px;flex-wrap:wrap">
                <div style="min-width:180px;background:#fff;padding:18px;border-radius:8px;box-shadow:0 2px 8px #0001">
                    <div style="font-size:14px;color:#888">Total Orders</div>
                    <div style="font-size:20px;font-weight:700">${s.totalOrders || 0}</div>
                </div>
                <div style="min-width:180px;background:#fff;padding:18px;border-radius:8px;box-shadow:0 2px 8px #0001">
                    <div style="font-size:14px;color:#888">Total Revenue</div>
                    <div style="font-size:20px;font-weight:700">₹${s.totalRevenue ? parseFloat(s.totalRevenue).toFixed(2) : '0.00'}</div>
                </div>
                <div style="min-width:180px;background:#fff;padding:18px;border-radius:8px;box-shadow:0 2px 8px #0001">
                    <div style="font-size:14px;color:#888">Products</div>
                    <div style="font-size:20px;font-weight:700">${s.totalProducts || 0}</div>
                </div>
                <div style="min-width:180px;background:#fff;padding:18px;border-radius:8px;box-shadow:0 2px 8px #0001">
                    <div style="font-size:14px;color:#888">Users</div>
                    <div style="font-size:20px;font-weight:700">${s.totalUsers || 0}</div>
                </div>
            </div>
            <p style="margin-top:18px;color:#666">(Simple analytics summary — more charts can be added later.)</p>
        `;
    } catch (err) {
        console.error('Error loading analytics', err);
        container.innerHTML = '<div class="section-desc">Failed to load analytics.</div>';
    }
}

// --- USERS ---
async function listUsers() {
    let tb = document.getElementById("usersTable").querySelector('tbody');
    tb.innerHTML = `<tr><td colspan="5">Loading...</td></tr>`;
    let res = await fetch(API_BASE_URL+'/admin/users',{headers:{'Authorization':'Bearer '+ADMIN_TOKEN}});
    let users = await res.json();
    tb.innerHTML = users.map(user => `
        <tr>
            <td>${user.id}</td>
            <td>${user.name}</td>
            <td>${user.email}</td>
            <td>${user.role}</td>
            <td>${user.createdAt ? new Date(user.createdAt).toLocaleDateString() : ""}</td>
        </tr>
    `).join('');
}
