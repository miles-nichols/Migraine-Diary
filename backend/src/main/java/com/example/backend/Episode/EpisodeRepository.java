package com.example.backend.Episode;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Integer> {
    
    List<Episode> findByUsername(String username);

    Optional<Episode> findByUsernameAndEpisodeDate(String username, LocalDate episodeDate);
    
    @Query("SELECT e FROM Episode e WHERE e.username = :username AND YEAR(e.episodeDate) = :year AND MONTH(e.episodeDate) = :month ORDER BY e.episodeDate")
    List<Episode> findByUsernameAndMonth(@Param("username") String username, 
                                        @Param("year") int year, 
                                        @Param("month") int month);
    
    @Query("SELECT e FROM Episode e WHERE e.username = :username AND e.episodeDate BETWEEN :startDate AND :endDate ORDER BY e.episodeDate")
    List<Episode> findByUsernameAndDateRange(@Param("username") String username, 
                                            @Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
}

