package com.example.backend.Episode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*; 

@RestController
@RequestMapping("/episodes")
public class EpisodeController {

    // This class will handle HTTP requests related to episodes
    // You can define endpoints for creating, retrieving, updating, and deleting episodes here

    @Autowired
    private EpisodeRepository episodeRepository;

    @GetMapping("/{userId}")
    public List<Episode> getAllEpisodes(@PathVariable int userId) {
        return episodeRepository.findByUserId(userId);
    }
}
