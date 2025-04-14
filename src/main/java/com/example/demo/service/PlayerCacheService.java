package com.example.demo.service;

import com.example.demo.cache.CacheFactory;
import com.example.demo.cache.MyCache;
import com.example.demo.entity.Player;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class PlayerCacheService {
    private final CacheFactory cacheFactory;
    private MyCache<Long, List<Player>> playerCache;

    @PostConstruct  // Инициализируем кеш при создании бина
    public void init() {
        this.playerCache = cacheFactory.createCache(
                "playerCache",  // Название кеша
                1000,          // Максимальный размер (maxSize)
                300_000        // Время жизни записи (TTL, 5 минут в мс)
        );
    }

    public List<Player> getPlayer(Long playerId, Supplier<List<Player>> loader) {
        return playerCache.get(playerId, loader);
    }


    public void clear() {
        playerCache.clear();
    }
}