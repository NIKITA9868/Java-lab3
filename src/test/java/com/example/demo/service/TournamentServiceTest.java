package com.example.demo.service;

import com.example.demo.dto.TournamentDto;
import com.example.demo.entity.Player;
import com.example.demo.entity.Tournament;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PlayerRepository;
import com.example.demo.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TournamentCacheService tournamentCacheService;

    @InjectMocks
    private TournamentService tournamentService;

    private Tournament tournament;
    private Player player;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Подготовка объектов для тестирования
        player = new Player();
        player.setId(1L);
        player.setName("Test Player");

        tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Test Tournament");
        tournament.setPrizePool(5000L);
        tournament.setPlayers(Set.of(player));
    }

    @Test
    void testGetTournamentsByPlayerName_Found() {
        // Мокаем данные для возвращаемых турниров
        when(tournamentCacheService.getPlayerTournaments(eq("Test Player"), any())).thenReturn(List.of(tournament));

        // Вызов метода
        List<TournamentDto> result = tournamentService.getTournamentsByPlayerName("Test Player");

        // Проверка результата
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Tournament", result.get(0).getName());
        verify(tournamentCacheService).getPlayerTournaments(eq("Test Player"), any());
    }

    @Test
    void testGetTournamentsByPlayerName_CacheFallbackException() {
        String playerName = "Error Player";

        // Эмулируем промах кэша и ошибку в fallback
        when(tournamentCacheService.getPlayerTournaments(eq(playerName), any()))
                .thenAnswer(invocation -> {
                    throw new RuntimeException("Cache fallback failed");
                });

        when(tournamentRepository.findTournamentsByName(playerName))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            tournamentService.getTournamentsByPlayerName(playerName);
        });
    }

    @Test
    void testUpdateTournament_ZeroPrizePool() {
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setName("Zero Prize");
        tournamentDto.setPrizePool(0L); // Граничное значение - 0

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TournamentDto result = tournamentService.updateTournament(1L, tournamentDto);

        assertEquals(0L, result.getPrizePool()); // Проверяем что 0 допустим
    }

    @Test
    void testRegisterPlayer_MaxLongValues() {
        Tournament tournament = new Tournament();
        tournament.setId(Long.MAX_VALUE);

        Player player = new Player();
        player.setId(Long.MAX_VALUE);

        when(tournamentRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.of(player));

        assertDoesNotThrow(() -> {
            tournamentService.registerPlayer(Long.MAX_VALUE, Long.MAX_VALUE);
        });
    }

    @Test
    void testDeleteTournament_ConcurrentModification() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);

        // Эмулируем конкурентное изменение
        when(tournamentRepository.findById(1L))
                .thenReturn(Optional.of(tournament))
                .thenThrow(new RuntimeException("Concurrent modification"));

        doThrow(new RuntimeException()).when(tournamentRepository).delete(any());

        assertThrows(RuntimeException.class, () -> {
            tournamentService.deleteTournament(1L);
        });
    }

    @Test
    void testCreateTournament_MaximumNameLength() {
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setName("A".repeat(255)); // Максимальная длина
        tournamentDto.setPrizePool(1000L);

        when(tournamentRepository.save(any())).thenAnswer(invocation -> {
            Tournament t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        TournamentDto result = tournamentService.createTournament(tournamentDto);

        assertEquals(255, result.getName().length());
    }

    @Test
    void testUnregisterPlayer_PlayerNotInTournamentCache() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setPlayers(new HashSet<>()); // Пустой список игроков

        Player player = new Player();
        player.setId(1L);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            tournamentService.unregisterPlayer(1L, 1L);
        });

        assertEquals("Player is not registered for the tournament", exception.getMessage());
    }

    @Test
    void testRegisterPlayer_DuplicateRegistrationDifferentCase() {
        Player playerDifferentCase = new Player();
        playerDifferentCase.setId(1L);
        playerDifferentCase.setName("test player"); // В другом регистре

        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setPlayers(new HashSet<>());

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(playerDifferentCase));

        // Первая регистрация должна пройти успешно
        assertDoesNotThrow(() -> tournamentService.registerPlayer(1L, 1L));

        // Повторная регистрация того же игрока (но объект другой)
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            tournamentService.registerPlayer(1L, 1L);
        });

        assertEquals("Player is already registered for the tournament", exception.getMessage());
    }

    @Test
    void testGetTournamentsByPlayerName_FallbackToDatabase() {
        // 1. Кэш пустой → падаем в базу
        when(tournamentCacheService.getPlayerTournaments(eq("Test Player"), any())).thenReturn(List.of());

        // 2. В базе есть турниры
        when(tournamentRepository.findTournamentsByName("Test Player")).thenReturn(List.of(tournament));

        List<TournamentDto> result = tournamentService.getTournamentsByPlayerName("Test Player");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tournamentRepository).findTournamentsByName("Test Player"); // Проверяем вызов базы
    }

    @Test
    void testGetAllTournaments_EmptyList() {
        when(tournamentRepository.findAll()).thenReturn(List.of());

        List<TournamentDto> result = tournamentService.getAllTournaments();

        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateTournament_NullName() {
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setName(null);
        tournamentDto.setPrizePool(1000L);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            tournamentService.createTournament(tournamentDto);
        });

        assertEquals("Tournament name cannot be empty", exception.getMessage());
    }

    @Test
    void testCreateTournament_NegativePrizePool() {
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setName("Test");
        tournamentDto.setPrizePool(-100L);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            tournamentService.createTournament(tournamentDto);
        });

        assertEquals("Prize pool cannot be negative", exception.getMessage());
    }

    @Test
    void testUnregisterPlayer_TournamentNotFound() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tournamentService.unregisterPlayer(1L, 1L);
        });

        assertEquals("Tournament not found with id: 1", exception.getMessage());
    }

    @Test
    void testRegisterPlayer_PlayerNotFound() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tournamentService.registerPlayer(1L, 1L);
        });

        assertEquals("Player not found with id: 1", exception.getMessage());
    }

    @Test
    void testCreateTournament_EmptyName() {
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setName("   "); // Пробелы вместо имени
        tournamentDto.setPrizePool(1000L);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            tournamentService.createTournament(tournamentDto);
        });

        assertEquals("Tournament name cannot be empty", exception.getMessage());
    }

    @Test
    void testUpdateTournament_NegativePrizePool() {
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setName("Valid Name");
        tournamentDto.setPrizePool(-500L);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            tournamentService.updateTournament(1L, tournamentDto);
        });

        assertEquals("Prize pool cannot be negative", exception.getMessage());
    }

    @Test
    void testRegisterPlayer_TournamentCacheClear() {
        Player newPlayer = new Player();
        newPlayer.setId(2L);
        newPlayer.setName("New Player");

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(2L)).thenReturn(Optional.of(newPlayer));

        tournamentService.registerPlayer(1L, 2L);

        // Проверяем, что кэш был очищен после регистрации
        verify(tournamentCacheService).clear();
    }

    @Test
    void testDeleteTournament_ClearsPlayerRelationships() {
        Tournament tournamentWithPlayers = new Tournament();
        tournamentWithPlayers.setId(1L);
        tournamentWithPlayers.setName("Tournament with Players");
        tournamentWithPlayers.setPlayers(Set.of(player));

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentWithPlayers));

        tournamentService.deleteTournament(1L);

        // Проверяем, что у игрока удалилась связь с турниром
        assertFalse(player.getTournaments().contains(tournamentWithPlayers));
        verify(tournamentCacheService).clear();
    }

    @Test
    void testGetTournamentsByPlayerName_CacheMissUsesFallback() {
        String playerName = "Cache Miss Player";

        // Эмулируем промах кэша
        when(tournamentCacheService.getPlayerTournaments(eq(playerName), any()))
                .thenAnswer(invocation -> {
                    // Вызываем fallback функцию
                    return ((List<Tournament>) invocation.getArgument(1)).stream().toList();
                });

        // Настраиваем fallback (репозиторий)
        when(tournamentRepository.findTournamentsByName(playerName))
                .thenReturn(List.of(tournament));

        List<TournamentDto> result = tournamentService.getTournamentsByPlayerName(playerName);

        assertEquals(1, result.size());
        verify(tournamentRepository).findTournamentsByName(playerName);
    }

    @Test
    void testUpdateTournament_PartialUpdate() {
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setName("Updated Name Only"); // Не обновляем prizePool

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TournamentDto result = tournamentService.updateTournament(1L, tournamentDto);

        assertEquals("Updated Name Only", result.getName());
        assertEquals(5000L, result.getPrizePool()); // Старое значение сохранилось
    }

    @Test
    void testUnregisterPlayer_CacheClear() {
        // Настроим, чтобы игрок был зарегистрирован
        Tournament tournamentWithPlayer = new Tournament();
        tournamentWithPlayer.setId(1L);
        tournamentWithPlayer.setPlayers(Set.of(player));

        player.getTournaments().add(tournamentWithPlayer);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournamentWithPlayer));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        tournamentService.unregisterPlayer(1L, 1L);

        verify(tournamentCacheService).clear();
    }

    @Test
    void testDeleteTournament_NotFound() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tournamentService.deleteTournament(1L);
        });

        assertEquals("Tournament not found with id: 1", exception.getMessage());
    }

    @Test
    void testUpdateTournament_NullFieldsKeepsOldValues() {
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setName(null); // Не передали название
        tournamentDto.setPrizePool(0); // Не передали призовой фонд

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any())).thenReturn(tournament);

        TournamentDto result = tournamentService.updateTournament(1L, tournamentDto);

        assertEquals("Test Tournament", result.getName()); // Старое название сохранилось
        assertEquals(5000L, result.getPrizePool()); // Старый призовой фонд сохранился
    }

    @Test
    void testGetTournamentsByPlayerName_NotFound() {
        // Мокаем отсутствие турниров для игрока
        when(tournamentCacheService.getPlayerTournaments(eq("Test Player"), any())).thenReturn(List.of());

        // Вызов метода и проверка исключения
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tournamentService.getTournamentsByPlayerName("Test Player");
        });

        assertEquals("No tournaments found for player with name: Test Player", exception.getMessage());
    }

    @Test
    void testGetAllTournaments() {
        // Мокаем список всех турниров
        when(tournamentRepository.findAll()).thenReturn(List.of(tournament));

        // Вызов метода
        List<TournamentDto> result = tournamentService.getAllTournaments();

        // Проверка результата
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tournamentRepository).findAll();
    }

    @Test
    void testGetTournamentById_Found() {
        // Мокаем существующий турнир
        when(tournamentRepository.findById(1L)).thenReturn(java.util.Optional.of(tournament));

        // Вызов метода
        TournamentDto result = tournamentService.getTournamentById(1L);

        // Проверка результата
        assertNotNull(result);
        assertEquals("Test Tournament", result.getName());
        verify(tournamentRepository).findById(1L);
    }

    @Test
    void testGetTournamentById_NotFound() {
        // Мокаем отсутствие турнира
        when(tournamentRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Вызов метода и проверка исключения
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tournamentService.getTournamentById(1L);
        });

        assertEquals("Tournament not found with id: 1", exception.getMessage());
    }

    @Test
    void testCreateTournament() {
        // Мокаем создание турнира
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setName("New Tournament");
        tournamentDto.setPrizePool(10000L);

        Tournament newTournament = new Tournament();
        newTournament.setId(2L);
        newTournament.setName(tournamentDto.getName());
        newTournament.setPrizePool(tournamentDto.getPrizePool());

        when(tournamentRepository.save(any())).thenReturn(newTournament);

        // Вызов метода
        TournamentDto result = tournamentService.createTournament(tournamentDto);

        // Проверка результата
        assertNotNull(result);
        assertEquals("New Tournament", result.getName());
        assertEquals(10000L, result.getPrizePool());
        verify(tournamentRepository).save(any());
    }

    @Test
    void testCreateTournament_BadRequest() {
        // Мокаем создание турнира с некорректным названием
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setName("");
        tournamentDto.setPrizePool(10000L);

        // Вызов метода и проверка исключения
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            tournamentService.createTournament(tournamentDto);
        });

        assertEquals("Tournament name cannot be empty", exception.getMessage());
    }

    @Test
    void testUpdateTournament_Found() {
        // Мокаем существующий турнир
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setName("Updated Tournament");
        tournamentDto.setPrizePool(15000L);

        when(tournamentRepository.findById(1L)).thenReturn(java.util.Optional.of(tournament));
        when(tournamentRepository.save(any())).thenReturn(tournament);

        // Вызов метода
        TournamentDto result = tournamentService.updateTournament(1L, tournamentDto);

        // Проверка результата
        assertNotNull(result);
        assertEquals("Updated Tournament", result.getName());
        assertEquals(15000L, result.getPrizePool());
        verify(tournamentRepository).findById(1L);
        verify(tournamentRepository).save(any());
        verify(tournamentCacheService).clear();
    }

    @Test
    void testUpdateTournament_NotFound() {
        // Мокаем отсутствие турнира
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setName("Updated Tournament");
        tournamentDto.setPrizePool(15000L);

        when(tournamentRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Вызов метода и проверка исключения
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tournamentService.updateTournament(1L, tournamentDto);
        });

        assertEquals("Tournament not found with id: 1", exception.getMessage());
    }

    @Test
    void testDeleteTournament() {
        // Мокаем существующий турнир
        when(tournamentRepository.findById(1L)).thenReturn(java.util.Optional.of(tournament));

        // Вызов метода
        tournamentService.deleteTournament(1L);

        // Проверка вызовов
        verify(tournamentRepository).findById(1L);
        verify(tournamentRepository).delete(any());
        verify(tournamentCacheService).clear();
    }

    @Test
    void testRegisterPlayer() {
        // Мокаем регистрацию игрока в турнир
        when(tournamentRepository.findById(1L)).thenReturn(java.util.Optional.of(tournament));
        when(playerRepository.findById(1L)).thenReturn(java.util.Optional.of(player));

        // Вызов метода
        TournamentDto result = tournamentService.registerPlayer(1L, 1L);

        // Проверка результата
        assertNotNull(result);
        assertTrue(result.getPlayers().contains(player));
        verify(tournamentRepository).findById(1L);
        verify(playerRepository).findById(1L);
        verify(tournamentRepository).save(any());
        verify(playerRepository).save(any());
        verify(tournamentCacheService).clear();
    }

    @Test
    void testRegisterPlayer_AlreadyRegistered() {
        // Мокаем, что игрок уже зарегистрирован
        when(tournamentRepository.findById(1L)).thenReturn(java.util.Optional.of(tournament));
        when(playerRepository.findById(1L)).thenReturn(java.util.Optional.of(player));

        // Вызов метода и проверка исключения
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            tournamentService.registerPlayer(1L, 1L);
        });

        assertEquals("Player is already registered for the tournament", exception.getMessage());
    }

    @Test
    void testUnregisterPlayer() {
        // Мокаем удаление игрока из турнира
        when(tournamentRepository.findById(1L)).thenReturn(java.util.Optional.of(tournament));
        when(playerRepository.findById(1L)).thenReturn(java.util.Optional.of(player));

        // Вызов метода
        TournamentDto result = tournamentService.unregisterPlayer(1L, 1L);

        // Проверка результата
        assertNotNull(result);
        assertFalse(result.getPlayers().contains(player));
        verify(tournamentRepository).findById(1L);
        verify(playerRepository).findById(1L);
        verify(tournamentRepository).save(any());
        verify(playerRepository).save(any());
        verify(tournamentCacheService).clear();
    }

    @Test
    void testUnregisterPlayer_NotRegistered() {
        // Мокаем, что игрок не зарегистрирован
        when(tournamentRepository.findById(1L)).thenReturn(java.util.Optional.of(tournament));
        when(playerRepository.findById(1L)).thenReturn(java.util.Optional.of(player));

        // Вызов метода и проверка исключения
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            tournamentService.unregisterPlayer(1L, 1L);
        });

        assertEquals("Player is not registered for the tournament", exception.getMessage());
    }
}
