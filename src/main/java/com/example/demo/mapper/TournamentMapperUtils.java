package com.example.demo.mapper;

import com.example.demo.dto.PlayerInfoDto;
import com.example.demo.dto.TournamentDto;
import com.example.demo.dto.TournamentInfoDto;
import com.example.demo.entity.Tournament;
import java.util.List;
import java.util.stream.Collectors;

public class TournamentMapperUtils {

    private TournamentMapperUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Преобразование Tournament -> TournamentInfoDto
    public static TournamentInfoDto converttotournamentinfodto(Tournament tournament) {
        TournamentInfoDto tournamentInfoDto = new TournamentInfoDto();
        tournamentInfoDto.setId(tournament.getId());
        tournamentInfoDto.setName(tournament.getName());
        tournamentInfoDto.setPrizePool(tournament.getPrizePool());
        return tournamentInfoDto;
    }

    // Преобразование сущности Tournament в TournamentDTO
    public static TournamentDto converttodto(Tournament tournament) {
        TournamentDto tournamentDto = new TournamentDto();
        tournamentDto.setId(tournament.getId());
        tournamentDto.setName(tournament.getName());
        tournamentDto.setPrizePool(tournament.getPrizePool());

        // Преобразуем игроков в PlayerInfoDTO
        List<PlayerInfoDto> playerInfoDtos = tournament.getPlayers().stream()
                .map(PlayerMapperUtils::converttoplayerinfodto)
                .collect(Collectors.toList());
        tournamentDto.setPlayers(playerInfoDtos);

        return tournamentDto;
    }
}
