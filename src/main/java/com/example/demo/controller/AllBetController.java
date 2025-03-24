package com.example.demo.controller;

import com.example.demo.dto.BetDto;
import com.example.demo.service.BetService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/bets")
public class AllBetController {
    private final BetService betService;

    public AllBetController(BetService betService) {
        this.betService = betService;
    }

    @GetMapping
    public ResponseEntity<List<BetDto>> getAllBets() {
        List<BetDto> bets = betService.getAllBets();
        return ResponseEntity.ok(bets);
    }
}
