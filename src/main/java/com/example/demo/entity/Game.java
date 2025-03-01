package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "games")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    @Id
    private int id;
    @Column(unique = true)
    private String name;
}

