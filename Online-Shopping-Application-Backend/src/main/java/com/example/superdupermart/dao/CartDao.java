package com.example.superdupermart.dao;

import com.example.superdupermart.entity.Cart;
import com.example.superdupermart.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CartDao extends AbstractHibernateDao<Cart> {

    public CartDao() {
        setClazz(Cart.class);
    }

    /**
     * 根据用户查询购物车
     */
    public Cart findByUser(User user) {
        Session session = getCurrentSession();
        Query<Cart> query = session.createQuery(
                "FROM Cart WHERE user = :user",
                Cart.class
        );
        query.setParameter("user", user);
        return query.uniqueResult();
    }

    /**
     * 清空指定用户的购物车（下单后使用）
     */
    public void clearCart(User user) {
        Cart cart = findByUser(user);
        if (cart != null) {
            // 使用 HQL 删除购物车项，避免懒加载问题
            Session session = getCurrentSession();
            Query query = session.createQuery(
                "DELETE FROM CartItem WHERE cart = :cart"
            );
            query.setParameter("cart", cart);
            query.executeUpdate();
        }
    }

    /**
     * 删除购物车（用于用户注销）
     */
    public void deleteCartByUser(User user) {
        Cart cart = findByUser(user);
        if (cart != null) {
            delete(cart);
        }
    }
}