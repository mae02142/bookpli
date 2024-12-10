package com.project.bookpli.auth.service;

import com.project.bookpli.entity.User;
import com.project.bookpli.mypage.dto.UserDTO;
import com.project.bookpli.mypage.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class SpotifyApiService {

    private static final String SPOTIFY_TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String SPOTIFY_PROFILE_URL = "https://api.spotify.com/v1/me";

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    private final WebClient webClient;
    private final UserRepository userRepository;

    public SpotifyApiService(WebClient.Builder webClientBuilder, UserRepository userRepository) {
        this.webClient = webClientBuilder.build();
        this.userRepository = userRepository;
    }

    public Map<String, String> requestSpotifyAccessToken(String code) {
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        return webClient.post()
                .uri(SPOTIFY_TOKEN_URL)
                .header("Authorization", "Basic " + credentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(buildFormData(code))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to fetch Spotify access token"));
    }

    public Map<String, String> refreshAccessToken(String refreshToken) {
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        return webClient.post()
                .uri(SPOTIFY_TOKEN_URL)
                .header("Authorization", "Basic " + credentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(buildRefreshTokenFormData(refreshToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to refresh Spotify access token"));
    }

    public UserDTO fetchSpotifyUserProfile(String accessToken) {
        return webClient.get()
                .uri(SPOTIFY_PROFILE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to fetch Spotify user profile"));
    }

    private MultiValueMap<String, String> buildFormData(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);
        return formData;
    }

    private MultiValueMap<String, String> buildRefreshTokenFormData(String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);
        return formData;
    }

    public String getRefreshToken(String spotifyId) {
        return userRepository.findBySpotifyId(spotifyId)
                .map(User::getRefreshToken) // User 객체에서 Refresh Token을 가져옴
                .orElseThrow(() -> new RuntimeException("Refresh Token not found for Spotify ID: " + spotifyId));
    }
}
