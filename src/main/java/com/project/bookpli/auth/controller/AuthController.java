package com.project.bookpli.auth.controller;

import com.project.bookpli.auth.dto.UserDTO;
import com.project.bookpli.auth.service.AuthService;
import com.project.bookpli.auth.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> handleCallback(@RequestParam String code) {
        try {
            // Spotify 인증 코드 처리 및 사용자 정보 가져오기
            UserDTO userDTO = authService.processSpotifyCallback(code);

            // JWT 생성
            String token = jwtService.createToken(userDTO.getSpotifyId());
            System.out.println("토큰 확인>>>>>>"  + token);

            // 프론트엔드로 리다이렉트 URL 생성
            String redirectUrl = String.format("http://localhost:3000/mypage/mypli?token=%s", token);

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            e.printStackTrace();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("http://localhost:3000/error"));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }


}
