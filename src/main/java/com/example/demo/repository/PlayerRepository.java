package com.example.demo.repository;

import com.example.demo.entity.Player;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("SELECT DISTINCT p FROM Player p JOIN p.bets b WHERE b.amount >= :minAmount")
    List<Player> findPlayersWithBetsGreaterThan(@Param("minAmount") double minAmount);

}
