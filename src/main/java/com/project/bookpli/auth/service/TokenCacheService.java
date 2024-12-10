package com.project.bookpli.auth.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenCacheService {

    private static final Logger logger = LoggerFactory.getLogger(TokenCacheService.class);

    private final Cache<String, String> tokenCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS) // 1시간 후 만료
            .maximumSize(1000) // 최대 항목 수
            .build();

    /**
     * 캐시에서 Access Token을 가져오거나, 없으면 TokenSupplier 호출
     *
     * @param spotifyId Spotify ID
     * @param tokenSupplier 토큰 공급자
     * @return Access Token
     */
    public String getAccessToken(String spotifyId, TokenSupplier tokenSupplier) {
        return tokenCache.get(spotifyId, key -> {
            logger.info("Access Token not found in cache. Fetching new token for Spotify ID: {}", spotifyId);
            return tokenSupplier.get();
        });
    }

    /**
     * Access Token 갱신
     *
     * @param spotifyId Spotify ID
     * @param accessToken 새 Access Token
     */
    public void updateAccessToken(String spotifyId, String accessToken) {
        logger.info("Updating Access Token in cache for Spotify ID: {}", spotifyId);
        tokenCache.put(spotifyId, accessToken);
    }

    /**
     * Access Token 무효화
     *
     * @param spotifyId Spotify ID
     */
    public void invalidateToken(String spotifyId) {
        logger.info("Invalidating Access Token in cache for Spotify ID: {}", spotifyId);
        tokenCache.invalidate(spotifyId);
    }

    @FunctionalInterface
    public interface TokenSupplier {
        String get();
    }
}
