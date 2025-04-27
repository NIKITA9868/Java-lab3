package com.example.demo.service;

import com.example.demo.cache.CacheFactory;
import com.example.demo.cache.MyCache;
import com.example.demo.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PlayerCacheServiceTest {

    private MyCache<Long, List<Player>> myCache;
    private PlayerCacheService playerCacheService;

    @BeforeEach
    void setUp() {
        CacheFactory cacheFactory = mock(CacheFactory.class);

        // 🟡 Создаём строго типизированный мок
        myCache = mock(MyCache.class);

        // 🟢 Принудительно кастуем возврат через Answer
        when(cacheFactory.createCache(anyString(), anyInt(), anyLong()))
                .thenAnswer(invocation -> myCache);

        playerCacheService = new PlayerCacheService(cacheFactory);
        playerCacheService.init();
    }

    @Test
    void testGetPlayer() {
        Long id = 42L;
        List<Player> mockPlayers = List.of(new Player());

        // 🔁 Мокаем возвращаемое значение из кеша
        when(myCache.get(eq(id), any())).thenReturn(mockPlayers);

        Supplier<List<Player>> loader = () -> List.of(new Player());
        List<Player> result = playerCacheService.getPlayer(id, loader);

        assertEquals(mockPlayers, result);
        verify(myCache).get(eq(id), any());
    }

    @Test
    void testClear() {
        playerCacheService.clear();
        verify(myCache).clear();
    }
}
