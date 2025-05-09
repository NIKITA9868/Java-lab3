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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Log4j2
public class TournamentService {

    private static final String TOURNAMENT_NOT_FOUND_MESSAGE = "Tournament not found with id: ";
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final TournamentCacheService tournamentCacheService;

    @Transactional
    public List<TournamentDto> getTournamentsByPlayerName(String name) {

        List<Tournament> tournaments = tournamentCacheService.getPlayerTournaments(
                name,
                () -> tournamentRepository.findTournamentsByName(name) // Ленивая загрузка
        );


        if (tournaments.isEmpty()) {
            throw new ResourceNotFoundException(
                        "No tournaments found for player with name: " + name);
        }

        return tournaments.stream()
                .map(TournamentMapperUtils::converttodto)
                .toList();


    }

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
        tournamentCacheService.clear();
        Tournament updatedTournament = tournamentRepository.save(tournament);
        return TournamentMapperUtils.converttodto(updatedTournament);
    }

    // Удалить турнир
    @Transactional
    public void deleteTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TOURNAMENT_NOT_FOUND_MESSAGE + tournamentId));




        // Удаляем связи
        for (Player player : tournament.getPlayers()) {
            player.getTournaments().remove(tournament);
        }
        tournament.getPlayers().clear();
        tournamentCacheService.clear();
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

        if (tournament.getPlayers().contains(player)) {
            throw new BadRequestException("Player is already registered for the tournament");
        }

        tournament.getPlayers().add(player);
        player.getTournaments().add(tournament);

        tournamentCacheService.clear();
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

        if (!tournament.getPlayers().contains(player)) {
            throw new BadRequestException("Player is not registered for the tournament");
        }

        tournament.getPlayers().remove(player);
        player.getTournaments().remove(tournament);

        tournamentCacheService.clear();
        tournamentRepository.save(tournament);
        playerRepository.save(player);



        return TournamentMapperUtils.converttodto(tournament);
    }
}