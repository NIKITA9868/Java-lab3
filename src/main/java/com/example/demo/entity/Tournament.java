package com.example.demo.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;



@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double prizePool;

    @ManyToMany(mappedBy = "tournaments") // Используем mappedBy, так как Player - владелец связи
    private Set<Player> players = new HashSet<>();


}