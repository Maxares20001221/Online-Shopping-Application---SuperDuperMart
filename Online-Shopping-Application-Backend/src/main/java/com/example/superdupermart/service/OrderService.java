package com.example.superdupermart.service;

import com.example.superdupermart.dao.*;
import com.example.superdupermart.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CartDao cartDao;

    @Autowired
    private CartItemDao cartItemDao;

    @Autowired
    private ProductDao productDao;

    /**
     * 用户下单（从购物车创建订单）
     */
    @Transactional(transactionManager = "transactionManager")
    public void placeOrder(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Cart cart = cartDao.findByUser(user);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }

        List<CartItem> items = cartItemDao.findByCart(cart);
        if (items.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 创建新订单
        Order order = new Order();
        order.setUser(user);
        order.setDatePlaced(LocalDateTime.now());
        order.setOrderStatus("Processing");

        for (CartItem cartItem : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            
            // 扣减库存
            Product product = cartItem.getProduct();
            if (product == null) {
                throw new RuntimeException("Product not found in cart item");
            }
            
            // 设置下单时的价格（使用零售价作为购买价）
            if (product.getRetailPrice() == null) {
                throw new RuntimeException("Product retail price is null: " + product.getName());
            }
            orderItem.setPurchasedPrice(product.getRetailPrice());
            
            // 设置进货价
            if (product.getWholesalePrice() == null) {
                throw new RuntimeException("Product wholesale price is null: " + product.getName());
            }
            orderItem.setWholesalePrice(product.getWholesalePrice());
            
            order.getOrderItems().add(orderItem);

            if (product.getQuantity() == null || product.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productDao.update(product);
        }

        // 保存订单
        orderDao.save(order);

        // 清空购物车
        cartDao.clearCart(user);
    }

    /**
     * 用户下单（直接指定商品集合）
     */
    @Transactional(transactionManager = "transactionManager")
    public void placeOrder(User user, Set<OrderItem> orderItems) {
        Order order = new Order();
        order.setUser(user);
        order.setDatePlaced(LocalDateTime.now());
        order.setOrderStatus("Processing");
        order.setOrderItems(orderItems);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
            // 扣减库存
            Product product = item.getProduct();
            if (product == null) {
                throw new RuntimeException("Product not found in order item");
            }
            
            // 设置下单时的价格（如果还没有设置）
            if (item.getPurchasedPrice() == null) {
                if (product.getRetailPrice() == null) {
                    throw new RuntimeException("Product retail price is null: " + product.getName());
                }
                item.setPurchasedPrice(product.getRetailPrice());
            }
            
            if (item.getWholesalePrice() == null) {
                if (product.getWholesalePrice() == null) {
                    throw new RuntimeException("Product wholesale price is null: " + product.getName());
                }
                item.setWholesalePrice(product.getWholesalePrice());
            }
            
            if (product.getQuantity() == null || product.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productDao.update(product);
        }

        orderDao.save(order);
    }

    /**
     * 获取指定用户的所有订单
     */
    @Transactional(transactionManager = "transactionManager")
    public List<Order> getOrdersByUser(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return orderDao.findByUser(user);
    }

    /**
     * 获取订单详情
     */
    @Transactional(transactionManager = "transactionManager")
    public Order getOrderDetail(Long orderId) {
        return orderDao.findByIdWithDetail(orderId);
    }

    /**
     * 管理员查看所有订单
     */
    @Transactional(transactionManager = "transactionManager")
    public List<Order> getAllOrders() {
        return orderDao.findAllOrders();
    }

    /**
     * 管理员更新订单状态
     */
    @Transactional(transactionManager = "transactionManager")
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderDao.findByIdWithDetail(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        String current = order.getOrderStatus();

        if ("Canceled".equalsIgnoreCase(status)) {
            if ("Completed".equalsIgnoreCase(current)) {
                throw new RuntimeException("Completed order cannot be canceled");
            }
            if (!"Canceled".equalsIgnoreCase(current)) {
                // 从 Processing 取消时回补库存
                for (OrderItem item : order.getOrderItems()) {
                    Product product = item.getProduct();
                    product.setQuantity(product.getQuantity() + item.getQuantity());
                    productDao.update(product);
                }
            }
            orderDao.updateOrderStatus(orderId, "Canceled");
            return;
        }

        if ("Completed".equalsIgnoreCase(status)) {
            if ("Canceled".equalsIgnoreCase(current)) {
                throw new RuntimeException("Canceled order cannot be completed");
            }
            orderDao.updateOrderStatus(orderId, "Completed");
            return;
        }

        // 其他状态直接更新
        orderDao.updateOrderStatus(orderId, status);
    }
}