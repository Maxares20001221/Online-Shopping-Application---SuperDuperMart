package com.example.superdupermart.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    private final String SECRET_KEY = "superdupermart-secret-key"; // ✅ 自定义密钥
    private final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 1天有效期

    /**
     * 生成Token
     */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * 从Token中解析用户名（email）
     */
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 从Token中解析角色
     */
    public String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }

    /**
     * 验证Token是否过期
     */
    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    /**
     * 验证Token合法性
     */
    public boolean validateToken(String token, String email) {
        return (email.equals(extractEmail(token)) && !isTokenExpired(token));
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}