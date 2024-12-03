package com.project.bookpli.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    private final WebClient webClient;
    private final UserRepository userRepository;

    public AuthService(WebClient.Builder webClientBuilder, UserRepository userRepository) {
        this.webClient = webClientBuilder.build();
        this.userRepository = userRepository;
    }

    public void processSpotifyCallback(String code) {
        // 1. Access Token 요청
        Map<String, String> tokenResponse = requestSpotifyAccessToken(code);
        String accessToken = tokenResponse.get("access_token");
        String refreshToken = tokenResponse.get("refresh_token");

        // 2. 회원 정보 조회
        SpotifyUser spotifyUser = fetchSpotifyUserProfile(accessToken);

        // 3. DB 저장
        Optional<User> existingUser = userRepository.findBySpotifyUserId(spotifyUser.getId());

        if (existingUser.isEmpty()) {
            // 새로운 사용자 등록
            User newUser = new User();
            newUser.setSpotifyUserId(spotifyUser.getId());
            newUser.setDisplayName(spotifyUser.getDisplayName());
            newUser.setEmail(spotifyUser.getEmail());
            newUser.setRefreshToken(refreshToken);
            userRepository.save(newUser);
        } else {
            // 기존 사용자 토큰 업데이트
            User user = existingUser.get();
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        }
    }

    private Map<String, String> requestSpotifyAccessToken(String code) {
        return webClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .bodyValue(buildFormData(code))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    private SpotifyUser fetchSpotifyUserProfile(String accessToken) {
        return webClient.get()
                .uri("https://api.spotify.com/v1/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(SpotifyUser.class)
                .block();
    }

    private MultiValueMap<String, String> buildFormData(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        return formData;
    }
}
