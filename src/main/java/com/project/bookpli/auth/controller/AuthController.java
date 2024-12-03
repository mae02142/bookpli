package com.project.bookpli.auth.controller;

import com.project.bookpli.auth.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> handleCallback(@RequestParam String code) {
        try {
            authService.processSpotifyCallback(code);

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("http://localhost:3000/mypage/mypli")); // 프론트엔드 URL
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
