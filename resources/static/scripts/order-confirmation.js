// 从URL获取订单ID
const urlParams = new URLSearchParams(window.location.search);
const orderId = urlParams.get('orderId');

// 获取订单详情
async function fetchOrderDetails() {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = '/login.html';
            return;
        }

        const response = await fetch(`/api/orders/${orderId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        const order = await response.json();
        renderOrderDetails(order);
    } catch (error) {
        console.error('Error fetching order details:', error);
    }
}

// 渲染订单详情
function renderOrderDetails(order) {
    // 显示订单号
    document.getElementById('orderId').textContent = order.id;

    // 渲染订单商品
    const orderItems = document.getElementById('orderItems');
    orderItems.innerHTML = order.items.map(item => `
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

    // 渲染收货地址
    const shippingAddress = document.getElementById('shippingAddress');
    shippingAddress.innerHTML = `
        <p>${order.shippingAddress.fullName}</p>
        <p>${order.shippingAddress.street}</p>
        <p>${order.shippingAddress.city}, ${order.shippingAddress.state} ${order.shippingAddress.zipCode}</p>
        <p>Phone: ${order.shippingAddress.phone}</p>
    `;

    // 渲染支付方式
    const paymentMethod = document.getElementById('paymentMethod');
    paymentMethod.innerHTML = `
        <p>${order.paymentMethod}</p>
        <p>Payment Status: ${order.paymentStatus}</p>
    `;

    // 更新订单总计
    updateOrderSummary(order.subtotal, order.shippingFee, order.totalAmount);
}

// 更新订单总计
function updateOrderSummary(subtotal, shipping, total) {
    document.getElementById('subtotal').textContent = `$${subtotal.toFixed(2)}`;
    document.getElementById('shipping').textContent = `$${shipping.toFixed(2)}`;
    document.getElementById('total').textContent = `$${total.toFixed(2)}`;
}

// 页面加载时获取订单详情
document.addEventListener('DOMContentLoaded', () => {
    if (!orderId) {
        window.location.href = '/product-list.html';
        return;
    }
    fetchOrderDetails();
}); 