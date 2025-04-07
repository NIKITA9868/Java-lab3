package com.example.demo.controller;

import com.example.demo.dto.BetDto;
import com.example.demo.service.BetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;





@RestController
@RequestMapping("/bets")
@Tag(name = "Bet Controller", description = "API для управления ставками")
public class AllBetController {
    private final BetService betService;

    public AllBetController(BetService betService) {
        this.betService = betService;
    }

    @GetMapping
    @Operation(summary = "Получить все ставки", description = "Возвращает список всех ставок")
    @ApiResponses({
        @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BetDto.class))
                    )
            ),
        @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    public ResponseEntity<List<BetDto>> getAllBets() {
        List<BetDto> bets = betService.getAllBets();
        return ResponseEntity.ok(bets);
    }
}