package com.example.demo.controller;

import com.example.demo.dto.PlayerDto;
import com.example.demo.service.PlayerService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/players")
@Tag(name = "Player Management", description = "API для управления игроками")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Operation(
            summary = "Получить игроков с количеством ставок больше указанного",
            description = "Возвращает игроков, сделавших больше ставок, чем заданное значение",
            parameters = {
                @Parameter(
                            name = "bets",
                            description = "Минимальное количество ставок",
                            example = "5",
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
                            schema = @Schema(implementation = PlayerDto.class)
                    )),
        @ApiResponse(
                    responseCode = "400",
                    description = "Неверный параметр запроса",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject
                                    (value = "Parameter 'bets' must be positive")
                    )
            )
    })
    @GetMapping(value = "/bets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PlayerDto>> getPlayersWithBets(
            @RequestParam Long bets) {
        List<PlayerDto> players = playerService.findPlayersWithBetsMoreThan(bets);
        return ResponseEntity.ok(players);
    }

    @Operation(
            summary = "Получить всех игроков",
            description = "Возвращает список всех зарегистрированных игроков"
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PlayerDto.class)
                    ))
        })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PlayerDto>> getAllPlayers() {
        List<PlayerDto> players = playerService.getAllPlayers();
        return ResponseEntity.ok(players);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<PlayerDto>> createPlayersBulk(
            @RequestBody List<PlayerDto> playerDtos
    ) {
        return ResponseEntity.ok(playerService.createPlayersBulk(playerDtos));
    }


    @Operation(
            summary = "Получить игрока по ID",
            description = "Возвращает данные игрока по его идентификатору",
            parameters = {
                @Parameter(
                            name = "id",
                            description = "ID игрока",
                            example = "1",
                            required = true
                    )
            }
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "200",
                    description = "Игрок найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PlayerDto.class)
                    )),
        @ApiResponse(
                            responseCode = "404",
                            description = "Игрок не найден",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    examples = @ExampleObject(value = "Player not found with id: 1")
                            )
                    )
        })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlayerDto> getPlayerById(@PathVariable Long id) {
        PlayerDto player = playerService.getPlayerById(id);
        return ResponseEntity.ok(player);
    }

    @Operation(
            summary = "Создать нового игрока",
            description = "Регистрирует нового игрока в системе"
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "201",
                    description = "Игрок успешно создан",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PlayerDto.class)
                    )
            ),
        @ApiResponse(
                    responseCode = "400",
                    description = "Неверные данные игрока",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(
                                    value = "Validation failed for fields: username")
                    )
            )
    })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PlayerDto> createPlayer(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового игрока",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PlayerDto.class),
                            examples = @ExampleObject(
                                    value = "{\"username\": \"player1\","
                                          + " \"email\": \"player1@example.com\"}"
                            )
                    )
            ) PlayerDto playerDto) {
        PlayerDto createdPlayer = playerService.createPlayer(playerDto);
        return new ResponseEntity<>(createdPlayer, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Обновить данные игрока",
            description = "Обновляет информацию об игроке по его ID",
            parameters = {
                @Parameter(
                            name = "id",
                            description = "ID игрока для обновления",
                            example = "1",
                            required = true
                    )
            }
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "200",
                    description = "Данные игрока успешно обновлены",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PlayerDto.class)
                    )
            ),
        @ApiResponse(
                    responseCode = "400",
                    description = "Неверные данные игрока",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(value = "Validation failed for fields: email")
                    )
            ),
        @ApiResponse(
                    responseCode = "404",
                    description = "Игрок не найден",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(value = "Player not found with id: 1")
                    )
            )
    })
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PlayerDto> updatePlayer(
            @PathVariable Long id,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные игрока",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PlayerDto.class),
                            examples = @ExampleObject(
                                    value = "{\"username\": \"updatedPlayer\","
                                           + " \"email\": \"updated@example.com\"}"
                            )
                    )
            ) PlayerDto playerDto) {
        PlayerDto updatedPlayer = playerService.updatePlayer(id, playerDto);
        return ResponseEntity.ok(updatedPlayer);
    }

    @Operation(
            summary = "Удалить игрока",
            description = "Удаляет игрока по его ID",
            parameters = {
                @Parameter(
                            name = "id",
                            description = "ID игрока для удаления",
                            example = "1",
                            required = true
                    )
            }
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "204",
                    description = "Игрок успешно удален"
            ),
        @ApiResponse(
                    responseCode = "404",
                    description = "Игрок не найден",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(value = "Player not found with id: 1")
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}