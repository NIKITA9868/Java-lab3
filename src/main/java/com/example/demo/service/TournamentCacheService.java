package com.example.demo.service;

import com.example.demo.cache.CacheFactory;
import com.example.demo.cache.MyCache;
import com.example.demo.entity.Tournament;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class TournamentCacheService {


    private final CacheFactory cacheFactory;
    private MyCache<String, List<Tournament>> playerTournamentsCache;

    @PostConstruct  // Инициализируем кеш при создании бина
    public void init() {
        this.playerTournamentsCache = cacheFactory.createCache(
                "tournamentCache",  // Название кеша
                1000,          // Максимальный размер (maxSize)
                300_000        // Время жизни записи (TTL, 5 минут в мс)
        );
    }

    public List<Tournament> getPlayerTournaments(String playerId,
                                                 Supplier<List<Tournament>> loader) {
        return playerTournamentsCache.get(playerId, loader);
    }


    public void clear() {
        playerTournamentsCache.clear();
    }
}