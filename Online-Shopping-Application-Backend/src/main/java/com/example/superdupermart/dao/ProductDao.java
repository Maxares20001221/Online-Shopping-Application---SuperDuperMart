package com.example.superdupermart.dao;

import com.example.superdupermart.entity.Product;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDao extends AbstractHibernateDao<Product> {

    public ProductDao() {
        setClazz(Product.class);
    }

    /**
     * 使用 Criteria 查询所有仍有库存的商品
     * 对应 Postman: User/Product/GetAllInStockProducts
     */
    public List<Product> findAllInStock() {
        Session session = getCurrentSession();
        Criteria criteria = session.createCriteria(Product.class);
        criteria.add(Restrictions.gt("stock", 0)); // stock > 0
        return criteria.list();
    }

    /**
     * 根据关键词搜索商品名称（模糊匹配）
     * 对应 Postman: 用户搜索
     */
    public List<Product> searchByName(String keyword) {
        Session session = getCurrentSession();
        Criteria criteria = session.createCriteria(Product.class);
        criteria.add(Restrictions.ilike("name", keyword, MatchMode.ANYWHERE));
        return criteria.list();
    }

    /**
     * 根据价格区间查找商品（演示用途）
     */
    public List<Product> findByPriceRange(double minPrice, double maxPrice) {
        Session session = getCurrentSession();
        Criteria criteria = session.createCriteria(Product.class);
        criteria.add(Restrictions.between("price", minPrice, maxPrice));
        return criteria.list();
    }
}