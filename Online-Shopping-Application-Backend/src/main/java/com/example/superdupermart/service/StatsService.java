package com.example.superdupermart.service;

import com.example.superdupermart.dao.OrderDao;
import com.example.superdupermart.dao.OrderItemDao;
import com.example.superdupermart.dao.ProductDao;
import com.example.superdupermart.dao.UserDao;
import com.example.superdupermart.entity.Order;
import com.example.superdupermart.entity.OrderItem;
import com.example.superdupermart.entity.Product;
import com.example.superdupermart.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class StatsService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    /**
     * 获取销量最高的商品
     * 对应: /admin/stats/topSellingProduct
     */
    public Product getTopSellingProduct() {
        // 基于已完成订单统计（更符合业务口径）
        Map<Product, Long> counter = aggregateProductCountFromCompletedOrders();
        return counter.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * 获取每个商品的销量统计
     * 对应: /admin/stats/allProductSales
     */
    public Map<String, Long> getProductSales() {
        Map<Product, Long> counter = aggregateProductCountFromCompletedOrders();
        Map<String, Long> salesMap = new HashMap<>();
        for (Map.Entry<Product, Long> e : counter.entrySet()) {
            salesMap.put(e.getKey().getName(), e.getValue());
        }
        return salesMap;
    }

    /**
     * 计算平台订单总数
     * 对应: /admin/stats/orderCount
     */
    public int getTotalOrderCount() {
        return orderDao.findAllOrders().size();
    }

    /**
     * 获取最近销售的商品（按销量降序排序）
     * 对应: /user/stats/recentPopularProducts
     */
    public List<Map.Entry<String, Long>> getPopularProducts() {
        Map<String, Long> salesMap = getProductSales();
        List<Map.Entry<String, Long>> sortedList = new ArrayList<>(salesMap.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sortedList.subList(0, Math.min(sortedList.size(), 5)); // 取前5
    }

    // Admin：最盈利 TopN（仅 Completed）
    public List<Map.Entry<String, Double>> getMostProfitableProducts(int topN) {
        List<Order> orders = orderDao.findAllOrders();
        Map<String, Double> profitMap = new HashMap<>();
        for (Order o : orders) {
            if (!"Completed".equalsIgnoreCase(o.getOrderStatus())) continue;
            for (OrderItem oi : o.getOrderItems()) {
                double retail = oi.getProduct().getRetailPrice() == null ? 0.0 : oi.getProduct().getRetailPrice().doubleValue();
                double wholesale = oi.getProduct().getWholesalePrice() == null ? 0.0 : oi.getProduct().getWholesalePrice().doubleValue();
                double profit = (retail - wholesale) * oi.getQuantity();
                profitMap.merge(oi.getProduct().getName(), profit, Double::sum);
            }
        }
        List<Map.Entry<String, Double>> list = new ArrayList<>(profitMap.entrySet());
        list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return list.subList(0, Math.min(list.size(), topN));
    }

    // ======== 新增：用户维度统计 ========
    public List<Map.Entry<String, Long>> getUserMostFrequentProducts(Long userId, int topN) {
        User user = userDao.findById(userId);
        if (user == null) throw new RuntimeException("User not found");
        List<Order> orders = orderDao.findByUser(user);
        Map<String, Long> counter = new HashMap<>();
        for (Order o : orders) {
            if ("Canceled".equalsIgnoreCase(o.getOrderStatus())) continue; // 排除已取消
            for (OrderItem oi : o.getOrderItems()) {
                counter.merge(oi.getProduct().getName(), (long) oi.getQuantity(), Long::sum);
            }
        }
        List<Map.Entry<String, Long>> list = new ArrayList<>(counter.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return list.subList(0, Math.min(list.size(), topN));
    }

    public List<Map.Entry<String, java.time.LocalDateTime>> getUserMostRecentProducts(Long userId, int topN) {
        User user = userDao.findById(userId);
        if (user == null) throw new RuntimeException("User not found");
        List<Order> orders = orderDao.findByUser(user);
        Map<String, java.time.LocalDateTime> latest = new HashMap<>();
        for (Order o : orders) {
            if ("Canceled".equalsIgnoreCase(o.getOrderStatus())) continue;
            for (OrderItem oi : o.getOrderItems()) {
                String name = oi.getProduct().getName();
                latest.merge(name, o.getDatePlaced(), (oldV, newV) -> newV.isAfter(oldV) ? newV : oldV);
            }
        }
        List<Map.Entry<String, java.time.LocalDateTime>> list = new ArrayList<>(latest.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return list.subList(0, Math.min(list.size(), topN));
    }

    // ======== 辅助：Completed 订单聚合销量 ========
    private Map<Product, Long> aggregateProductCountFromCompletedOrders() {
        List<Order> orders = orderDao.findAllOrders();
        Map<Product, Long> counter = new HashMap<>();
        for (Order o : orders) {
            if (!"Completed".equalsIgnoreCase(o.getOrderStatus())) continue;
            for (OrderItem oi : o.getOrderItems()) {
                counter.merge(oi.getProduct(), (long) oi.getQuantity(), Long::sum);
            }
        }
        return counter;
    }
}