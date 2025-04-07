package com.example.demo.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Configure tournaments cache
        cacheManager.registerCustomCache("tournaments",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .recordStats()
                        .build());

        // Configure bets cache
        cacheManager.registerCustomCache("bets",
                Caffeine.newBuilder()
                        .maximumSize(200)
                        .recordStats()
                        .build());

        return cacheManager;
    }
}
