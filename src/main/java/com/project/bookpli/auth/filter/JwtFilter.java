package com.project.bookpli.auth.filter;

import com.project.bookpli.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = extractJwtFromCookies(request); // JWT 추출

        if (jwt != null) {
            try {
                // JWT 검증 및 클레임 추출
                System.out.println("doFilterInternal에서 토큰 검증");
                Claims claims = jwtService.verifyToken(jwt);

                // SecurityContext에 인증 정보 설정
                setAuthentication(claims);

                log.debug("JWT 검증 성공, SecurityContext에 사용자 인증 정보 저장 완료.");
            } catch (Exception e) {
                log.warn("JWT 검증 실패: {}", e.getMessage());
            }
        } else {
            log.debug("JWT 토큰이 존재하지 않습니다.");
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * 쿠키에서 JWT 추출
     */
    private String extractJwtFromCookies(HttpServletRequest request) {
        // 1. Authorization 헤더에서 먼저 토큰 추출
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // "Bearer " 접두사 제거
        }

        // 2. 쿠키에서 토큰 추출 (기존 로직 유지)
        return Arrays.stream(request.getCookies() != null ? request.getCookies() : new Cookie[0])
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * SecurityContext에 사용자 인증 정보 저장
     */
    private void setAuthentication(Claims claims) {
        // 사용자 정보 및 권한 추출
        String spotifyId = claims.get("spotifyId", String.class);
        String roles = claims.get("roles", String.class); // JWT에 roles 정보가 있는 경우
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(spotifyId, null, null);

        // SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
