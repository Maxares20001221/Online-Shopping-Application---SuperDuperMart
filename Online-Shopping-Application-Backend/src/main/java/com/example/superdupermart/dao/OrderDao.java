package com.example.superdupermart.dao;

import com.example.superdupermart.entity.Order;
import com.example.superdupermart.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderDao extends AbstractHibernateDao<Order> {

    public OrderDao() {
        setClazz(Order.class);
    }

    /**
     * 获取指定用户的所有订单（使用 JOIN FETCH 避免懒加载问题）
     * 对应 Postman: /user/order/getAllOrders
     */
    public List<Order> findByUser(User user) {
        Session session = getCurrentSession();
        Query<Order> query = session.createQuery(
                "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.user = :user ORDER BY o.datePlaced DESC",
                Order.class
        );
        query.setParameter("user", user);
        return query.list();
    }

    /**
     * 管理员查看所有订单（使用 JOIN FETCH 避免懒加载问题）
     * 对应 Postman: /admin/order/getAllOrders
     */
    public List<Order> findAllOrders() {
        Session session = getCurrentSession();
        Query<Order> query = session.createQuery(
                "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product ORDER BY o.datePlaced DESC",
                Order.class
        );
        return query.list();
    }

    /**
     * 根据订单 ID 获取订单详情（使用 JOIN FETCH 避免懒加载问题）
     * 对应 Postman: /user/order/getOrderDetail, /admin/order/getOrderDetail
     */
    public Order findByIdWithDetail(Long orderId) {
        Session session = getCurrentSession();
        Query<Order> query = session.createQuery(
                "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.orderId = :id",
                Order.class
        );
        query.setParameter("id", orderId);
        return query.uniqueResult();
    }

    /**
     * 更新订单状态（取消、完成等）
     * 对应 Postman: /admin/order/updateOrderStatus
     */
    public void updateOrderStatus(Long orderId, String status) {
        Session session = getCurrentSession();
        Query query = session.createQuery(
                "UPDATE Order SET orderStatus = :status WHERE orderId = :id"
        );
        query.setParameter("status", status);
        query.setParameter("id", orderId);
        query.executeUpdate();
    }
}