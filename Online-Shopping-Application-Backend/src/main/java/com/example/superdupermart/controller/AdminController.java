package com.example.superdupermart.controller;

import com.example.superdupermart.dao.UserDao;
import com.example.superdupermart.dto.OrderItemDTO;
import com.example.superdupermart.dto.OrderResponseDTO;
import com.example.superdupermart.dto.ProductDTO;
import com.example.superdupermart.entity.Order;
import com.example.superdupermart.entity.OrderItem;
import com.example.superdupermart.entity.Product;
import com.example.superdupermart.entity.User;
import com.example.superdupermart.service.OrderService;
import com.example.superdupermart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserDao userDao;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/deleteUser/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userDao.deleteById(userId);
        return "User deleted successfully.";
    }

    @GetMapping("/orders")
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders().stream().map(this::toOrderDTO).collect(Collectors.toList());
    }

    @PutMapping("/updateOrderStatus/{orderId}")
    public String updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return "Order status updated to " + status + ".";
    }

    private OrderResponseDTO toOrderDTO(Order o) {
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

    private ProductDTO toProductDTO(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(p.getProductId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getRetailPrice() == null ? 0.0 : p.getRetailPrice().doubleValue());
        dto.setStock(p.getQuantity());
        return dto;
    }
}