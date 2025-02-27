package com.example.demo.games;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Game {
    private int id;
    private String name;
}
