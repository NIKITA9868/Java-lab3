package com.example.demo.repository;

import com.example.demo.entity.Tournament;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    // Нативный запрос для поиска турниров по ID игрока
    @Query(
            value = "SELECT t.* FROM tournament t "
                    + "JOIN player_tournament pt ON t.id = pt.tournament_id "
                    + "WHERE pt.player_id = :playerId",
            nativeQuery = true
    )
    List<Tournament> findTournamentsByPlayerId(@Param("playerId") Long playerId);
}
