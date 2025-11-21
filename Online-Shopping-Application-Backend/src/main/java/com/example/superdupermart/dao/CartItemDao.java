package com.example.superdupermart.dao;

import com.example.superdupermart.entity.Cart;
import com.example.superdupermart.entity.CartItem;
import com.example.superdupermart.entity.Product;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CartItemDao extends AbstractHibernateDao<CartItem> {

    public CartItemDao() {
        setClazz(CartItem.class);
    }

    /**
     * 根据购物车查找所有购物项（使用 JOIN FETCH 避免懒加载问题）
     */
    public List<CartItem> findByCart(Cart cart) {
        Session session = getCurrentSession();
        Query<CartItem> query = session.createQuery(
                "SELECT ci FROM CartItem ci LEFT JOIN FETCH ci.product WHERE ci.cart = :cart",
                CartItem.class
        );
        query.setParameter("cart", cart);
        return query.list();
    }

    /**
     * 根据购物车与商品查找是否已有该商品
     * 用于：添加商品时判重
     */
    public CartItem findByCartAndProduct(Cart cart, Product product) {
        Session session = getCurrentSession();
        Query<CartItem> query = session.createQuery(
                "FROM CartItem WHERE cart = :cart AND product = :product",
                CartItem.class
        );
        query.setParameter("cart", cart);
        query.setParameter("product", product);
        return query.uniqueResult();
    }

    /**
     * 根据商品 ID 查找购物项
     * 用于删除商品
     */
    public CartItem findByProductId(Long productId) {
        Session session = getCurrentSession();
        Query<CartItem> query = session.createQuery(
                "FROM CartItem WHERE product.id = :productId",
                CartItem.class
        );
        query.setParameter("productId", productId);
        return query.uniqueResult();
    }

    /**
     * 更新购物车中某个商品的数量
     */
    public void updateQuantity(Long cartItemId, int newQuantity) {
        Session session = getCurrentSession();
        Query query = session.createQuery(
                "UPDATE CartItem SET quantity = :qty WHERE itemId = :id"
        );
        query.setParameter("qty", newQuantity);
        query.setParameter("id", cartItemId);
        query.executeUpdate();
    }

    /**
     * 删除购物车中指定商品
     */
    public void deleteByCartAndProduct(Cart cart, Product product) {
        Session session = getCurrentSession();
        Query query = session.createQuery(
                "DELETE FROM CartItem WHERE cart = :cart AND product = :product"
        );
        query.setParameter("cart", cart);
        query.setParameter("product", product);
        query.executeUpdate();
    }
}