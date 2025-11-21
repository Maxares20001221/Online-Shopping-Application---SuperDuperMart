package com.example.superdupermart.service;

import com.example.superdupermart.dao.ProductDao;
import com.example.superdupermart.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "transactionManager")
public class ProductService {

    @Autowired
    private ProductDao productDao;

    /**
     * 获取所有商品
     */
    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    /**
     * 根据 ID 查找商品
     */
    public Product getProductById(Long id) {
        return productDao.findById(id);
    }

    /**
     * 新增商品
     */
    public void saveProduct(Product product) {
        productDao.save(product);
    }

    /**
     * 更新商品信息（兼容 retailPrice / wholesalePrice / quantity）
     */
    public void updateProduct(Long id, Product updatedProduct) {
        Product existing = productDao.findById(id);
        if (existing != null) {
            existing.setName(updatedProduct.getName());
            existing.setDescription(updatedProduct.getDescription());
            existing.setQuantity(updatedProduct.getQuantity());
            existing.setRetailPrice(updatedProduct.getRetailPrice());
            existing.setWholesalePrice(updatedProduct.getWholesalePrice());
            productDao.update(existing);
        }
    }

    /**
     * 删除商品
     */
    public void deleteProduct(Long id) {
        productDao.deleteById(id);
    }
}