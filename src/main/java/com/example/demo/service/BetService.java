package com.example.demo.service;

import com.example.demo.dto.BetDto;
import com.example.demo.entity.Bet;
import com.example.demo.entity.Player;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.BetMapperUtils;
import com.example.demo.repository.BetRepository;
import com.example.demo.repository.PlayerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Transactional
public class BetService {
    private final BetRepository betRepository;
    private final PlayerRepository playerRepository;


    public List<BetDto> getAllBets() {
        return betRepository.findAll().stream()
                .map(BetMapperUtils::converttobetdto)
                .toList();
    }


    public List<BetDto> getBetsByPlayerId(Long playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new ResourceNotFoundException("Player not found with id: " + playerId);
        }
        return betRepository.findByPlayerId(playerId).stream()
                .map(BetMapperUtils::converttobetdto)
                .toList();
    }

    @CacheEvict(value = "bets", allEntries = true)
    public BetDto createBet(Long playerId, BetDto betDto) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Player not found with id: " + playerId));

        if (player.getBalance() < betDto.getAmount()) {
            throw new InsufficientBalanceException(
                    "Not enough balance to place the bet. Player ID: " + playerId);
        }

        Bet bet = new Bet();
        bet.setAmount(betDto.getAmount());
        bet.setPlayer(player);

        player.setBalance(player.getBalance() - betDto.getAmount());
        playerRepository.save(player);

        Bet savedBet = betRepository.save(bet);
        return BetMapperUtils.converttobetdto(savedBet);
    }

    @CacheEvict(value = "bets", allEntries = true)
    public void deleteBet(Long playerId, Long betId) {
        if (!playerRepository.existsById(playerId)) {
            throw new ResourceNotFoundException("Player not found with id: " + playerId);
        }

        Bet bet = betRepository.findById(betId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bet not found with id: " + betId));

        // Возвращаем деньги игроку
        Player player = bet.getPlayer();
        player.setBalance(player.getBalance() + bet.getAmount());
        playerRepository.save(player);

        betRepository.delete(bet);
    }
}