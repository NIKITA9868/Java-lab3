package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for Bet information")
public class BetDto {

    @Schema(
            description = "Unique identifier of the bet",
            example = "123",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Amount of money placed on the bet",
            example = "100.50"

    )
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @Schema(
            description = "ID of the player who placed the bet",
            example = "42"

    )
    @Positive(message = "Player ID must be positive")
    private Long playerId;
}