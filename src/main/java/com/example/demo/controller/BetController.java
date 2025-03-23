package com.example.demo.controller;

import com.example.demo.dto.BetDto;
import com.example.demo.service.BetService;
import java.util.List;
import org.springframework.http.HttpStatus;
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
public class BetController {


    private final BetService betService;

    public BetController(BetService betService) {
        this.betService = betService;
    }

    // Получить все ставки игрока
    @GetMapping
    public ResponseEntity<List<BetDto>> getBetsByPlayerId(@PathVariable Long playerId) {
        List<BetDto> bets = betService.getBetsByPlayerId(playerId);
        return ResponseEntity.ok(bets);
    }

    // Создать новую ставку
    @PostMapping
    public ResponseEntity<BetDto> createBet(@PathVariable Long playerId,
                                            @RequestBody BetDto betDto) {
        BetDto createdBet = betService.createBet(playerId, betDto);
        return new ResponseEntity<>(createdBet, HttpStatus.CREATED);
    }

    // Удалить ставку
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBet(@PathVariable Long playerId, @PathVariable Long id) {
        betService.deleteBet(playerId, id);
        return ResponseEntity.noContent().build();
    }
}
