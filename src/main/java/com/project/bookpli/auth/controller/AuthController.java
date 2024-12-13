package com.project.bookpli.auth.controller;

import com.project.bookpli.auth.service.AuthService;
import com.project.bookpli.auth.service.JwtService;
import com.project.bookpli.common.exception.BaseException;
import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.common.response.BaseResponseStatus;
import com.project.bookpli.entity.User;
import com.project.bookpli.mypage.service.MypageService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final MypageService mypageService;

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    public AuthController(AuthService authService, JwtService jwtService, MypageService mypageService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.mypageService = mypageService;
    }

    // 1. 스포티파이 로그인 URL 반환
    @GetMapping("/login")
    public ResponseEntity<String> login() {
        String spotifyAuthUrl = "https://accounts.spotify.com/authorize" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&scope=user-read-private user-read-email playlist-read-private streaming user-read-playback-state user-modify-playback-state user-read-currently-playing user-read-recently-played playlist-modify-private "+
                "playlist-modify-public";
        return ResponseEntity.ok(spotifyAuthUrl); // URL 반환
    }

    // 2. 로그인 성공 후 콜백 처리
    @GetMapping("/callback")
    public void handleCallback(@RequestParam String code, HttpServletResponse response) {
        try {
            // Access Token 발급 및 사용자 정보 처리
            Map<String, Object> userInfo = authService.processSpotifyCallback(code);

            // jwt 토큰 생성
            String jwt = jwtService.createJwtToken(userInfo);

            // 3. JWT를 쿠키에 저장
            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setHttpOnly(true); // XSS 방지
            cookie.setSecure(false); // HTTPS에서만 작동 (로컬 개발 시 false로 설정 가능)
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60); // 쿠키 유효 기간: 1시간
            response.addCookie(cookie);
            log.debug("JWT 쿠키 생성: {}", cookie);

            response.sendRedirect("http://localhost:3000/mypage/mypli");

        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "로그인 처리 중 오류가 발생했습니다.");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @GetMapping("/user-info")
    public BaseResponse<Map<String, Object>> getUserInfo(@CookieValue(value = "jwt", required = false) String jwt) {
        if (jwt == null || jwt.isEmpty()) {
            // JWT가 없으면 에러 응답 반환
            return new BaseResponse<>(BaseResponseStatus.JWT_NOT_FOUND);
        }

        // JWT 검증 및 디코딩
        Map<String, Object> decodedToken = jwtService.verifyToken(jwt);

        // 사용자 정보 조회
        String spotifyId = (String) decodedToken.get("spotifyId");
        User user = mypageService.findBySpotifyId(spotifyId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));

        // 필요한 사용자 정보만 반환
        Map<String, Object> userInfo = Map.of(
                "spotifyId", user.getSpotifyId(),
                "userId", user.getUserId()
        );

        return new BaseResponse<>(userInfo);

    }
}