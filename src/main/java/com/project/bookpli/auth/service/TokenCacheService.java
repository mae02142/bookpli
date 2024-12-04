package com.project.bookpli.auth.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenCacheService {

    private final Cache<String, String> tokenCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS) // 1시간 후 캐시 만료
            .maximumSize(1000) // 최대 캐시 항목 수
            .build();

    public String getAccessToken(String userId, TokenSupplier tokenSupplier) {
        return tokenCache.get(userId, tokenSupplier::get);
    }

    public void invalidateToken(String userId) {
        tokenCache.invalidate(userId);
    }

    @FunctionalInterface
    public interface TokenSupplier {
        String get(String userId);
    }
}
