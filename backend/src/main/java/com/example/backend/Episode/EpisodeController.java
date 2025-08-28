package com.example.backend.Episode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/user/{username}")
    public List<Episode> getAllEpisodes(@PathVariable String username) {
        return episodeService.getEpisodesPerUser(username);
    }

    @PostMapping("/log")
    public void postEpisode(@RequestBody Episode episode) {
        logger.info("Received POST request at /episodes/log");
        logger.info("Episode received: {}", episode.toString()); 
        episodeService.saveEpisode(episode);
    }

    @GetMapping("/user/{username}/month/{year}/{month}")
    public ResponseEntity<List<Episode>> getEpisodesByMonth(
            @PathVariable String username,
            @PathVariable int year,
            @PathVariable int month) {
        List<Episode> episodes = episodeService.getUserEpisodesByMonth(username, year, month);
        return ResponseEntity.ok(episodes);
    }

    @GetMapping("/user/{username}/date/{date}")
    public ResponseEntity<Episode> getEpisodeByDate(
            @PathVariable String username,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Optional<Episode> episode = episodeService.getEpisodeByDate(username, date);
        return episode.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteEpisode(@PathVariable Integer id) {
    //     episodeService.deleteEpisode(id);
    //     return ResponseEntity.ok().build();
    // }

    // @PutMapping("/{id}")
    // public ResponseEntity<Episode> updateEpisode(@PathVariable Integer id, @RequestBody Episode episode) {
    //     episode.setEpisodeId(id);
    //     try {
    //         Episode updatedEpisode = episodeService.saveOrUpdateEpisode(episode);
    //         return ResponseEntity.ok(updatedEpisode);
    //     } catch (RuntimeException e) {
    //         return ResponseEntity.badRequest().body(null);
    //     }
    // }

}
