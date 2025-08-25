package com.example.backend.Episode;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/episodes")
public class EpisodeController {

    // This class will handle HTTP requests related to episodes
    // You can define endpoints for creating, retrieving, updating, and deleting episodes here

    private static final Logger logger = LoggerFactory.getLogger(EpisodeController.class);

    // @Autowired
    // private EpisodeRepository episodeRepository;
    @Autowired
    private EpisodeService episodeService;

    @GetMapping("/{username}")
    public List<Episode> getAllEpisodes(@PathVariable String username) {
        return episodeService.getEpisodesPerUser(username);
    }

    @PostMapping("/log")
    public void postEpisode(@RequestBody Episode episode) {
        logger.info("Received POST request at /episodes/log");
        logger.info("Episode received: {}", episode.toString()); 
        episodeService.saveEpisode(episode);
    }
}
