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
    public UserDTO processSpotifyCallback(String code) {
        // 1. Access Token 및 Refresh Token 요청
        System.out.println("코드 확인>>>>>>>>>>>>>>>>" + code);
        Map<String, String> tokenResponse = spotifyApiService.requestSpotifyAccessToken(code);
        String accessToken = tokenResponse.get("access_token");
        String refreshToken = tokenResponse.get("refresh_token");

        // 2. Spotify 회원 정보 조회
        UserDTO userDTO = spotifyApiService.fetchSpotifyUserProfile(accessToken);
        System.out.println("회원정보조회>>>>>>>>>>>>>>" + userDTO);
        System.out.println("엑세스 토큰>>>>>>>>>>>>>>" + accessToken);

        // 3. DB 저장 또는 업데이트
        User user = userRepository.findBySpotifyId(userDTO.getSpotifyId())
                .map(existingUser -> {
                    // 새로 받은 Refresh Token이 null이 아니고 기존 값과 다를 경우에만 업데이트
                    if (refreshToken != null && !refreshToken.equals(existingUser.getRefreshToken())) {
                        existingUser.updateRefreshToken(refreshToken);
                    }
                    return existingUser;
                })
                .orElseGet(() -> {
                    // 신규 사용자 생성 및 저장
                    User newUser = userDTO.toEntity();
                    newUser.updateRefreshToken(refreshToken);
                    return userRepository.save(newUser);
                });

        // 4. Access Token 캐싱
        tokenCacheService.getAccessToken(user.getSpotifyId(), id -> accessToken);

        // 5. DTO 반환
        return UserDTO.fromEntity(user);
    }

    public String getAccessToken(String spotifyId) {
        return tokenCacheService.getAccessToken(spotifyId, this::fetchNewAccessToken);
    }

    private String fetchNewAccessToken(String spotifyId) {
        String refreshToken = fetchRefreshTokenFromDB(spotifyId);
        return spotifyApiService.refreshAccessToken(refreshToken).get("access_token");
    }

    private String fetchRefreshTokenFromDB(String spotifyId) {
        User user = userRepository.findBySpotifyId(spotifyId)
                .orElseThrow(() -> new RuntimeException("User not found: " + spotifyId));
        return user.getRefreshToken();
    }
}
