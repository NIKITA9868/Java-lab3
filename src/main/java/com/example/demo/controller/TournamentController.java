package com.example.demo.controller;

import com.example.demo.dto.TournamentDto;
import com.example.demo.service.TournamentService;
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
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/tournaments")
@RequiredArgsConstructor
@Tag(name = "Tournament Management", description = "API для управления турнирами")
public class TournamentController {

    private final TournamentService tournamentService;

    @Operation(
            summary = "Получить все турниры",
            description = "Возвращает список всех доступных турниров"
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TournamentDto.class)
                    )
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TournamentDto>> getAllTournaments() {
        List<TournamentDto> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }

    @Operation(
            summary = "Получить турниры по имени игрока",
            description = "Возвращает турниры, в которых участвует указанный игрок",
            parameters = {
                @Parameter(
                            name = "name",
                            description = "Имя игрока",
                            example = "JohnDoe",
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
                            schema = @Schema(implementation = TournamentDto.class)
                    )),
        @ApiResponse(
                            responseCode = "404",
                            description = "Игрок не найден",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    examples = @ExampleObject(value =
                                            "Player not found with name: JohnDoe")
                            )
                    )
        })
    @GetMapping(value = "/byPlayerName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TournamentDto>> getTournamentsByPlayerName(
            @RequestParam String name) {
        List<TournamentDto> tournaments = tournamentService.getTournamentsByPlayerId(name);
        return ResponseEntity.ok(tournaments);
    }

    @Operation(
            summary = "Получить турнир по ID",
            description = "Возвращает детальную информацию о турнире",
            parameters = {
                @Parameter(
                            name = "id",
                            description = "ID турнира",
                            example = "1",
                            required = true
                    )
            }
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "200",
                    description = "Турнир найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TournamentDto.class)
                    )),
        @ApiResponse(
                            responseCode = "404",
                            description = "Турнир не найден",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    examples = @ExampleObject(value =
                                            "Tournament not found with id: 1")
                            )
                    )
        })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TournamentDto> getTournamentById(@PathVariable Long id) {
        TournamentDto tournament = tournamentService.getTournamentById(id);
        return ResponseEntity.ok(tournament);
    }

    @Operation(
            summary = "Создать новый турнир",
            description = "Создает новый турнир с указанными параметрами"
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "201",
                    description = "Турнир успешно создан",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TournamentDto.class)
                    )
            ),
        @ApiResponse(
                    responseCode = "400",
                    description = "Неверные данные турнира",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(value = "Validation failed for fields: name")
                    )
            )
    })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TournamentDto> createTournament(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового турнира",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TournamentDto.class),
                            examples = @ExampleObject(
                                    value = "{\"name\": \"Spring Cup\", "
                                          + "prizePool\": 10000, \"startDate\": \"2023-12-01\"}"
                            )
                    )
            ) TournamentDto tournamentDto) {
        TournamentDto createdTournament = tournamentService.createTournament(tournamentDto);
        return new ResponseEntity<>(createdTournament, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Обновить турнир",
            description = "Обновляет информацию о турнире",
            parameters = {
                @Parameter(
                            name = "id",
                            description = "ID турнира для обновления",
                            example = "1",
                            required = true
                    )
            }
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "200",
                    description = "Турнир успешно обновлен",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TournamentDto.class)
                    )),
        @ApiResponse(
                            responseCode = "400",
                            description = "Неверные данные турнира",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    examples = @ExampleObject(value =
                                            "Validation failed for fields: prizePool")
                            )
                    ),
        @ApiResponse(
                            responseCode = "404",
                            description = "Турнир не найден",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    examples = @ExampleObject(value =
                                            "Tournament not found with id: 1")
                            )
                    )
        })
    @PutMapping(
                    value = "/{id}",
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE
            )
    public ResponseEntity<TournamentDto> updateTournament(
            @PathVariable Long id,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные турнира",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TournamentDto.class),
                            examples = @ExampleObject(
                                    value = "{\"name\": \"Updated Tournament\","
                                           + " \"prizePool\": 15000}"
                            )
                    )
            ) TournamentDto tournamentDto) {
        TournamentDto updatedTournament = tournamentService.updateTournament(id, tournamentDto);
        return ResponseEntity.ok(updatedTournament);
    }

    @Operation(
            summary = "Удалить турнир",
            description = "Удаляет турнир по его ID",
            parameters = {
                @Parameter(
                            name = "id",
                            description = "ID турнира для удаления",
                            example = "1",
                            required = true
                    )
            }
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "204",
                    description = "Турнир успешно удален"
            ),
        @ApiResponse(
                    responseCode = "404",
                    description = "Турнир не найден",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(value = "Tournament not found with id: 1")
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Зарегистрировать игрока на турнир",
            description = "Добавляет игрока в список участников турнира",
            parameters = {
                @Parameter(
                            name = "tournamentId",
                            description = "ID турнира",
                            example = "1",
                            required = true
                    ),
                @Parameter(
                            name = "playerId",
                            description = "ID игрока",
                            example = "10",
                            required = true
                    )
            }
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "200",
                    description = "Игрок успешно зарегистрирован",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TournamentDto.class)
                    )),
        @ApiResponse(
                            responseCode = "400",
                            description = "Игрок уже зарегистрирован",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    examples = @ExampleObject(value =
                                            "Player already registered for this tournament")
                            )
                    ),
        @ApiResponse(
                            responseCode = "404",
                            description = "Турнир или игрок не найдены",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    examples = @ExampleObject(value =
                                            "Tournament not found with id: 1")
                            )
                    )
        })
    @PostMapping(
                    value = "/{tournamentId}/register/{playerId}",
                    produces = MediaType.APPLICATION_JSON_VALUE
            )
    public ResponseEntity<TournamentDto> registerPlayer(
            @PathVariable Long tournamentId,
            @PathVariable Long playerId) {
        TournamentDto tournament = tournamentService.registerPlayer(tournamentId, playerId);
        return ResponseEntity.ok(tournament);
    }

    @Operation(
            summary = "Отменить регистрацию игрока на турнир",
            description = "Удаляет игрока из списка участников турнира",
            parameters = {
                @Parameter(
                            name = "tournamentId",
                            description = "ID турнира",
                            example = "1",
                            required = true
                    ),
                @Parameter(
                            name = "playerId",
                            description = "ID игрока",
                            example = "10",
                            required = true
                    )
            }
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "200",
                    description = "Регистрация игрока успешно отменена",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TournamentDto.class)
                    )
            ),
        @ApiResponse(
                    responseCode = "400",
                    description = "Игрок не был зарегистрирован на турнир",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(value =
                                    "Player was not registered for this tournament")
                    )
            ),
        @ApiResponse(
                    responseCode = "404",
                    description = "Турнир или игрок не найдены",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(value = "Player not found with id: 10")
                    )
            )
    })
    @PostMapping(
            value = "/{tournamentId}/unregister/{playerId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TournamentDto> unregisterPlayer(
            @PathVariable Long tournamentId,
            @PathVariable Long playerId) {
        TournamentDto tournament = tournamentService.unregisterPlayer(tournamentId, playerId);
        return ResponseEntity.ok(tournament);
    }
}