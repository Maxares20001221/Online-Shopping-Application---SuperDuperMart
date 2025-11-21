package com.example.superdupermart.controller;

import com.example.superdupermart.dto.LoginRequestDTO;
import com.example.superdupermart.dto.UserRegistrationDTO;
import com.example.superdupermart.entity.User;
import com.example.superdupermart.security.JwtTokenUtil;
import com.example.superdupermart.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody UserRegistrationDTO req) {
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        String message = authService.register(user);
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", message);
        return resp;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequestDTO req) {
        // collection 使用 username 字段，这里与 service 的 email 参数对齐
        String email = req.getUsername();
        authService.login(email, req.getPassword()); // 无效会抛 InvalidCredentialsException

        // 从数据库获取用户信息
        User user = authService.getUserByEmail(email);
        String role = user != null ? user.getRole() : "USER";
        
        Map<String, Object> resp = new HashMap<>();
        String token = jwtTokenUtil.generateToken(email, role);
        resp.put("message", "Login successful");
        resp.put("token", token);
        resp.put("role", role);
        // 返回用户信息给前端
        if (user != null) {
            resp.put("userId", user.getUserId());
            resp.put("username", user.getUsername());
        }
        return resp;
    }
}