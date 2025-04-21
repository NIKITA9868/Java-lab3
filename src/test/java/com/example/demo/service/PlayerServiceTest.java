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
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
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
    private PlayerDto playerDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        player = new Player();
        player.setId(1L);
        player.setName("Test Player");
        player.setBalance(100L);
        player.setTournaments(new HashSet<>());

        playerDto = new PlayerDto();
        playerDto.setId(1L);
        playerDto.setName("Test Player");
        playerDto.setBalance(100L);
    }

    @Test
    void testGetAllPlayers() {
        when(playerRepository.findAll()).thenReturn(List.of(player));

        List<PlayerDto> result = playerService.getAllPlayers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Player");
    }

    @Test
    void testGetPlayerByIdSuccess() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        PlayerDto result = playerService.getPlayerById(1L);

        assertThat(result.getName()).isEqualTo("Test Player");
    }

    @Test
    void testGetPlayerByIdNotFound() {
        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.getPlayerById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testCreatePlayer() {
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        PlayerDto result = playerService.createPlayer(playerDto);

        verify(playerCacheService).clear();
        assertThat(result.getName()).isEqualTo("Test Player");
    }

    @Test
    void testCreatePlayersBulk() {
        when(playerRepository.saveAll(anyList())).thenReturn(List.of(player));

        List<PlayerDto> result = playerService.createPlayersBulk(List.of(playerDto));

        verify(playerCacheService).clear();
        verify(tournamentCacheService).clear();
        assertThat(result).hasSize(1);
    }

    @Test
    void testCreatePlayersBulkInvalid() {
        assertThatThrownBy(() -> playerService.createPlayersBulk(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdatePlayer() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        PlayerDto result = playerService.updatePlayer(1L, playerDto);

        verify(playerCacheService).clear();
        verify(tournamentCacheService).clear();
        assertThat(result.getName()).isEqualTo("Test Player");
    }

    @Test
    void testDeletePlayer() {
        Tournament tournament = new Tournament();
        tournament.setPlayers(new HashSet<>(List.of(player)));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(tournamentRepository.findTournamentsByName("Test Player"))
                .thenReturn(List.of(tournament));

        playerService.deletePlayer(1L);

        verify(playerRepository).delete(player);
        verify(playerCacheService).clear();
        verify(tournamentCacheService).clear();
    }

    @Test
    void testFindPlayersWithBetsMoreThanSuccess() {
        when(playerCacheService.getPlayer(eq(50L), any()))
                .thenReturn(List.of(player));

        List<PlayerDto> result = playerService.findPlayersWithBetsMoreThan(50L);

        assertThat(result).hasSize(1);
    }

    @Test
    void testFindPlayersWithBetsMoreThanFail() {
        when(playerCacheService.getPlayer(eq(200L), any()))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> playerService.findPlayersWithBetsMoreThan(200L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
