package com.example.demo.controller;

import com.example.demo.dto.TournamentDto;
import com.example.demo.service.TournamentService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tournaments")
public class TournamentController {


    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    // Получить все турниры
    @GetMapping
    public ResponseEntity<List<TournamentDto>> getAllTournaments() {
        List<TournamentDto> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }

    // Получить турнир по ID
    @GetMapping("/{id}")
    public ResponseEntity<TournamentDto> getTournamentById(@PathVariable Long id) {
        TournamentDto tournament = tournamentService.getTournamentById(id);
        return ResponseEntity.ok(tournament);
    }

    // Создать новый турнир
    @PostMapping
    public ResponseEntity<TournamentDto> createTournament(
            @RequestBody TournamentDto tournamentDto) {
        TournamentDto createdTournament = tournamentService.createTournament(tournamentDto);
        return new ResponseEntity<>(createdTournament, HttpStatus.CREATED);
    }

    // Обновить турнир
    @PutMapping("/{id}")
    public ResponseEntity<TournamentDto> updateTournament(
            @PathVariable Long id, @RequestBody TournamentDto tournamentDto) {
        TournamentDto updatedTournament = tournamentService.updateTournament(id, tournamentDto);
        return ResponseEntity.ok(updatedTournament);
    }

    // Удалить турнир
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }

    // Зарегистрировать игрока на турнир
    @PostMapping("/{tournamentId}/register/{playerId}")
    public ResponseEntity<TournamentDto> registerPlayer(
            @PathVariable Long tournamentId, @PathVariable Long playerId) {
        TournamentDto tournament = tournamentService.registerPlayer(tournamentId, playerId);
        return ResponseEntity.ok(tournament);
    }
}