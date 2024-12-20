// 获取购物车数据
async function fetchCart() {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = '/login.html';
            return;
        }

        const response = await fetch('/api/cart', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        const cart = await response.json();
        renderCart(cart);
    } catch (error) {
        console.error('Error fetching cart:', error);
    }
}

// 渲染购物车
function renderCart(cart) {
    const cartItems = document.getElementById('cartItems');
    
    if (!cart.items || cart.items.length === 0) {
        cartItems.innerHTML = '<div class="empty-cart">Your cart is empty</div>';
        updateSummary(0, 0);
        return;
    }

    cartItems.innerHTML = cart.items.map(item => `
        <div class="cart-item">
            <img src="${item.productImageUrl}" alt="${item.productName}">
            <div class="item-details">
                <div class="item-name">${item.productName}</div>
                <div class="item-price">$${item.price}</div>
            </div>
            <div class="quantity-controls">
                <button onclick="updateQuantity(${item.id}, ${item.quantity - 1})">-</button>
                <input type="number" value="${item.quantity}" 
                       onchange="updateQuantity(${item.id}, this.value)">
                <button onclick="updateQuantity(${item.id}, ${item.quantity + 1})">+</button>
            </div>
            <div class="item-subtotal">$${item.subtotal}</div>
            <button class="remove-btn" onclick="removeItem(${item.id})">Remove</button>
        </div>
    `).join('');

    updateSummary(cart.totalAmount);
}

// 更新数量
async function updateQuantity(itemId, quantity) {
    if (quantity < 1) return;

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`/api/cart/items/${itemId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                quantity: quantity
            })
        });

        if (response.ok) {
            fetchCart();
        }
    } catch (error) {
        console.error('Error updating quantity:', error);
    }
}

// 移除商品
async function removeItem(itemId) {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`/api/cart/items/${itemId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            fetchCart();
        }
    } catch (error) {
        console.error('Error removing item:', error);
    }
}

// 更新总计
function updateSummary(subtotal) {
    const shipping = subtotal > 0 ? 10 : 0; // 简单的运费计算
    const total = subtotal + shipping;

    document.getElementById('subtotal').textContent = `$${subtotal.toFixed(2)}`;
    document.getElementById('shipping').textContent = `$${shipping.toFixed(2)}`;
    document.getElementById('total').textContent = `$${total.toFixed(2)}`;
}

// 结账
function checkout() {
    // 跳转到结账页面
    window.location.href = '/checkout.html';
}

// 页面加载时获取购物车数据
document.addEventListener('DOMContentLoaded', fetchCart); 