package com.example.demo.service;

import com.example.demo.dto.TournamentDto;
import com.example.demo.entity.Player;
import com.example.demo.entity.Tournament;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.TournamentMapperUtils;
import com.example.demo.repository.PlayerRepository;
import com.example.demo.repository.TournamentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TournamentService {

    private static final String TOURNAMENT_NOT_FOUND_MESSAGE = "Tournament not found with id: ";

    private final TournamentRepository tournamentRepository;


    private final PlayerRepository playerRepository;

    public TournamentService(
            TournamentRepository tournamentRepository, PlayerRepository playerRepository) {
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
    }

    // Получить все турниры
    public List<TournamentDto> getAllTournaments() {
        return tournamentRepository.findAll().stream()
                .map(TournamentMapperUtils::converttodto)
                .toList();
    }

    // Получить турнир по ID
    public TournamentDto getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TOURNAMENT_NOT_FOUND_MESSAGE + id));
        return TournamentMapperUtils.converttodto(tournament);
    }

    // Создать новый турнир
    public TournamentDto createTournament(TournamentDto tournamentDto) {
        // Проверяем, что название турнира не пустое
        if (tournamentDto.getName() == null || tournamentDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Tournament name cannot be empty");
        }

        // Проверяем, что призовой фонд не отрицательный
        if (tournamentDto.getPrizePool() < 0) {
            throw new BadRequestException("Prize pool cannot be negative");
        }

        Tournament tournament = new Tournament();
        tournament.setName(tournamentDto.getName());
        tournament.setPrizePool(tournamentDto.getPrizePool());
        Tournament savedTournament = tournamentRepository.save(tournament);
        return TournamentMapperUtils.converttodto(savedTournament);
    }

    // Обновить турнир
    public TournamentDto updateTournament(Long id, TournamentDto tournamentDto) {
        // Проверяем, что название турнира не пустое
        if (tournamentDto.getName() == null || tournamentDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Tournament name cannot be empty");
        }

        // Проверяем, что призовой фонд не отрицательный
        if (tournamentDto.getPrizePool() < 0) {
            throw new BadRequestException("Prize pool cannot be negative");
        }

        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TOURNAMENT_NOT_FOUND_MESSAGE + id));
        tournament.setName(tournamentDto.getName());
        tournament.setPrizePool(tournamentDto.getPrizePool());
        Tournament updatedTournament = tournamentRepository.save(tournament);
        return TournamentMapperUtils.converttodto(updatedTournament);
    }

    // Удалить турнир
    @Transactional
    public void deleteTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TOURNAMENT_NOT_FOUND_MESSAGE + tournamentId));

        // Удаляем все связи с игроками
        for (Player player : tournament.getPlayers()) {
            player.getTournaments().remove(tournament); // Удаляем турнир из списка игрока
        }
        tournament.getPlayers().clear(); // Очищаем список игроков у турнира

        // Удаляем турнир
        tournamentRepository.delete(tournament);
    }

    // Зарегистрировать игрока на турнир
    @Transactional
    public TournamentDto registerPlayer(Long tournamentId, Long playerId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TOURNAMENT_NOT_FOUND_MESSAGE + tournamentId));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Player not found with id: " + playerId));

        // Проверяем, не зарегистрирован ли игрок уже на турнир
        if (tournament.getPlayers().contains(player)) {
            throw new BadRequestException("Player is already registered for the tournament");
        }

        tournament.getPlayers().add(player);
        player.getTournaments().add(tournament);

        tournamentRepository.save(tournament);
        playerRepository.save(player);

        return TournamentMapperUtils.converttodto(tournament);
    }

    @Transactional
    public TournamentDto unregisterPlayer(Long tournamentId, Long playerId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TOURNAMENT_NOT_FOUND_MESSAGE + tournamentId));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Player not found with id: " + playerId));

        // Проверяем, зарегистрирован ли игрок на турнир
        if (!tournament.getPlayers().contains(player)) {
            throw new BadRequestException("Player is not registered for the tournament");
        }

        tournament.getPlayers().remove(player);
        player.getTournaments().remove(tournament);

        tournamentRepository.save(tournament);
        playerRepository.save(player);

        return TournamentMapperUtils.converttodto(tournament);
    }
}