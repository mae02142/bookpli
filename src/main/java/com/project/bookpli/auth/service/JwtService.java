package com.project.bookpli.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

    private final String secretKeyString;

    public JwtService(@Value("${CUSTOM_JWT_SECRETKEY}") String secretKeyString) {
        this.secretKeyString = secretKeyString;
    }

    /**
     * 비밀키 생성
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = secretKeyString.getBytes();
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("비밀키는 최소 256비트(32바이트) 이상이어야 합니다.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT 생성
     */
    public String createJwtToken(Map<String, Object> userInfo) {
        String jwt = Jwts.builder()
                .setClaims(userInfo)
                .setExpiration(new Date(System.currentTimeMillis() + (60 * 60 * 1000))) // Expiration: 60 minutes
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }

    /**
     * JWT 검증 및 클레임 반환
     */
    public Claims verifyToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 JWT 토큰");
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT가 만료되었습니다: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException | IllegalArgumentException e) {
            log.error("JWT가 유효하지 않습니다: {}", e.getMessage());
            throw e;
        }
    }
}
