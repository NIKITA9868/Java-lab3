package com.example.demo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TournamentDto {
    private Long id;
    private String name;
    private double prizePool;
    private List<PlayerInfoDto> players; // Список ID игроков, участвующих в турнире

    // Геттеры и сеттеры
}