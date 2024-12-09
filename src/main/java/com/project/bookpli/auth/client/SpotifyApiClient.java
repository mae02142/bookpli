package com.project.bookpli.auth.client;

import com.project.bookpli.auth.manager.TokenManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class SpotifyApiClient {

    private final WebClient webClient;
    private final TokenManager tokenManager;

    public SpotifyApiClient(WebClient.Builder webClientBuilder, TokenManager tokenManager) {
        this.webClient = webClientBuilder.baseUrl("https://api.spotify.com/v1").build();
        this.tokenManager = tokenManager;
    }

    /**
     * SecurityContext에서 Spotify ID 가져오기
     * @return Spotify ID
     */
    private String getSpotifyIdFromContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            return (String) principal;
        }
        throw new IllegalStateException("Unauthenticated user.");
    }

    /**
     * GET 요청 보내기
     * @param endpoint 호출할 엔드포인트
     * @return API 응답
     */
    public Map<String, Object> sendGetRequest(String endpoint) {
        String spotifyId = getSpotifyIdFromContext();
        String accessToken = tokenManager.getAccessToken(spotifyId);

        return webClient.get()
                .uri(endpoint)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    /**
     * POST 요청 보내기
     * @param endpoint 호출할 엔드포인트
     * @param body 요청 바디
     * @return API 응답
     */
    public Map<String, Object> sendPostRequest(String endpoint, Object body) {
        String spotifyId = getSpotifyIdFromContext();
        String accessToken = tokenManager.getAccessToken(spotifyId);

        return webClient.post()
                .uri(endpoint)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}
