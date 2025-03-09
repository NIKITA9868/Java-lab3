package com.example.demo.service;

import com.example.demo.dto.ResponseGameDto;
import com.example.demo.entity.Game;
import com.example.demo.repository.GameRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GameService {


    @Autowired
    private GameRepo gameRepo;

    public boolean isIdNotValid(int id) {
        return id <= 0;
    }

    public Game save(Game game) {
        return gameRepo.save(game);
    }

    public List<Game> findAll() {
        return gameRepo.findAll();
    }

    public ResponseGameDto getUserById(int id) {
        return gameRepo.findById(id)
                .map(game -> new ResponseGameDto(game.getGame(), game.getId()))
                .orElse(new ResponseGameDto("User not found", id));
    }

    public boolean doesntExistsById(int id) {
        return !gameRepo.existsById(id);
    }

    public void deleteById(int id) {
        gameRepo.deleteById(id);
    }
}
