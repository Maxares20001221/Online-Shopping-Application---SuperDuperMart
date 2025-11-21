package com.example.superdupermart.controller;

import com.example.superdupermart.dto.OrderItemDTO;
import com.example.superdupermart.dto.OrderResponseDTO;
import com.example.superdupermart.entity.Order;
import com.example.superdupermart.entity.OrderItem;
import com.example.superdupermart.entity.User;
import com.example.superdupermart.service.AuthService;
import com.example.superdupermart.service.OrderService;
import com.example.superdupermart.service.ProductService;
import com.example.superdupermart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private AuthService authService;

    // POST /orders  Body: { "order":[{"productId":1,"quantity":10}, ...] }
    // 说明：现有 service 是“从购物车下单”，这里为了匹配 collection，仍调用 placeOrder(userId) 的模式不变；
    // 如果需要严格按 body 下单，应在 service 层补方法。先提供最小可运行版本。
    public static class OrderLine {
        public Long productId;
        public int quantity;
    }

    public static class PlaceOrderRequest {
        public java.util.List<OrderLine> order;
    }

    @PostMapping("/orders")
    public Map<String, Object> placeNewOrder(@RequestParam Long userId, @RequestBody(required = false) PlaceOrderRequest body) {
        if (body == null || body.order == null || body.order.isEmpty()) {
            orderService.placeOrder(userId);
        } else {
            java.util.Set<com.example.superdupermart.entity.OrderItem> set = new java.util.HashSet<>();
            for (OrderLine line : body.order) {
                com.example.superdupermart.entity.OrderItem oi = new com.example.superdupermart.entity.OrderItem();
                oi.setProduct(productService.getProductById(line.productId));
                oi.setQuantity(line.quantity);
                set.add(oi);
            }
            orderService.placeOrder(userService.getUserById(userId), set);
        }
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("message", "Order placed");
        return resp;
    }

    // GET /orders/all
    @GetMapping("/orders/all")
    public List<OrderResponseDTO> getAllOrders(@RequestParam(required = false) Long userId) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        
        List<Order> orders;
        if (isAdmin && userId == null) {
            // 管理员且没有指定 userId，返回所有订单
            orders = orderService.getAllOrders();
        } else {
            // 普通用户或管理员指定了 userId，返回指定用户的订单
            Long targetUserId = userId;
            if (targetUserId == null) {
                // 如果没有提供 userId，从 token 中获取当前用户
                String email = authentication != null ? authentication.getName() : null;
                if (email != null) {
                    User currentUser = authService.getUserByEmail(email);
                    if (currentUser != null) {
                        targetUserId = currentUser.getUserId();
                    }
                }
            }
            if (targetUserId == null) {
                throw new RuntimeException("User ID is required");
            }
            // 验证：普通用户只能查看自己的订单
            if (!isAdmin) {
                String email = authentication != null ? authentication.getName() : null;
                if (email != null) {
                    User currentUser = authService.getUserByEmail(email);
                    if (currentUser != null && !currentUser.getUserId().equals(targetUserId)) {
                        throw new RuntimeException("You can only view your own orders");
                    }
                }
            }
            orders = orderService.getOrdersByUser(targetUserId);
        }
        return orders.stream().map(this::toOrderDTO).collect(Collectors.toList());
    }

    // GET /orders/{orderId}
    @GetMapping("/orders/{orderId}")
    public OrderResponseDTO getOrderDetail(@PathVariable Long orderId) {
        return toOrderDTO(orderService.getOrderDetail(orderId));
    }

    // PATCH /orders/{orderId}/cancel
    @PatchMapping("/orders/{orderId}/cancel")
    public Map<String, Object> cancelOrder(@PathVariable Long orderId) {
        orderService.updateOrderStatus(orderId, "Canceled");
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("message", "Order canceled");
        return resp;
    }

    // PATCH /orders/{orderId}/complete
    @PatchMapping("/orders/{orderId}/complete")
    public Map<String, Object> completeOrder(@PathVariable Long orderId) {
        orderService.updateOrderStatus(orderId, "Completed");
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("message", "Order completed");
        return resp;
    }

    private OrderResponseDTO toOrderDTO(Order o) {
        if (o == null) return null;
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(o.getOrderId());
        dto.setDatePlaced(o.getDatePlaced());
        dto.setOrderStatus(o.getOrderStatus());
        double total = o.getOrderItems().stream()
                .mapToDouble(i -> (i.getProduct().getRetailPrice() == null ? 0.0 : i.getProduct().getRetailPrice().doubleValue()) * i.getQuantity())
                .sum();
        dto.setTotalPrice(total);
        dto.setItems(o.getOrderItems().stream().map(this::toItemDTO).collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO toItemDTO(OrderItem oi) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(oi.getProduct().getProductId());
        dto.setProductName(oi.getProduct().getName());
        dto.setQuantity(oi.getQuantity());
        dto.setPrice(oi.getProduct().getRetailPrice() == null ? 0.0 : oi.getProduct().getRetailPrice().doubleValue());
        return dto;
    }
}