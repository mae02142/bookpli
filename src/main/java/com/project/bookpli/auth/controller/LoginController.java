package com.project.bookpli.auth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    @GetMapping("/login")
    public ResponseEntity<Void> spotifyLogin() {
        System.out.println("clientId: " + clientId);
        System.out.println("redirectUri: " + redirectUri);

        System.out.println(">>>>>>>>>>>>>>>>>>확인");
        String scope = "user-read-private user-read-email";
        String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        String encodedScope = URLEncoder.encode(scope, StandardCharsets.UTF_8);

        // Spotify 인증 URL 생성
        String spotifyAuthUrl = String.format(
                "https://accounts.spotify.com/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=%s",
                clientId,
                encodedRedirectUri,
                encodedScope
        );

        System.out.println(">>>>>>>>>>>>Spotify 인증 URL 생성 : " + spotifyAuthUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(spotifyAuthUrl)); // 리다이렉트
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }


}
