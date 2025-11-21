package com.example.superdupermart.service;

import com.example.superdupermart.dao.UserDao;
import com.example.superdupermart.entity.User;
import com.example.superdupermart.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 注册新用户
     * 检查用户名/邮箱是否已存在
     */
    public String register(User user) {
        if (userDao.findByUsername(user.getUsername()) != null) {
            return "Username already exists";
        }
        if (userDao.findByEmail(user.getEmail()) != null) {
            return "Email already registered";
        }

        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
        return "Registration successful";
    }

    /**
     * 用户登录验证
     */
    public boolean login(String email, String password) {
        User user = userDao.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return true;
    }

    /**
     * 根据邮箱获取用户信息
     */
    public User getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }
}