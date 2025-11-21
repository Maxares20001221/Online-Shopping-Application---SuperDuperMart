package com.example.superdupermart.controller;

import com.example.superdupermart.dto.WatchlistDTO;
import com.example.superdupermart.entity.Watchlist;
import com.example.superdupermart.service.ProductService;
import com.example.superdupermart.service.UserService;
import com.example.superdupermart.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // GET /watchlist/products/all
    @GetMapping("/products/all")
    public List<WatchlistDTO> getAllWatchlist(@RequestParam Long userId) {
        return watchlistService.getUserWatchlist(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // POST /watchlist/product/{productId}
    @PostMapping("/product/{productId}")
    public String addToWatchlist(@RequestParam Long userId, @PathVariable Long productId) {
        return watchlistService.addToWatchlist(
                userService.getUserById(userId),
                productService.getProductById(productId)
        );
    }

    // DELETE /watchlist/product/{productId}
    @DeleteMapping("/product/{productId}")
    public String removeFromWatchlist(@RequestParam Long userId, @PathVariable Long productId) {
        return watchlistService.removeFromWatchlist(userId, productId);
    }

    private WatchlistDTO toDTO(Watchlist w) {
        WatchlistDTO dto = new WatchlistDTO();
        dto.setProductId(w.getProduct().getProductId());
        dto.setProductName(w.getProduct().getName());
        dto.setPrice(w.getProduct().getRetailPrice() == null ? 0.0 : w.getProduct().getRetailPrice().doubleValue());
        return dto;
    }
}