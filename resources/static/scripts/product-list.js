let currentPage = 1;
const pageSize = 12;
let products = [];

// 获取商品列表
async function fetchProducts() {
    try {
        const response = await fetch(`/api/products/search?page=${currentPage - 1}&size=${pageSize}&keyword=${getSearchKeyword()}&category=${getSelectedCategory()}&sortBy=${getSortBy()}`);
        const data = await response.json();
        products = data.content;
        renderProducts();
        renderPagination(data.totalPages);
    } catch (error) {
        console.error('Error fetching products:', error);
    }
}

// 渲染商品列表
function renderProducts() {
    const productGrid = document.getElementById('productGrid');
    productGrid.innerHTML = products.map(product => `
        <div class="product-card">
            <img src="${product.imageUrl}" alt="${product.name}">
            <h3>${product.name}</h3>
            <p>${product.description}</p>
            <div class="price">$${product.price}</div>
            <button onclick="addToCart(${product.id})">Add to Cart</button>
        </div>
    `).join('');
}

// 渲染分页
function renderPagination(totalPages) {
    const pagination = document.getElementById('pagination');
    let buttons = '';
    
    if (currentPage > 1) {
        buttons += `<button onclick="changePage(${currentPage - 1})">Previous</button>`;
    }
    
    for (let i = 1; i <= totalPages; i++) {
        buttons += `<button class="${currentPage === i ? 'active' : ''}" onclick="changePage(${i})">${i}</button>`;
    }
    
    if (currentPage < totalPages) {
        buttons += `<button onclick="changePage(${currentPage + 1})">Next</button>`;
    }
    
    pagination.innerHTML = buttons;
}

// 切换页面
function changePage(page) {
    currentPage = page;
    fetchProducts();
}

// 搜索商品
function searchProducts() {
    currentPage = 1;
    fetchProducts();
}

// 获取搜索关键词
function getSearchKeyword() {
    return document.getElementById('searchInput').value;
}

// 获取选中的分类
function getSelectedCategory() {
    return document.getElementById('categoryFilter').value;
}

// 获取排序方式
function getSortBy() {
    return document.getElementById('sortBy').value;
}

// 添加到购物车
async function addToCart(productId) {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = '/login.html';
            return;
        }

        const response = await fetch('/api/cart/items', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                productId,
                quantity: 1
            })
        });

        if (response.ok) {
            updateCartCount();
            alert('Product added to cart!');
        }
    } catch (error) {
        console.error('Error adding to cart:', error);
        alert('Failed to add product to cart');
    }
}

// 更新购物车数量
async function updateCartCount() {
    try {
        const token = localStorage.getItem('token');
        if (!token) return;

        const response = await fetch('/api/cart', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        const cart = await response.json();
        
        const cartCount = document.getElementById('cartCount');
        cartCount.textContent = cart.items.length;
    } catch (error) {
        console.error('Error updating cart count:', error);
    }
}

// 页面加载时获取商品列表和购物车数量
document.addEventListener('DOMContentLoaded', () => {
    fetchProducts();
    updateCartCount();
}); 