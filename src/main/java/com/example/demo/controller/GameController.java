package com.example.demo.controller;

import com.example.demo.dto.ResponseGameDto;
import com.example.demo.entity.Game;
import com.example.demo.service.GameService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping()
    public ResponseEntity<ResponseGameDto> getGame(@RequestParam int id) {
        if (gameService.isIdNotValid(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseGameDto("Invalid game ID", id));
        }
        ResponseGameDto response = gameService.getUserById(id);

        if ("User not found".equals(response.game())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Game>> getGames() {
        List<Game> games = gameService.findAll();
        return ResponseEntity.ok(games);
    }

    @PostMapping()
    public ResponseEntity<Game> addGame(@RequestBody Game game) {
        if (game.getGame() == null || game.getGame().isEmpty()
                || gameService.isIdNotValid(game.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(game);
        }

        Game savedGame = gameService.save(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGame);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteGame(@RequestParam int id) {
        if (gameService.doesntExistsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        gameService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping()
    public ResponseEntity<Game> updateGame(@RequestBody Game game) {
        if (gameService.doesntExistsById(game.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Game updatedGame = gameService.save(game);
        return ResponseEntity.ok(updatedGame);
    }


}
