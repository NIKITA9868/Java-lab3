package com.example.demo.mapper;


import com.example.demo.dto.BetDto;
import com.example.demo.dto.PlayerDto;
import com.example.demo.dto.PlayerInfoDto;
import com.example.demo.dto.TournamentInfoDto;
import com.example.demo.entity.Player;
import java.util.List;
import java.util.stream.Collectors;


public class PlayerMapperUtils {

    private PlayerMapperUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Преобразование Player -> PlayerDto
    public static PlayerDto converttodto(Player player) {
        PlayerDto playerDto = new PlayerDto();
        playerDto.setId(player.getId());
        playerDto.setName(player.getName());
        playerDto.setBalance(player.getBalance());

        // Преобразуем ставки в BetDTO
        List<BetDto> betDtos = player.getBets().stream()
                .map(BetMapperUtils::converttobetdto)
                .collect(Collectors.toList());
        playerDto.setBets(betDtos);


        // Преобразуем турниры в TournamentInfoDTO
        List<TournamentInfoDto> tournamentInfoDtos = player.getTournaments().stream()
                .map(TournamentMapperUtils::converttotournamentinfodto)
                .collect(Collectors.toList());
        playerDto.setTournaments(tournamentInfoDtos);

        return playerDto;
    }

    public static PlayerInfoDto converttoplayerinfodto(Player player) {
        PlayerInfoDto playerInfoDto = new PlayerInfoDto();
        playerInfoDto.setId(player.getId());
        playerInfoDto.setName(player.getName());
        playerInfoDto.setBalance(player.getBalance());
        return playerInfoDto;
    }

}
