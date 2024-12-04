package com.project.bookpli.auth.service;

import com.project.bookpli.auth.dto.UserDTO;
import com.project.bookpli.entity.User;
import com.project.bookpli.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final SpotifyApiService spotifyApiService;
    private final TokenCacheService tokenCacheService;
    private final UserRepository userRepository;

    public AuthService(SpotifyApiService spotifyApiService, TokenCacheService tokenCacheService, UserRepository userRepository) {
        this.spotifyApiService = spotifyApiService;
        this.tokenCacheService = tokenCacheService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void processSpotifyCallback(String code) {
        // 1. Access Token 요청
        Map<String, String> tokenResponse = spotifyApiService.requestSpotifyAccessToken(code);
        String accessToken = tokenResponse.get("access_token");
        String refreshToken = tokenResponse.get("refresh_token");

        // 2. Spotify 회원 정보 조회
        UserDTO userDTO = spotifyApiService.fetchSpotifyUserProfile(accessToken, UserDTO.class);

        // 3. DB 저장
        Optional<User> existingUser = userRepository.findBySpotifyId(userDTO.getSpotifyId());
        if (existingUser.isEmpty()) {
            User newUser = userDTO.toEntity();
            newUser.updateRefreshToken(refreshToken);
            userRepository.save(newUser);
        }
    }

    public String getAccessToken(String userId) {
        return tokenCacheService.getAccessToken(userId, this::fetchNewAccessToken);
    }

    private String fetchNewAccessToken(String userId) {
        String refreshToken = fetchRefreshTokenFromDB(userId);
        return spotifyApiService.refreshAccessToken(refreshToken).get("access_token");
    }

    private String fetchRefreshTokenFromDB(String userId) {
        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRefreshToken();
    }
}
