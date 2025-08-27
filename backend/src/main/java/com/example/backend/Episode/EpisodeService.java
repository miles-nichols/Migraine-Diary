package com.example.backend.Episode;
import com.example.backend.MedicineDailyLog.*; 
import com.example.backend.Users.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
            episode.setUsername(user.getUsername());
            if (episode.getEpisodeDate() == null) {
                episode.setEpisodeDate(LocalDateTime.now().toLocalDate());
            }

            if (episode.getMedicines() != null && !episode.getMedicines().isEmpty()) {
                LocalDateTime now = LocalDateTime.now();
                
                for (int i = 0; i < episode.getMedicines().size(); i++) {
                    MedicineDailyLog medicine = episode.getMedicines().get(i);
                    medicine.setEpisode(episode);
                    medicine.setUsername(user.getUsername());
                    medicine.setCreatedAt(now);
                    medicine.setUpdatedAt(now);
                    if (medicine.getMedicineLogDate() == null) {
                        medicine.setMedicineLogDate(now);
                    }

                    // CRITICAL: Ensure time_taken is never null or empty
                    if (medicine.getTimeTaken() == null || medicine.getTimeTaken().trim().isEmpty()) {
                        // Use current time with unique offset for each medicine
                        LocalTime uniqueTime = LocalTime.now().plusMinutes(i);
                        medicine.setTimeTaken(uniqueTime.toString());
                    }
                }
            }
            episodeRepository.save(episode);
        } else {
            throw new RuntimeException("User not found with username: " + episode.getUsername());
        }
    }
}