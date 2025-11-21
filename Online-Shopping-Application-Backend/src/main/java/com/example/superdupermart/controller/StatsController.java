package com.example.superdupermart.controller;

import com.example.superdupermart.dao.OrderDao;
import com.example.superdupermart.dao.UserDao;
import com.example.superdupermart.dto.StatsDTO;
import com.example.superdupermart.entity.Order;
import com.example.superdupermart.entity.OrderItem;
import com.example.superdupermart.entity.User;
import com.example.superdupermart.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class StatsController {

    @Autowired
    private StatsService statsService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserDao userDao;

    // GET /products/frequent/{topN} （用户：最常购买 topN）
    @GetMapping("/products/frequent/{topN}")
    @Transactional
    public List<StatsDTO> mostFrequentlyPurchased(@PathVariable int topN, @RequestParam Long userId) {
        User user = userDao.findById(userId);
        if (user == null) throw new RuntimeException("User not found");
        
        // 获取用户最常购买的商品列表
        List<Map.Entry<String, Long>> frequentProducts = statsService.getUserMostFrequentProducts(userId, topN);
        
        // 计算每个商品的总收入
        List<Order> userOrders = orderDao.findByUser(user);
        Map<String, Double> revenueMap = new HashMap<>();
        Map<String, Long> soldMap = new HashMap<>();
        
        for (Order order : userOrders) {
            if ("Canceled".equalsIgnoreCase(order.getOrderStatus())) continue;
            for (OrderItem item : order.getOrderItems()) {
                String productName = item.getProduct().getName();
                long quantity = item.getQuantity();
                double revenue = item.getPurchasedPrice() == null ? 0.0 : 
                    item.getPurchasedPrice().doubleValue() * quantity;
                
                revenueMap.merge(productName, revenue, Double::sum);
                soldMap.merge(productName, quantity, Long::sum);
            }
        }
        
        return frequentProducts.stream()
                .map(e -> toStats(e.getKey(), e.getValue(), revenueMap.getOrDefault(e.getKey(), 0.0)))
                .collect(Collectors.toList());
    }

    // GET /products/recent/{topN} （用户：最近购买 topN）
    @GetMapping("/products/recent/{topN}")
    @Transactional
    public List<StatsDTO> mostRecentlyPurchased(@PathVariable int topN, @RequestParam Long userId) {
        User user = userDao.findById(userId);
        if (user == null) throw new RuntimeException("User not found");
        
        // 获取用户最近购买的商品列表
        List<Map.Entry<String, java.time.LocalDateTime>> recentProducts = statsService.getUserMostRecentProducts(userId, topN);
        
        // 计算每个商品的总销量和总收入
        List<Order> userOrders = orderDao.findByUser(user);
        Map<String, Double> revenueMap = new HashMap<>();
        Map<String, Long> soldMap = new HashMap<>();
        
        for (Order order : userOrders) {
            if ("Canceled".equalsIgnoreCase(order.getOrderStatus())) continue;
            for (OrderItem item : order.getOrderItems()) {
                String productName = item.getProduct().getName();
                long quantity = item.getQuantity();
                double revenue = item.getPurchasedPrice() == null ? 0.0 : 
                    item.getPurchasedPrice().doubleValue() * quantity;
                
                revenueMap.merge(productName, revenue, Double::sum);
                soldMap.merge(productName, quantity, Long::sum);
            }
        }
        
        return recentProducts.stream()
                .map(e -> toStats(e.getKey(), 
                    soldMap.getOrDefault(e.getKey(), 0L), 
                    revenueMap.getOrDefault(e.getKey(), 0.0)))
                .collect(Collectors.toList());
    }

    // GET /products/profit/{topN} （Admin：最盈利 topN）
    @GetMapping("/products/profit/{topN}")
    @Transactional
    public List<StatsDTO> mostProfitable(@PathVariable int topN) {
        // 获取最盈利的商品列表（利润已计算）
        List<Map.Entry<String, Double>> profitableProducts = statsService.getMostProfitableProducts(topN);
        
        // 计算每个商品的总销量和总收入（仅 Completed 订单）
        List<Order> allOrders = orderDao.findAllOrders();
        Map<String, Long> soldMap = new HashMap<>();
        Map<String, Double> revenueMap = new HashMap<>();
        
        for (Order order : allOrders) {
            if (!"Completed".equalsIgnoreCase(order.getOrderStatus())) continue;
            for (OrderItem item : order.getOrderItems()) {
                String productName = item.getProduct().getName();
                long quantity = item.getQuantity();
                double revenue = item.getPurchasedPrice() == null ? 0.0 : 
                    item.getPurchasedPrice().doubleValue() * quantity;
                
                soldMap.merge(productName, quantity, Long::sum);
                revenueMap.merge(productName, revenue, Double::sum);
            }
        }
        
        return profitableProducts.stream()
                .map(e -> toStats(e.getKey(), 
                    soldMap.getOrDefault(e.getKey(), 0L), 
                    revenueMap.getOrDefault(e.getKey(), 0.0))) // 显示总收入
                .collect(Collectors.toList());
    }

    // GET /products/popular/{topN} （Admin：最受欢迎 topN = 销量）
    @GetMapping("/products/popular/{topN}")
    @Transactional
    public List<StatsDTO> mostPopular(@PathVariable int topN) {
        // 获取最受欢迎的商品列表（销量已计算）
        List<Map.Entry<String, Long>> popularProducts = statsService.getPopularProducts();
        
        // 计算每个商品的总收入（仅 Completed 订单）
        List<Order> allOrders = orderDao.findAllOrders();
        Map<String, Double> revenueMap = new HashMap<>();
        
        for (Order order : allOrders) {
            if (!"Completed".equalsIgnoreCase(order.getOrderStatus())) continue;
            for (OrderItem item : order.getOrderItems()) {
                String productName = item.getProduct().getName();
                long quantity = item.getQuantity();
                double revenue = item.getPurchasedPrice() == null ? 0.0 : 
                    item.getPurchasedPrice().doubleValue() * quantity;
                
                revenueMap.merge(productName, revenue, Double::sum);
            }
        }
        
        return popularProducts.stream()
                .limit(topN)
                .map(e -> toStats(e.getKey(), e.getValue(), revenueMap.getOrDefault(e.getKey(), 0.0)))
                .collect(Collectors.toList());
    }

    private StatsDTO toStats(String productName, long totalSold, double revenue) {
        StatsDTO dto = new StatsDTO();
        dto.setProductName(productName);
        dto.setTotalSold(totalSold);
        dto.setTotalRevenue(revenue);
        return dto;
    }
}