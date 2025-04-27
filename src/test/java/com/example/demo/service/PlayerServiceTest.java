package com.example.demo.service;

import com.example.demo.dto.PlayerDto;
import com.example.demo.entity.Player;
import com.example.demo.entity.Tournament;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PlayerRepository;
import com.example.demo.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private PlayerCacheService playerCacheService;

    @Mock
    private TournamentCacheService tournamentCacheService;

    @InjectMocks
    private PlayerService playerService;

    private Player player;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Подготовка игрока для тестов
        player = new Player();
        player.setId(1L);
        player.setName("Test Player");
        player.setBalance(1000L);
    }

    @Test
    void testFindPlayersWithBetsMoreThan_Found() {
        // Мокаем поведение кэша
        List<Player> mockPlayers = List.of(player);
        when(playerCacheService.getPlayer(eq(100L), any())).thenReturn(mockPlayers);

        // Вызов метода
        List<PlayerDto> result = playerService.findPlayersWithBetsMoreThan(100L);

        // Проверка результата
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(playerCacheService).getPlayer(eq(100L), any());
    }

    @Test
    void testFindPlayersWithBetsMoreThan_NotFound() {
        // Мокаем пустой результат из кэша
        when(playerCacheService.getPlayer(eq(100L), any())).thenReturn(List.of());

        // Вызов метода и проверка исключения
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            playerService.findPlayersWithBetsMoreThan(100L);
        });

        assertEquals("No players found with bets more than 100", exception.getMessage());
    }

    @Test
    void testGetAllPlayers() {
        // Мокаем список всех игроков
        List<Player> players = List.of(player);
        when(playerRepository.findAll()).thenReturn(players);

        // Вызов метода
        List<PlayerDto> result = playerService.getAllPlayers();

        // Проверка результата
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(playerRepository).findAll();
    }

    @Test
    void testGetPlayerById_Found() {
        // Мокаем поведение репозитория
        when(playerRepository.findById(1L)).thenReturn(java.util.Optional.of(player));

        // Вызов метода
        PlayerDto result = playerService.getPlayerById(1L);

        // Проверка результата
        assertNotNull(result);
        assertEquals("Test Player", result.getName());
        verify(playerRepository).findById(1L);
    }

    @Test
    void testGetPlayerById_NotFound() {
        // Мокаем отсутствие игрока
        when(playerRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Вызов метода и проверка исключения
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            playerService.getPlayerById(1L);
        });

        assertEquals("Player not found with id: 1", exception.getMessage());
    }

    @Test
    void testCreatePlayersBulk() {
        // Мокаем сохранение игроков
        PlayerDto playerDto = new PlayerDto();
        playerDto.setName("Test Player");
        playerDto.setBalance(1000L);

        Player savedPlayer = new Player();
        savedPlayer.setName(playerDto.getName());
        savedPlayer.setBalance(playerDto.getBalance());

        when(playerRepository.saveAll(anyList())).thenReturn(List.of(savedPlayer));


        // Вызов метода
        List<PlayerDto> result = playerService.createPlayersBulk(List.of(playerDto));

        // Проверка результата
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Player", result.get(0).getName());
        verify(playerRepository).saveAll(anyList());
        verify(tournamentCacheService).clear();
    }

    @Test
    void testFindPlayersWithBetsMoreThan_FallbackToDatabase() {
        // 1. Кэш пустой → падаем в базу
        when(playerCacheService.getPlayer(eq(100L), any())).thenReturn(List.of());

        // 2. В базе есть игроки
        when(playerRepository.findPlayersWithBetsGreaterThan(100L)).thenReturn(List.of(player));

        List<PlayerDto> result = playerService.findPlayersWithBetsMoreThan(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(playerRepository).findPlayersWithBetsGreaterThan(100L); // Проверяем вызов базы
    }

    @Test
    void testDeletePlayer_NoTournaments() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(tournamentRepository.findTournamentsByName(player.getName())).thenReturn(List.of());

        playerService.deletePlayer(1L);

        verify(tournamentRepository).findTournamentsByName(player.getName());
        verify(playerRepository).delete(player);
    }

    @Test
    void testUpdatePlayer_NullBalance_KeepsOldValue() {
        PlayerDto playerDto = new PlayerDto();
        playerDto.setName("Updated Player");
        playerDto.setBalance(0); // Не передали баланс

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(playerRepository.save(any())).thenReturn(player);

        PlayerDto result = playerService.updatePlayer(1L, playerDto);

        assertEquals(1000L, result.getBalance()); // Старый баланс сохранился
    }

    @Test
    void testCreatePlayersBulk_EmptyList() {
        assertThrows(IllegalArgumentException.class, () -> {
            playerService.createPlayersBulk(List.of());
        });
    }

    @Test
    void testGetAllPlayers_EmptyList() {
        when(playerRepository.findAll()).thenReturn(List.of());

        List<PlayerDto> result = playerService.getAllPlayers();

        assertTrue(result.isEmpty());
    }

    @Test
    void testCreatePlayer() {
        // Мокаем сохранение игрока
        PlayerDto playerDto = new PlayerDto();
        playerDto.setName("Test Player");
        playerDto.setBalance(1000L);

        Player savedPlayer = new Player();
        savedPlayer.setName(playerDto.getName());
        savedPlayer.setBalance(playerDto.getBalance());

        when(playerRepository.save(any())).thenReturn(savedPlayer);

        // Вызов метода
        PlayerDto result = playerService.createPlayer(playerDto);

        // Проверка результата
        assertNotNull(result);
        assertEquals("Test Player", result.getName());
        verify(playerRepository).save(any());
        verify(playerCacheService).clear();
    }

    @Test
    void testUpdatePlayer_Found() {
        // Мокаем существующего игрока
        PlayerDto playerDto = new PlayerDto();
        playerDto.setName("Updated Player");
        playerDto.setBalance(1200L);

        when(playerRepository.findById(1L)).thenReturn(java.util.Optional.of(player));
        when(playerRepository.save(any())).thenReturn(player);

        // Вызов метода
        PlayerDto result = playerService.updatePlayer(1L, playerDto);

        // Проверка результата
        assertNotNull(result);
        assertEquals("Updated Player", result.getName());
        verify(playerRepository).findById(1L);
        verify(playerRepository).save(any());
        verify(playerCacheService).clear();
        verify(tournamentCacheService).clear();
    }

    @Test
    void testUpdatePlayer_NotFound() {
        // Мокаем отсутствие игрока
        PlayerDto playerDto = new PlayerDto();
        playerDto.setName("Updated Player");
        playerDto.setBalance(1200L);

        when(playerRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Вызов метода и проверка исключения
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            playerService.updatePlayer(1L, playerDto);
        });

        assertEquals("Player not found with id: 1", exception.getMessage());
    }

    @Test
    void testDeletePlayer() {
        // Мокаем существующего игрока и турниры
        when(playerRepository.findById(1L)).thenReturn(java.util.Optional.of(player));
        when(tournamentRepository.findTournamentsByName(player.getName())).thenReturn(List.of(new Tournament()));

        // Вызов метода
        playerService.deletePlayer(1L);

        // Проверка вызовов
        verify(playerRepository).findById(1L);
        verify(tournamentRepository).findTournamentsByName(player.getName());
        verify(playerRepository).delete(any());
        verify(playerCacheService).clear();
        verify(tournamentCacheService).clear();
    }
}
