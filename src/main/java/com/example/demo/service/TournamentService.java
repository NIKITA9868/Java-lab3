package com.example.demo.service;


import com.example.demo.cache.CacheFactory;
import com.example.demo.cache.MyCache;
import com.example.demo.dto.TournamentDto;
import com.example.demo.entity.Player;
import com.example.demo.entity.Tournament;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.TournamentMapperUtils;
import com.example.demo.repository.PlayerRepository;
import com.example.demo.repository.TournamentRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TournamentService {

    private static final String TOURNAMENT_NOT_FOUND_MESSAGE = "Tournament not found with id: ";
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;

    private final CacheFactory cacheFactory;
    private MyCache<String, List<TournamentDto>> tournamentMyCache;

    @PostConstruct
    public void init() {
        this.tournamentMyCache = cacheFactory.createCache(
                "tournamentCache",
                2,
                60_000
        );
    }

    @Transactional
    public List<TournamentDto> getTournamentsByPlayerId(String name) {

        return tournamentMyCache.get(name, () -> {
            List<TournamentDto> tournaments = tournamentRepository.findTournamentsByName(name)
                    .stream()
                    .map(TournamentMapperUtils::converttodto)
                    .toList();

            if (tournaments.isEmpty()) {
                throw new ResourceNotFoundException(
                        "No tournaments found for player with name: " + name);
            }
            // Если нет в кэше, получаем из БД и кэшируем
            return tournaments;
        });

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
        tournamentMyCache.clear();
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
        tournamentMyCache.clear();
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

        tournamentMyCache.clear();
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

        tournamentMyCache.clear();
        tournamentRepository.save(tournament);
        playerRepository.save(player);



        return TournamentMapperUtils.converttodto(tournament);
    }
}