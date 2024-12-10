package com.project.bookpli.auth.service;

import com.project.bookpli.entity.User;
import com.project.bookpli.mypage.dto.UserDTO;
import com.project.bookpli.mypage.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final SpotifyApiService spotifyApiService;
    private final TokenCacheService tokenCacheService;
    private final UserRepository userRepository;


    public AuthService(SpotifyApiService spotifyApiService, TokenCacheService tokenCacheService, UserRepository userRepository, JwtService jwtService) {
        this.spotifyApiService = spotifyApiService;
        this.tokenCacheService = tokenCacheService;
        this.userRepository = userRepository;
    }

    public Map<String, Object> processSpotifyCallback(String code) {
        // 1. Access Token 및 Refresh Token 요청
        Map<String, String> tokenResponse = spotifyApiService.requestSpotifyAccessToken(code);
        String accessToken = tokenResponse.get("access_token");
        String refreshToken = tokenResponse.get("refresh_token");

        // 2. Spotify 회원 정보 조회
        UserDTO userDTO = spotifyApiService.fetchSpotifyUserProfile(accessToken);

        // 3. DB 저장 또는 업데이트
        User user = userRepository.findBySpotifyId(userDTO.getSpotifyId())
                .map(existingUser -> {
                    if (refreshToken != null && !refreshToken.equals(existingUser.getRefreshToken())) {
                        existingUser.updateRefreshToken(refreshToken);
                    }
                    return existingUser;
                })
                .orElseGet(() -> {
                    User newUser = userDTO.toEntity();
                    newUser.updateRefreshToken(refreshToken);
                    return userRepository.save(newUser);
                });

        // 4. Access Token 캐싱
        tokenCacheService.updateAccessToken(user.getSpotifyId(), accessToken);

        // 5. Map으로 userId와 spotifyId 반환
        return Map.of(
                "userId", user.getUserId(),
                "spotifyId", user.getSpotifyId()
        );
    }

}
