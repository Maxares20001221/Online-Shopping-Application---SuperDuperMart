package com.example.superdupermart.dao;

import com.example.superdupermart.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao extends AbstractHibernateDao<User> {

    public UserDao() {
        setClazz(User.class);
    }

    /**
     * 根据用户名查找用户
     * 对应: /auth/login, /auth/register (用户名查重)
     */
    public User findByUsername(String username) {
        Session session = getCurrentSession();
        Query<User> query = session.createQuery(
                "FROM User WHERE username = :username", User.class);
        query.setParameter("username", username);
        return query.uniqueResult();
    }

    /**
     * 根据邮箱查找用户
     * 对应: /auth/register (邮箱查重)
     */
    public User findByEmail(String email) {
        Session session = getCurrentSession();
        Query<User> query = session.createQuery(
                "FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        return query.uniqueResult();
    }

    /**
     * 根据用户名和密码查找用户
     * 对应: /auth/login
     */
    public User findByUsernameAndPassword(String username, String password) {
        Session session = getCurrentSession();
        Query<User> query = session.createQuery(
                "FROM User WHERE username = :username AND password = :password", User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        return query.uniqueResult();
    }

    /**
     * 查询所有用户 (管理员使用)
     * 对应: /admin/users
     */
    public List<User> findAllUsers() {
        return findAll();
    }

    /**
     * 根据角色获取用户列表
     * 对应: 管理员查看所有 buyer/admin
     */
    public List<User> findByRole(String role) {
        Session session = getCurrentSession();
        Query<User> query = session.createQuery(
                "FROM User WHERE role = :role", User.class);
        query.setParameter("role", role);
        return query.list();
    }
}