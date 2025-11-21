package com.example.superdupermart.dao;

import com.example.superdupermart.entity.Watchlist;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WatchlistDao extends AbstractHibernateDao<Watchlist> {

    public WatchlistDao() {
        setClazz(Watchlist.class);
    }

    /**
     * 获取指定用户的所有收藏记录（使用 JOIN FETCH 避免懒加载问题）
     * 对应: /user/watchlist/view
     */
    public List<Watchlist> findByUserId(Long userId) {
        Session session = getCurrentSession();
        Query<Watchlist> query = session.createQuery(
                "SELECT w FROM Watchlist w LEFT JOIN FETCH w.product WHERE w.user.userId = :userId", Watchlist.class);
        query.setParameter("userId", userId);
        return query.list();
    }

    /**
     * 根据用户和商品 ID 查找是否已收藏
     * 对应: /user/watchlist/add (用于判重)
     */
    public Watchlist findByUserAndProduct(Long userId, Long productId) {
        Session session = getCurrentSession();
        Query<Watchlist> query = session.createQuery(
                "FROM Watchlist WHERE user.userId = :userId AND product.productId = :productId",
                Watchlist.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        return query.uniqueResult();
    }

    /**
     * 删除收藏项 (根据用户和商品)
     * 对应: /user/watchlist/remove/{productId}
     */
    public void deleteByUserAndProduct(Long userId, Long productId) {
        Session session = getCurrentSession();
        Query query = session.createQuery(
                "DELETE FROM Watchlist WHERE user.userId = :userId AND product.productId = :productId");
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        query.executeUpdate();
    }
}