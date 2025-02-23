package com.example.demo.service;

public class GameService {

    private GameService() {
    }

    public static String getNameOfGame(int id) {
        return switch (id) {
            case 1 -> "Black Jack";
            case 2 -> "Roulette";
            case 3 -> "Poker";
            default -> "Not a valid game";
        };
    }
}
