package com.project.bookpli.auth.controller;

import com.project.bookpli.auth.manager.TokenManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    private final TokenManager tokenManager;

    public TokenController(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @GetMapping("/accessToken")
    public ResponseEntity<Map<String, String>> getAccessToken(@RequestParam String spotifyId) {
        String accessToken = tokenManager.getAccessToken(spotifyId);
        System.out.println(ResponseEntity.ok(Map.of("access_token", accessToken)));
        return ResponseEntity.ok(Map.of("access_token", accessToken));
    }
}