package com.project.bookpli.auth.client;

import com.project.bookpli.auth.manager.TokenManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
    /**
     * PUT 요청 보내기
     * @param endpoint 호출할 엔드포인트
     * @param body 요청 바디
     * @return API 응답
     */
    public Map<String, Object> sendPutRequest(String endpoint, Object body) {
        String spotifyId = getSpotifyIdFromContext();
        String accessToken = tokenManager.getAccessToken(spotifyId);

        return webClient.put()
                .uri(endpoint)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    /**
     * DELETE 요청 보내기
     * @param endpoint 호출할 엔드포인트
     * @param body 요청 바디
     * @return API 응답
     */
    public void sendDeleteRequest(String endpoint, Object body) {
        String spotifyId = getSpotifyIdFromContext();
        String accessToken = tokenManager.getAccessToken(spotifyId);

        webClient.method(HttpMethod.DELETE) // DELETE 메서드 명시적 사용
                .uri(endpoint) // 요청 URL 설정
                .header("Authorization", "Bearer " + accessToken) // 인증 헤더 추가
                .header("Content-Type", "application/json") // Content-Type 헤더 추가
                .bodyValue(body) // 요청 바디 추가
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(), // 에러 상태 확인
                        response -> response.bodyToMono(String.class).map(RuntimeException::new) // 에러 처리
                )
                .toBodilessEntity() // 응답 바디가 필요 없는 경우
                .block(); // 동기 처리
    }

    public void sendDeleteRequestWithoutBody(String endpoint) {
        String spotifyId = getSpotifyIdFromContext();
        String accessToken = tokenManager.getAccessToken(spotifyId);

        webClient.method(HttpMethod.DELETE) // DELETE 메서드 명시적 사용
                .uri(endpoint) // 요청 URL 설정
                .header("Authorization", "Bearer " + accessToken) // 인증 헤더 추가
                .header("Content-Type", "application/json") // Content-Type 헤더 추가
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(), // 에러 상태 확인
                        response -> response.bodyToMono(String.class).map(RuntimeException::new) // 에러 처리
                )
                .toBodilessEntity() // 응답 바디가 필요 없는 경우
                .block(); // 동기 처리
    }
}
