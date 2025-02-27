package com.example.demo.controller;

import com.example.demo.dto.ResponseGameDto;
import com.example.demo.dto.ResponseUserDto;
import com.example.demo.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class MainController {

    @GetMapping("/game")
    public ResponseEntity<?> nameOfGame(@RequestParam int id) {
        if (!GameService.isIdValid(id)) {
            return ResponseEntity.badRequest().body("Invalid game ID");
        }
        try {
            String nameOfGame = GameService.getNameOfGame(id);
            if (nameOfGame == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game not found");
            }
            return ResponseEntity.ok(new ResponseGameDto(nameOfGame, id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred");
        }
    }

    @GetMapping("/user/{name}")
    public ResponseEntity<?> userInfo(@PathVariable String name) {
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body("Invalid user name");
        }
        try {
            return ResponseEntity.ok(new ResponseUserDto(name, 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred");
        }
    }
}
