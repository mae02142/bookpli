package com.project.bookpli.auth.service;

import com.project.bookpli.auth.dto.UserDTO;
import com.project.bookpli.auth.entity.User;
import com.project.bookpli.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public void processSpotifyCallback(String code) {
        // 1. Access Token 요청
        Map<String, String> tokenResponse = requestSpotifyAccessToken(code);
        String accessToken = tokenResponse.get("access_token");
        String refreshToken = tokenResponse.get("refresh_token");

        // 2. Spotify 회원 정보 조회
        UserDTO userDTO = fetchSpotifyUserProfile(accessToken);

        // 3. DB 저장
        Optional<User> existingUser = userRepository.findBySpotifyId(userDTO.getSpotifyId());

        if (existingUser.isEmpty()) {
            // 새로운 사용자 등록
            User newUser = userDTO.toEntity();
            newUser.updateRefreshToken(refreshToken);
            userRepository.save(newUser);
        }
//        else {
//            // 기존 사용자 토큰 업데이트
//            User user = existingUser.get();
//            user.updateRefreshToken(refreshToken);
//        }
    }

    private Map<String, String> requestSpotifyAccessToken(String code) {
        return webClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(buildFormData(code))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .block();
    }


    private UserDTO fetchSpotifyUserProfile(String accessToken) {
        return webClient.get()
                .uri("https://api.spotify.com/v1/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(UserDTO.class)
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
