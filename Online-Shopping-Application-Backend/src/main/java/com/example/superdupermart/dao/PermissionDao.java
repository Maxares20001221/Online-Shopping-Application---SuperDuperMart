package com.example.superdupermart.dao;

import com.example.superdupermart.entity.Permission;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PermissionDao extends AbstractHibernateDao<Permission> {

    public PermissionDao() {
        setClazz(Permission.class);
    }

    /**
     * 根据角色名（如 "admin" 或 "user"）查询对应权限
     * 示例：value字段里可能是 "admin_read", "admin_update"
     */
    public List<Permission> findByRole(String roleName) {
        Session session = getCurrentSession();
        Query<Permission> query = session.createQuery(
                "FROM Permission p WHERE p.value LIKE :rolePattern",
                Permission.class
        );
        query.setParameter("rolePattern", "%" + roleName + "%");
        return query.list();
    }

    /**
     * 获取系统所有权限（一般仅管理员可用）
     */
    public List<Permission> findAllPermissions() {
        return findAll();
    }
}