package com.example.demo.service;

import com.example.demo.entity.Game;
import java.util.List;

public class GameService {

    private GameService() {
    }

    public static String getNameOfGame(int id) {

        List<Game> games = List.of(
                new Game(1, "Black Jack"),
                new Game(2, "Roulette"),
                new Game(3, "Poker")
        );

        return games.stream()
                .filter(game -> game.getId() == id)
                .map(Game::getName)
                .findFirst()
                .orElse(null);
    }

    public static boolean isIdValid(int id) {
        return id > 0;
    }
}
