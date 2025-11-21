package com.example.superdupermart.controller;

import com.example.superdupermart.dto.ProductDTO;
import com.example.superdupermart.entity.Product;
import com.example.superdupermart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // GET /products/all （User: 仅展示有库存；Admin: 展示全部包括缺货）
    @GetMapping("/all")
    public List<ProductDTO> getAllProducts() {
        boolean isAdmin = isAdminUser();
        return productService.getAllProducts()
                .stream()
                .filter(p -> isAdmin || p.getQuantity() > 0) // Admin 看全部，User 只看有库存
                .map(p -> toDTO(p, isAdmin))
                .collect(Collectors.toList());
    }

    /**
     * 检查当前用户是否为管理员
     */
    private boolean isAdminUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    // GET /products/{productId}
    @GetMapping("/{productId}")
    public ProductDTO getProductDetailById(@PathVariable Long productId) {
        boolean isAdmin = isAdminUser();
        return toDTO(productService.getProductById(productId), isAdmin);
    }

    // POST /products （Admin.CreateAProduct）
    @PostMapping
    public ProductDTO createProduct(@RequestBody Product payload) {
        productService.saveProduct(payload);
        return toDTO(payload, true);  // 创建产品的是管理员，返回完整信息
    }

    // PATCH /products/{productId} （Admin.UpdateProduct）
    @PatchMapping("/{productId}")
    public ProductDTO updateProduct(@PathVariable Long productId, @RequestBody Product payload) {
        productService.updateProduct(productId, payload);
        return toDTO(productService.getProductById(productId), true);  // 更新产品的是管理员，返回完整信息
    }

    private ProductDTO toDTO(Product p, boolean isAdmin) {
        if (p == null) return null;
        ProductDTO dto = new ProductDTO();
        dto.setProductId(p.getProductId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getRetailPrice() == null ? 0.0 : p.getRetailPrice().doubleValue());
        dto.setStock(p.getQuantity());
        // 仅管理员可见批发价
        if (isAdmin && p.getWholesalePrice() != null) {
            dto.setWholesalePrice(p.getWholesalePrice().doubleValue());
        }
        return dto;
    }
}