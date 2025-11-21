package com.example.superdupermart.service;

import com.example.superdupermart.dao.UserDao;
import com.example.superdupermart.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "transactionManager")
public class UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 根据用户 ID 获取用户信息
     */
    public User getUserById(Long userId) {
        return userDao.findById(userId);
    }

    /**
     * 更新用户信息
     */
    public void updateUser(Long userId, User updatedUser) {
        User existingUser = userDao.findById(userId);
        if (existingUser != null) {
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setRole(updatedUser.getRole());
            userDao.update(existingUser);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    /**
     * 查询所有用户（管理员功能）
     */
    public List<User> getAllUsers() {
        return userDao.findAllUsers();
    }
}