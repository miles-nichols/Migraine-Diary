package com.example.backend.Episode;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Integer> {
    List<Episode> findByUsername(String username);
}
