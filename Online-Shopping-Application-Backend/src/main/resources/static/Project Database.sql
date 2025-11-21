USE superdupermart;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE cart_items;
TRUNCATE TABLE carts;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE permissions;
TRUNCATE TABLE products;
TRUNCATE TABLE users;
TRUNCATE TABLE watchlists;
SET FOREIGN_KEY_CHECKS = 1;

SELECT * FROM users;
UPDATE users SET role = 'ADMIN' WHERE email = 'maxares1221@gmail.com';

SELECT * FROM carts;

SELECT * FROM orders;

SELECT * FROM cart_items;

SELECT * FROM order_items;

SELECT * FROM permissions;


-- 插入产品数据
INSERT INTO products (name, description, wholesalePrice, retailPrice, quantity) VALUES
('Logistic Mouse', 'A high-precision wireless mouse with ergonomic design, perfect for office and gaming use', 99.99, 300.99, 50),
('Mechanical Keyboard Pro', 'RGB backlit mechanical keyboard with Cherry MX switches, ideal for typing and gaming', 150.00, 450.00, 30),
('Wireless Headphones', 'Premium noise-cancelling wireless headphones with 30-hour battery life', 200.00, 599.99, 25),
('USB-C Hub', '7-in-1 USB-C hub with HDMI, USB 3.0 ports, SD card reader, and power delivery', 45.00, 129.99, 100),
('Laptop Stand', 'Adjustable aluminum laptop stand for better ergonomics and cooling', 25.00, 79.99, 80),
('Webcam HD', '1080p HD webcam with auto-focus and built-in microphone for video conferencing', 60.00, 179.99, 40),
('External SSD 1TB', 'Portable SSD with USB 3.2 Gen 2, read speed up to 1050MB/s', 80.00, 249.99, 60),
('Wireless Charger', 'Fast wireless charging pad compatible with Qi-enabled devices', 30.00, 89.99, 70),
('Monitor Stand', 'Dual monitor stand with gas spring arms, supports up to 27 inches', 120.00, 349.99, 20),
('Desk Mat', 'Large gaming desk mat with stitched edges, waterproof and easy to clean', 15.00, 49.99, 150);

-- 查看插入的产品
SELECT * FROM products;

SELECT * FROM watchlists;




