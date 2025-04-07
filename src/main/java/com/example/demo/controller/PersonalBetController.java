package com.example.demo.controller;

import com.example.demo.dto.BetDto;
import com.example.demo.service.BetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/players/{playerId}/bets")
@Tag(name = "Personal Bets Management",
        description = "API для управления персональными ставками игроков")
public class PersonalBetController {

    private final BetService betService;

    public PersonalBetController(BetService betService) {
        this.betService = betService;
    }

    @Operation(
            summary = "Получить все ставки игрока",
            description = "Возвращает список всех ставок для указанного игрока",
            parameters = {
                @Parameter(
                            name = "playerId",
                            description = "ID игрока",
                            example = "123",
                            required = true
                    )
            }
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BetDto.class)
                    )),
        @ApiResponse(
                            responseCode = "404",
                            description = "Игрок не найден",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    examples = @ExampleObject(
                                            value = "Player not found with id: 123")
                            )
                    )
        })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BetDto>> getBetsByPlayerId(
            @PathVariable Long playerId) {
        List<BetDto> bets = betService.getBetsByPlayerId(playerId);
        return ResponseEntity.ok(bets);
    }

    @Operation(
            summary = "Создать новую ставку",
            description = "Создает новую ставку для указанного игрока",
            parameters = {
                @Parameter(
                            name = "playerId",
                            description = "ID игрока",
                            example = "123",
                            required = true
                    )
            }
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "201",
                    description = "Ставка успешно создана",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BetDto.class)
                    )),

        @ApiResponse(
                            responseCode = "400",
                            description = "Неверные данные ставки",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    examples = @ExampleObject(
                                            value = "Validation failed for fields: amount")
                            )),
        @ApiResponse(
                                    responseCode = "404",
                                    description = "Игрок не найден",
                                    content = @Content(
                                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                                            examples = @ExampleObject(
                                                    value = "Player not found with id: 123")
                                    )
                            )
        })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BetDto> createBet(
            @PathVariable Long playerId,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания ставки",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = BetDto.class),
                            examples = @ExampleObject(
                                    value = "{\"amount\": 100.0, \"eventId\": 456}"
                            )
                    )
            ) BetDto betDto) {
        BetDto createdBet = betService.createBet(playerId, betDto);
        return new ResponseEntity<>(createdBet, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Удалить ставку",
            description = "Удаляет ставку по ID для указанного игрока",
            parameters = {
                @Parameter(
                            name = "playerId",
                            description = "ID игрока",
                            example = "123",
                            required = true
                    ),
                @Parameter(
                            name = "id",
                            description = "ID ставки",
                            example = "789",
                            required = true
                    )
            }
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "204",
                    description = "Ставка успешно удалена"
            ),
        @ApiResponse(
                    responseCode = "404",
                    description = "Игрок или ставка не найдены",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(value =
                                    "Bet not found with id: 789 for player: 123")
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBet(
            @PathVariable Long playerId,
            @PathVariable Long id) {
        betService.deleteBet(playerId, id);
        return ResponseEntity.noContent().build();
    }
}