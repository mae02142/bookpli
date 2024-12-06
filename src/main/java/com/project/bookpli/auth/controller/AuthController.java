package com.project.bookpli.auth.controller;

import com.project.bookpli.auth.dto.AuthResponseDTO;
import com.project.bookpli.auth.dto.UserDTO;
import com.project.bookpli.auth.service.AuthService;
import com.project.bookpli.auth.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    // 1. 스포티파이 로그인 URL 반환
    @GetMapping("/login")
    public ResponseEntity<String> login() {
        String spotifyAuthUrl = "https://accounts.spotify.com/authorize" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&scope=user-read-private user-read-email";
        System.out.println("spotifyAuthUrl>>>>>>>>"+spotifyAuthUrl);
        return ResponseEntity.ok(spotifyAuthUrl); // URL 반환
    }

    // 2. 로그인 성공 후 콜백 처리
    @GetMapping("/callback")
    public void handleCallback(@RequestParam String code, HttpServletResponse response) {
        try {
            // Access Token 발급
            AuthResponseDTO authResponse = authService.processSpotifyCallback(code);
            UserDTO userDTO = authResponse.getUser();
            String accessToken = authResponse.getAccessToken();

            // JWT 생성
            String jwtToken = jwtService.createToken(userDTO, accessToken);

            // JWT를 HTTP-Only 쿠키에 저장 - 추후에 쿠키로 변경해야함
//            Cookie jwtCookie = new Cookie("token", jwtToken);
//            jwtCookie.setHttpOnly(true);
//            jwtCookie.setPath("/");
//            jwtCookie.setMaxAge(60 * 60); // 1시간 유효기간
//
//            response.addCookie(jwtCookie);

            // 프론트엔드로 리다이렉트하면서 JWT 전달
            String redirectUrl = "http://localhost:3000/mypage/mypli?token=" + jwtToken;
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "로그인 처리 중 오류가 발생했습니다.");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
