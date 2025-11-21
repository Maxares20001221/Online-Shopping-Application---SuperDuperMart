package com.example.superdupermart.service;

import com.example.superdupermart.dao.WatchlistDao;
import com.example.superdupermart.entity.Product;
import com.example.superdupermart.entity.User;
import com.example.superdupermart.entity.Watchlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WatchlistService {

    @Autowired
    private WatchlistDao watchlistDao;

    /**
     * 获取用户的所有收藏商品
     * 对应: /user/watchlist/view
     */
    public List<Watchlist> getUserWatchlist(Long userId) {
        return watchlistDao.findByUserId(userId);
    }

    /**
     * 添加商品到收藏夹
     * 对应: /user/watchlist/add
     */
    public String addToWatchlist(User user, Product product) {
        Watchlist existing = watchlistDao.findByUserAndProduct(user.getUserId(), product.getProductId());
        if (existing != null) {
            return "Product already in watchlist";
        }

        Watchlist newWatch = new Watchlist();
        newWatch.setUser(user);
        newWatch.setProduct(product);
        watchlistDao.save(newWatch);

        return "Product added to watchlist";
    }

    /**
     * 从收藏夹移除商品
     * 对应: /user/watchlist/remove/{productId}
     */
    public String removeFromWatchlist(Long userId, Long productId) {
        Watchlist existing = watchlistDao.findByUserAndProduct(userId, productId);
        if (existing == null) {
            return "Product not found in watchlist";
        }

        watchlistDao.deleteByUserAndProduct(userId, productId);
        return "Product removed from watchlist";
    }
}