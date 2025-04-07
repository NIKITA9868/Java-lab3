package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для представления игрока")
public class PlayerDto {
    @Schema(
            description = "Уникальный идентификатор игрока",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Имя игрока",
            example = "JohnDoe",
            minLength = 2,
            maxLength = 50
    )
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Schema(
            description = "Баланс игрока",
            example = "1000.50",
            minimum = "0.0"
    )
    @DecimalMin(value = "0.0", message = "Balance cannot be negative")
    private double balance;

    @Schema(
            description = "Список ставок игрока",
            implementation = BetDto.class
    )
    @Valid
    private List<BetDto> bets;

    @Schema(
            description = "Список турниров, в которых участвует игрок",
            implementation = TournamentInfoDto.class
    )
    @Valid
    private List<TournamentInfoDto> tournaments;
}