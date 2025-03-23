package com.example.demo.repository;


import com.example.demo.entity.Bet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;



public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByPlayerId(Long playerId);
}
