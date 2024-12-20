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
        renderOrderSummary(cart);
    } catch (error) {
        console.error('Error fetching cart:', error);
    }
}

// 渲染订单摘要
function renderOrderSummary(cart) {
    const orderItems = document.getElementById('orderItems');
    
    orderItems.innerHTML = cart.items.map(item => `
        <div class="order-item">
            <img src="${item.productImageUrl}" alt="${item.productName}">
            <div class="item-details">
                <div class="item-name">${item.productName}</div>
                <div class="item-price">$${item.price}</div>
                <div class="item-quantity">Quantity: ${item.quantity}</div>
            </div>
            <div class="item-subtotal">$${item.subtotal}</div>
        </div>
    `).join('');

    updateSummary(cart.totalAmount);
}

// 更新总计
function updateSummary(subtotal) {
    const shipping = subtotal > 0 ? 10 : 0;
    const total = subtotal + shipping;

    document.getElementById('subtotal').textContent = `$${subtotal.toFixed(2)}`;
    document.getElementById('shipping').textContent = `$${shipping.toFixed(2)}`;
    document.getElementById('total').textContent = `$${total.toFixed(2)}`;
}

// 提交订单
async function placeOrder() {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = '/login.html';
            return;
        }

        // 获取表单数据
        const shippingData = {
            fullName: document.getElementById('fullName').value,
            street: document.getElementById('street').value,
            city: document.getElementById('city').value,
            state: document.getElementById('state').value,
            zipCode: document.getElementById('zipCode').value,
            phone: document.getElementById('phone').value
        };

        const paymentData = {
            method: document.querySelector('input[name="payment"]:checked').value,
            cardNumber: document.getElementById('cardNumber').value,
            expiry: document.getElementById('expiry').value,
            cvv: document.getElementById('cvv').value
        };

        // 创建订单
        const response = await fetch('/api/orders', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                shippingAddress: shippingData,
                paymentMethod: paymentData.method
            })
        });

        if (response.ok) {
            const order = await response.json();
            // 跳转到订单确认页面
            window.location.href = `/order-confirmation.html?orderId=${order.id}`;
        } else {
            throw new Error('Failed to create order');
        }
    } catch (error) {
        console.error('Error placing order:', error);
        alert('Failed to place order. Please try again.');
    }
}

// 页面加载时获取购物车数据
document.addEventListener('DOMContentLoaded', fetchCart);

// 信用卡输入格式化
document.getElementById('cardNumber').addEventListener('input', function(e) {
    let value = e.target.value.replace(/\D/g, '');
    value = value.replace(/(.{4})/g, '$1 ').trim();
    e.target.value = value;
});

// 有效期输入格式化
document.getElementById('expiry').addEventListener('input', function(e) {
    let value = e.target.value.replace(/\D/g, '');
    if (value.length >= 2) {
        value = value.slice(0,2) + '/' + value.slice(2,4);
    }
    e.target.value = value;
});

// CVV输入限制
document.getElementById('cvv').addEventListener('input', function(e) {
    let value = e.target.value.replace(/\D/g, '');
    e.target.value = value.slice(0,3);
}); 