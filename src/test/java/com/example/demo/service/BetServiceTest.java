package com.example.demo.service;

import com.example.demo.dto.BetDto;
import com.example.demo.entity.Bet;
import com.example.demo.entity.Player;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BetRepository;
import com.example.demo.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetServiceTest {

    @Mock
    private BetRepository betRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerCacheService playerCacheService;

    @InjectMocks
    private BetService betService;

    @Test
    void getAllBets_ShouldReturnListOfBets() {
        // Arrange
        Bet bet = new Bet();
        when(betRepository.findAll()).thenReturn(Collections.singletonList(bet));

        // Act
        List<BetDto> result = betService.getAllBets();

        // Assert
        assertFalse(result.isEmpty());
        verify(betRepository, times(1)).findAll();
    }

    @Test
    void getBetsByPlayerId_WhenPlayerExists_ShouldReturnBets() {
        // Arrange
        Long playerId = 1L;
        when(playerRepository.existsById(playerId)).thenReturn(true);
        when(betRepository.findByPlayerId(playerId)).thenReturn(Collections.singletonList(new Bet()));

        // Act
        List<BetDto> result = betService.getBetsByPlayerId(playerId);

        // Assert
        assertFalse(result.isEmpty());
        verify(playerRepository, times(1)).existsById(playerId);
    }

    @Test
    void getBetsByPlayerId_WhenPlayerNotExists_ShouldThrowException() {
        // Arrange
        Long playerId = 1L;
        when(playerRepository.existsById(playerId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> betService.getBetsByPlayerId(playerId));
    }

    @Test
    void createBet_WhenPlayerExistsAndHasBalance_ShouldCreateBet() {
        // Arrange
        Long playerId = 1L;
        BetDto betDto = new BetDto();
        betDto.setAmount(100.0);

        Player player = new Player();
        player.setId(playerId);
        player.setBalance(200.0);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(betRepository.save(any(Bet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BetDto result = betService.createBet(playerId, betDto);

        // Assert
        assertNotNull(result);
        assertEquals(100.0, player.getBalance()); // 200 - 100 = 100
        verify(playerRepository, times(1)).save(player);
        verify(playerCacheService, times(1)).clear();
    }

    @Test
    void createBet_WhenPlayerNotExists_ShouldThrowException() {
        // Arrange
        Long playerId = 1L;
        BetDto betDto = new BetDto();
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> betService.createBet(playerId, betDto));
    }

    @Test
    void createBet_WhenInsufficientBalance_ShouldThrowException() {
        // Arrange
        Long playerId = 1L;
        BetDto betDto = new BetDto();
        betDto.setAmount(200.0);

        Player player = new Player();
        player.setId(playerId);
        player.setBalance(100.0);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class, () -> betService.createBet(playerId, betDto));
    }

    @Test
    void deleteBet_WhenPlayerAndBetExist_ShouldDeleteBetAndReturnMoney() {
        // Arrange
        Long playerId = 1L;
        Long betId = 1L;
        double betAmount = 100.0;

        Player player = new Player();
        player.setId(playerId);
        player.setBalance(50.0);

        Bet bet = new Bet();
        bet.setId(betId);
        bet.setAmount(betAmount);
        bet.setPlayer(player);

        when(playerRepository.existsById(playerId)).thenReturn(true);
        when(betRepository.findById(betId)).thenReturn(Optional.of(bet));

        // Act
        betService.deleteBet(playerId, betId);

        // Assert
        assertEquals(150.0, player.getBalance()); // 50 + 100 = 150
        verify(playerRepository, times(1)).save(player);
        verify(betRepository, times(1)).delete(bet);
        verify(playerCacheService, times(1)).clear();
    }

    @Test
    void deleteBet_WhenPlayerNotExists_ShouldThrowException() {
        // Arrange
        Long playerId = 1L;
        Long betId = 1L;
        when(playerRepository.existsById(playerId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> betService.deleteBet(playerId, betId));
    }

    @Test
    void deleteBet_WhenBetNotExists_ShouldThrowException() {
        // Arrange
        Long playerId = 1L;
        Long betId = 1L;
        when(playerRepository.existsById(playerId)).thenReturn(true);
        when(betRepository.findById(betId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> betService.deleteBet(playerId, betId));
    }
}