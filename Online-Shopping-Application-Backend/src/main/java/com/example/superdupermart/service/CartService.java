package com.example.superdupermart.service;

import com.example.superdupermart.dao.CartDao;
import com.example.superdupermart.dao.CartItemDao;
import com.example.superdupermart.dao.ProductDao;
import com.example.superdupermart.dao.UserDao;
import com.example.superdupermart.entity.Cart;
import com.example.superdupermart.entity.CartItem;
import com.example.superdupermart.entity.Product;
import com.example.superdupermart.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartDao cartDao;

    @Autowired
    private CartItemDao cartItemDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    /**
     * 获取用户购物车中所有商品
     */
    public List<CartItem> getCartItems(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Cart cart = cartDao.findByUser(user);
        if (cart == null) {
            // 如果购物车不存在，返回空列表（而不是抛出异常）
            return java.util.Collections.emptyList();
        }
        return cartItemDao.findByCart(cart);
    }

    /**
     * 添加商品到购物车
     */
    public void addToCart(Long userId, Long productId, int quantity) {
        User user = userDao.findById(userId);
        Product product = productDao.findById(productId);
        if (user == null || product == null) {
            throw new RuntimeException("User or Product not found");
        }

        Cart cart = cartDao.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartDao.save(cart);
        }

        CartItem existingItem = cartItemDao.findByCartAndProduct(cart, product);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemDao.update(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemDao.save(newItem);
        }
    }

    /**
     * 更新购物车中某项商品数量
     */
    public void updateQuantity(Long cartItemId, int newQuantity) {
        CartItem item = cartItemDao.findById(cartItemId);
        if (item == null) {
            throw new RuntimeException("CartItem not found");
        }
        item.setQuantity(newQuantity);
        cartItemDao.update(item);
    }

    /**
     * 删除当前用户购物车中的某个商品
     */
    public void removeItem(Long userId, Long productId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Cart cart = cartDao.findByUser(user);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }
        Product product = productDao.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        CartItem item = cartItemDao.findByCartAndProduct(cart, product);
        if (item != null) {
            cartItemDao.delete(item);
        }
    }

    /**
     * 清空购物车
     */
    public void clearCart(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Cart cart = cartDao.findByUser(user);
        if (cart != null) {
            List<CartItem> items = cartItemDao.findByCart(cart);
            for (CartItem item : items) {
                cartItemDao.delete(item);
            }
        }
    }
}