package com.example.superdupermart.controller;

import com.example.superdupermart.dto.CartItemDTO;
import com.example.superdupermart.dto.CartResponseDTO;
import com.example.superdupermart.entity.CartItem;
import com.example.superdupermart.entity.Product;
import com.example.superdupermart.entity.User;
import com.example.superdupermart.service.AuthService;
import com.example.superdupermart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private AuthService authService;

    /**
     * 验证用户是否有权限访问该 userId 的购物车
     */
    private void validateUserAccess(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("Authentication required");
        }
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        
        if (!isAdmin) {
            // 普通用户只能访问自己的购物车
            String email = authentication.getName();
            User currentUser = authService.getUserByEmail(email);
            if (currentUser == null || !currentUser.getUserId().equals(userId)) {
                throw new RuntimeException("You can only access your own cart");
            }
        }
    }

    @GetMapping("/view")
    public CartResponseDTO viewCart(@RequestParam Long userId) {
        validateUserAccess(userId);
        List<CartItem> items = cartService.getCartItems(userId);
        return toCartResponseDTO(userId, items);
    }

    @PostMapping("/add")
    public CartResponseDTO addToCart(@RequestParam Long userId,
                                     @RequestParam Long productId,
                                     @RequestParam int quantity) {
        validateUserAccess(userId);
        cartService.addToCart(userId, productId, quantity);
        return viewCart(userId);
    }

    @PatchMapping("/updateQuantity")
    public CartResponseDTO updateQuantity(@RequestParam Long cartItemId,
                                          @RequestParam int newQuantity,
                                          @RequestParam Long userId) {
        validateUserAccess(userId);
        cartService.updateQuantity(cartItemId, newQuantity);
        return viewCart(userId);
    }

    @DeleteMapping("/remove/{productId}")
    public CartResponseDTO removeItem(@PathVariable Long productId, @RequestParam Long userId) {
        validateUserAccess(userId);
        cartService.removeItem(userId, productId);
        return viewCart(userId);
    }

    @DeleteMapping("/clear/{userId}")
    public CartResponseDTO clearCart(@PathVariable Long userId) {
        validateUserAccess(userId);
        cartService.clearCart(userId);
        return toCartResponseDTO(userId, java.util.Collections.emptyList());
    }

    private CartResponseDTO toCartResponseDTO(Long userId, List<CartItem> items) {
        List<CartItemDTO> list = items.stream().map(this::toItemDTO).collect(Collectors.toList());
        int totalItems = list.stream().mapToInt(CartItemDTO::getQuantity).sum();
        double totalPrice = list.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();

        CartResponseDTO dto = new CartResponseDTO();
        dto.setUserId(userId);
        dto.setItems(list);
        dto.setTotalItems(totalItems);
        dto.setTotalPrice(totalPrice);
        return dto;
    }

    private CartItemDTO toItemDTO(CartItem item) {
        Product p = item.getProduct();
        CartItemDTO dto = new CartItemDTO();
        dto.setItemId(item.getItemId());
        dto.setProductId(p.getProductId());
        dto.setProductName(p.getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(p.getRetailPrice() == null ? 0.0 : p.getRetailPrice().doubleValue());
        return dto;
    }
}