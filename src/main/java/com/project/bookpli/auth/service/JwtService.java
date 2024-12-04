package com.project.bookpli.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class JwtService {

    private final String secretKey;

    // 생성자 주입을 통해 secretKey를 초기화
    public JwtService(@Value("${CUSTOM_JWT_SECRETKEY}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String createToken(String spotifyId) {
        return Jwts.builder()
                .setSubject(spotifyId) // 사용자 식별 정보
                .setIssuedAt(new Date()) // 토큰 생성 시간
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1시간 유효
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 알고리즘 및 비밀키
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
