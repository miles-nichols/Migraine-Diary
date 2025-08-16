package com.example.backend.Episode;


import org.springframework.beans.factory.annotation.Autowired;
import java.util.*; 

public class EpisodeService {

    @Autowired
    private EpisodeRepository episodeRepository;

    public List<Episode> getAllEpisodes(int userId) {
        return episodeRepository.findByUserId(userId);
    }
}
