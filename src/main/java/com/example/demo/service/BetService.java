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
import org.springframework.stereotype.Service;


@Service
public class BetService {


    private final BetRepository betRepository;


    private final PlayerRepository playerRepository;

    public BetService(BetRepository betRepository, PlayerRepository playerRepository) {
        this.betRepository = betRepository;
        this.playerRepository = playerRepository;
    }

    public List<BetDto> getAllBets() {

        return betRepository.findAll().stream()
                .map(BetMapperUtils::converttobetdto)
                .toList();
    }

    // Получить все ставки игрока по ID
    public List<BetDto> getBetsByPlayerId(Long playerId) {
        // Проверяем, существует ли игрок
        if (!playerRepository.existsById(playerId)) {
            throw new ResourceNotFoundException("Player not found with id: " + playerId);
        }

        return betRepository.findByPlayerId(playerId).stream()
                .map(BetMapperUtils::converttobetdto)
                .toList();
    }

    // Создать новую ставку
    public BetDto createBet(Long playerId, BetDto betDto) {
        // Находим игрока по ID
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Player not found with id: " + playerId));

        // Проверяем, достаточно ли средств на балансе
        if (player.getBalance() < betDto.getAmount()) {
            throw new InsufficientBalanceException(
                    "Not enough balance to place the bet. Player ID: " + playerId);
        }

        // Создаем новую ставку
        Bet bet = new Bet();
        bet.setAmount(betDto.getAmount());
        bet.setPlayer(player);

        // Списываем деньги с баланса игрока
        player.setBalance(player.getBalance() - betDto.getAmount());

        // Сохраняем обновленного игрока и ставку
        playerRepository.save(player); // Обновляем баланс игрока
        Bet savedBet = betRepository.save(bet); // Сохраняем ставку

        // Возвращаем DTO созданной ставки
        return BetMapperUtils.converttobetdto(savedBet);
    }

    // Удалить ставку по ID
    public void deleteBet(Long playerId, Long id) {
        // Проверяем, существует ли ставка
        if (!playerRepository.existsById(id)) {
            throw new ResourceNotFoundException("player not found with id: " + playerId);
        }

        if (!betRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bet not found with id: " + id);
        }

        betRepository.deleteById(id);
    }
}
