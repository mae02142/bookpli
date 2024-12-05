package com.project.bookpli.auth.service;

import com.project.bookpli.auth.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
@Service
public class JwtService {

    private final String secretKeyString;

    // 생성자 주입을 통해 비밀 키를 초기화
    public JwtService(@Value("${CUSTOM_JWT_SECRETKEY}") String secretKeyString) {
        this.secretKeyString = secretKeyString;
    }

    // SecretKey 객체 생성
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    // JWT 토큰 생성 메서드
    public String createToken(UserDTO userDTO, String accessToken) {
        SecretKey secretKey = getSecretKey();
        return Jwts.builder()
                .setSubject(userDTO.getSpotifyId())
                .claim("accessToken", accessToken)
                .claim("userId", userDTO.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1일 후 만료
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}

