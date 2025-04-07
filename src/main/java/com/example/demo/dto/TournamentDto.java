package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для представления турнира")
public class TournamentDto {
    @Schema(
            description = "Уникальный идентификатор турнира",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Название турнира",
            example = "World Championship",
            minLength = 3,
            maxLength = 100

    )
    @NotBlank(message = "Tournament name cannot be blank")
    @Size(min = 3, max = 100, message = "Tournament name must be between 3 and 100 characters")
    private String name;

    @Schema(
            description = "Призовой фонд турнира",
            example = "1000000.00",
            minimum = "0.0",
            exclusiveMinimum = true,
            pattern = "^\\d{1,10}(\\.\\d{1,2})?$"
    )
    @DecimalMin(value = "0.0", inclusive = false, message = "Prize pool must be greater than 0")
    @Digits(integer = 10, fraction = 2, message =
            "Prize pool must have up to 10 integer and 2 decimal digits")
    private double prizePool;

    @Schema(
            description = "Список игроков, участвующих в турнире",
            implementation = PlayerInfoDto.class
    )
    @Valid
    private List<PlayerInfoDto> players;
}