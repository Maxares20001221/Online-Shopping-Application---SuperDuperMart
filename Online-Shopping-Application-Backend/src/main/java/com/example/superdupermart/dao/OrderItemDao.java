package com.example.superdupermart.dao;

import com.example.superdupermart.entity.Order;
import com.example.superdupermart.entity.OrderItem;
import com.example.superdupermart.entity.Product;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemDao extends AbstractHibernateDao<OrderItem> {

    public OrderItemDao() {
        setClazz(OrderItem.class);
    }

    /**
     * 根据订单查询其所有明细项
     * 对应：订单详情页面 (User/Admin)
     */
    public List<OrderItem> findByOrder(Order order) {
        Session session = getCurrentSession();
        Query<OrderItem> query = session.createQuery(
                "FROM OrderItem WHERE order = :order", OrderItem.class);
        query.setParameter("order", order);
        return query.list();
    }

    /**
     * 批量保存订单项
     * 在下单时被 Service 调用
     */
    public void saveAll(List<OrderItem> items) {
        Session session = getCurrentSession();
        for (OrderItem item : items) {
            session.save(item);
        }
    }

    /**
     * 根据商品统计其被购买次数（用于统计模块）
     * 对应 Postman: /admin/stats/mostPopularProduct
     */
    public Long countByProduct(Product product) {
        Session session = getCurrentSession();
        Query<Long> query = session.createQuery(
                "SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product = :product", Long.class);
        query.setParameter("product", product);
        return query.uniqueResult();
    }
}