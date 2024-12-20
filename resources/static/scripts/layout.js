function loadLayout(title, content) {
    document.title = title;
    
    // 添加一些基本的错误处理和调试信息
    try {
        const body = document.body;
        if (!body) {
            console.error('Body element not found');
            return;
        }

        // 添加页面基本结构
        body.innerHTML = `
            <header>
                <nav>
                    <div class="logo">
                        <a href="/">E-Commerce</a>
                    </div>
                    <div class="nav-links">
                        <a href="/pages/product-list.html">Products</a>
                        <a href="/pages/cart.html">Cart</a>
                        <a href="/pages/login.html">Login</a>
                    </div>
                </nav>
            </header>
            <main>
                ${content}
            </main>
            <footer>
                <p>&copy; 2024 E-Commerce. All rights reserved.</p>
            </footer>
        `;
        
        console.log('Layout loaded successfully');
    } catch (error) {
        console.error('Error loading layout:', error);
    }
} 