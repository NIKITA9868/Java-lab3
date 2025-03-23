package com.example.demo.service;

import com.example.demo.dto.PlayerDto;
import com.example.demo.entity.Player;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.PlayerMapperUtils;
import com.example.demo.repository.PlayerRepository;
import java.util.List;
import org.springframework.stereotype.Service;



@Service

public class PlayerService {

    private static final String PLAYER_NOT_FOUND_MESSAGE = "Player not found with id: ";

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // Получить всех игроков с их ставками
    public List<PlayerDto> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(PlayerMapperUtils::converttodto) // Используем статический метод
                .toList();
    }

    // Получить игрока по ID с его ставками
    public PlayerDto getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PLAYER_NOT_FOUND_MESSAGE + id));
        return PlayerMapperUtils.converttodto(player); // Используем статический метод
    }

    // Создать нового игрока
    public PlayerDto createPlayer(PlayerDto playerDto) {
        // Проверяем, что имя игрока не пустое
        if (playerDto.getName() == null || playerDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Player name cannot be empty");
        }

        // Проверяем, что баланс не отрицательный
        if (playerDto.getBalance() < 0) {
            throw new BadRequestException("Player balance cannot be negative");
        }

        Player player = new Player();
        player.setName(playerDto.getName());
        player.setBalance(playerDto.getBalance());
        Player savedPlayer = playerRepository.save(player);
        return PlayerMapperUtils.converttodto(savedPlayer); // Используем статический метод
    }

    // Обновить данные игрока
    public PlayerDto updatePlayer(Long id, PlayerDto playerDto) {
        // Проверяем, что имя игрока не пустое
        if (playerDto.getName() == null || playerDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Player name cannot be empty");
        }

        // Проверяем, что баланс не отрицательный
        if (playerDto.getBalance() < 0) {
            throw new BadRequestException("Player balance cannot be negative");
        }

        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PLAYER_NOT_FOUND_MESSAGE + id));
        player.setName(playerDto.getName());
        player.setBalance(playerDto.getBalance());
        Player updatedPlayer = playerRepository.save(player);
        return PlayerMapperUtils.converttodto(updatedPlayer); // Используем статический метод
    }

    // Удалить игрока
    public void deletePlayer(Long id) {
        // Проверяем, существует ли игрок
        if (!playerRepository.existsById(id)) {
            throw new ResourceNotFoundException(PLAYER_NOT_FOUND_MESSAGE + id);
        }

        playerRepository.deleteById(id);
    }
}
