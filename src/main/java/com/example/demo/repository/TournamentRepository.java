package com.example.demo.repository;

import com.example.demo.entity.Tournament;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    @Query(
            value = "SELECT t.* FROM tournament t "
                    +
                    "JOIN player_tournament pt ON t.id = pt.tournament_id "
                    +
                    "JOIN player p ON pt.player_id = p.id "
                    +
                    "WHERE p.name = :name",
            nativeQuery = true
    )
    List<Tournament> findTournamentsByName(@Param("name") String name);
}
