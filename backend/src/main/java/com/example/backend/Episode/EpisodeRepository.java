package com.example.backend.Episode;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

//Jpa already has a ton of built-in methods for operations (define JpaRepository<Episode, Integer>)

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Integer> {

    List<Episode> findByUserId(int userId);
}
