package com.example.backend.Episode;
import com.example.backend.MedicineDailyLog.*; 
import com.example.backend.Users.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*; 

@Service
public class EpisodeService {

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicineDailyLogRepository medicineDailyLogRepository;

    public EpisodeService(EpisodeRepository episodeRepository, UserRepository userRepository, MedicineDailyLogRepository medicineDailyLogRepository) {
        this.episodeRepository = episodeRepository;
        this.userRepository = userRepository;
        this.medicineDailyLogRepository = medicineDailyLogRepository;
    }

    public List<Episode> getEpisodesPerUser(String username) {
        return episodeRepository.findByUsername(username);
    }

    @Transactional
    public void saveEpisode(Episode episode) {
        User user = userRepository.findByUsername(episode.getUsername());
        if (user != null) {
            if (episode.getMedicines() != null && !episode.getMedicines().isEmpty()) {
                for (MedicineDailyLog medicine : episode.getMedicines()) {
                    medicine.setEpisode(episode);
                    medicine.setUser(user);
                    medicine.setCreatedAt(LocalDateTime.now());
                    medicine.setUpdatedAt(LocalDateTime.now());
                }
            }
            
            // This is the CRUCIAL line you were missing!
            episodeRepository.save(episode);
        }
        else {
            throw new RuntimeException("User not found with id: " + episode.getUsername());
        }
    }
}
