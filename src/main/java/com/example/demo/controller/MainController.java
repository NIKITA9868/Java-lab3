package com.example.demo.controller;

import com.example.demo.dto.ResponseGameDto;
import com.example.demo.service.GameService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class MainController {

    @GetMapping("/game")
    public ResponseGameDto nameOfGame(@RequestParam(defaultValue = "0") int id) {
        String nameOfGame = GameService.getNameOfGame(id);
        return new ResponseGameDto(nameOfGame, id);
    }

    @GetMapping("/user/{name}")
    public Map<String, Object> userInfo(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("name", name);
        response.put("balance", 0);
        return response;
    }
}
