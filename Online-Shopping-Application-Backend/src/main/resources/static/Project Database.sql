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

SELECT * FROM carts;

SELECT * FROM orders;

SELECT * FROM cart_item;

SELECT * FROM order_item;

SELECT * FROM permissions;

SELECT * FROM products;

SELECT * FROM watchlists;

UPDATE users SET role = 'ADMIN' WHERE email = 'maxares1221@gmail.com';
SELECT * FROM users;




