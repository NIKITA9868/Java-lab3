package com.example.demo.service;

import com.example.demo.cache.CacheFactory;
import com.example.demo.cache.MyCache;
import com.example.demo.dto.PlayerDto;
import com.example.demo.entity.Player;
import com.example.demo.entity.Tournament;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.PlayerMapperUtils;
import com.example.demo.repository.PlayerRepository;
import com.example.demo.repository.TournamentRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"players", "playersWithBets"})
public class PlayerService {

    private static final String PLAYER_NOT_FOUND_MESSAGE = "Player not found with id: ";
    private final PlayerRepository playerRepository;
    private final TournamentRepository tournamentRepository;
    private final CacheFactory cacheFactory;
    private MyCache<Long, List<PlayerDto>> playerMyCache;

    @PostConstruct
    public void init() {
        this.playerMyCache = cacheFactory.createCache(
                "playerCache",
                2,
                60_000
        );
    }

    public List<PlayerDto> findPlayersWithBetsMoreThan(Long bets) {

        return playerMyCache.get(bets, () -> {
            List<PlayerDto> result = playerRepository.findPlayersWithBetsGreaterThan(bets).stream()
                  .map(PlayerMapperUtils::converttodto)
                  .toList();

            if (result.isEmpty()) {
                throw new ResourceNotFoundException(
                        String.format("No players found with bets more than %d", bets)
                );
            }
            return result;
        });

    }

    public List<PlayerDto> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(PlayerMapperUtils::converttodto)
                .toList();
    }


    public PlayerDto getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PLAYER_NOT_FOUND_MESSAGE + id));
        return PlayerMapperUtils.converttodto(player);
    }


    public PlayerDto createPlayer(PlayerDto playerDto) {

        Player player = new Player();
        player.setName(playerDto.getName());
        player.setBalance(playerDto.getBalance());
        Player savedPlayer = playerRepository.save(player);
        playerMyCache.clear();
        return PlayerMapperUtils.converttodto(savedPlayer);
    }

    public PlayerDto updatePlayer(Long id, PlayerDto playerDto) {


        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PLAYER_NOT_FOUND_MESSAGE + id));
        player.setName(playerDto.getName());
        player.setBalance(playerDto.getBalance());
        Player updatedPlayer = playerRepository.save(player);
        playerMyCache.clear();
        return PlayerMapperUtils.converttodto(updatedPlayer);
    }

    @Transactional
    public void deletePlayer(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PLAYER_NOT_FOUND_MESSAGE + playerId));

        List<Tournament> tournaments = tournamentRepository.findTournamentsByName(
                player.getName());
        tournaments.forEach(t -> t.getPlayers().remove(player));
        tournamentRepository.saveAll(tournaments);

        player.getTournaments().clear();
        player.getBets().clear();
        playerMyCache.clear();
        playerRepository.delete(player);
    }


}