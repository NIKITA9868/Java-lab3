package com.example.demo.service;


import com.example.demo.dto.PlayerDto;
import com.example.demo.entity.Player;
import com.example.demo.entity.Tournament;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.PlayerMapperUtils;
import com.example.demo.repository.PlayerRepository;
import com.example.demo.repository.TournamentRepository;
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
    private final PlayerCacheService playerCacheService;
    private final TournamentCacheService tournamentCacheService;

    @Transactional
    public List<PlayerDto> findPlayersWithBetsMoreThan(Long bets) {


        List<Player> result = playerCacheService.getPlayer(
                bets,
                () -> playerRepository.findPlayersWithBetsGreaterThan(bets)// Ленивая загрузка
        );

        if (result.isEmpty()) {
            throw new ResourceNotFoundException(
                        String.format("No players found with bets more than %d", bets)
                );
        }

        return result.stream().map(PlayerMapperUtils::converttodto).toList();



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
        playerCacheService.clear();
        return PlayerMapperUtils.converttodto(savedPlayer);
    }


    public PlayerDto updatePlayer(Long id, PlayerDto playerDto) {


        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PLAYER_NOT_FOUND_MESSAGE + id));
        player.setName(playerDto.getName());
        player.setBalance(playerDto.getBalance());
        Player updatedPlayer = playerRepository.save(player);
        playerCacheService.clear();
        tournamentCacheService.clear();
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
        playerCacheService.clear();
        tournamentCacheService.clear();

        playerRepository.delete(player);
    }


}