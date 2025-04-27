package com.example.demo.service;

import com.example.demo.cache.CacheFactory;
import com.example.demo.cache.MyCache;
import com.example.demo.entity.Tournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TournamentCacheServiceTest {

    private CacheFactory cacheFactory;
    private MyCache<String, List<Tournament>> myCache;
    private TournamentCacheService tournamentCacheService;

    @BeforeEach
    void setUp() {
        cacheFactory = mock(CacheFactory.class);

        // 🟡 Создаём строго типизированный мок
        myCache = mock(MyCache.class);

        // 🟢 Принудительно кастуем возврат через Answer
        when(cacheFactory.createCache(anyString(), anyInt(), anyLong()))
                .thenAnswer(invocation -> myCache);

        tournamentCacheService = new TournamentCacheService(cacheFactory);
        tournamentCacheService.init();
    }

    @Test
    void testGetPlayerTournaments_ReturnsCachedValue() {
        String playerId = "player123";
        List<Tournament> mockTournaments = List.of(new Tournament());

        // 🔁 Мокаем возвращаемое значение из кеша
        when(myCache.get(eq(playerId), any())).thenReturn(mockTournaments);

        Supplier<List<Tournament>> loader = () -> List.of(new Tournament());
        List<Tournament> result = tournamentCacheService.getPlayerTournaments(playerId, loader);

        assertEquals(mockTournaments, result);
        verify(myCache).get(eq(playerId), any());
    }

    @Test
    void testClear_ClearsCache() {
        tournamentCacheService.clear();
        verify(myCache).clear();
    }
}
