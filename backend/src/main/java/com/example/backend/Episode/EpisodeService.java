package com.example.backend.Episode;
import com.example.backend.MedicineDailyLog.*; 
import com.example.backend.Users.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        @Transactional
    public void saveEpisode(Episode episode) {
        User user = userRepository.findByUsername(episode.getUsername());
        
        if (user != null) {
            episode.setUsername(user.getUsername());
            if (episode.getEpisodeDate() == null) {
                episode.setEpisodeDate(LocalDateTime.now().toLocalDate());
            }

            if (episode.getMedicineLogs() != null && !episode.getMedicineLogs().isEmpty()) {
                LocalDateTime now = LocalDateTime.now();
                
                for (int i = 0; i < episode.getMedicineLogs().size(); i++) {
                    MedicineDailyLog medicine = episode.getMedicineLogs().get(i);
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

    public List<Episode> getEpisodesPerUser(String username) {
        return episodeRepository.findByUsername(username);
    }
    
    public List<Episode> getUserEpisodesByMonth(String username, int year, int month) {
        return episodeRepository.findByUsernameAndMonth(username, year, month);
    }
    
    public Optional<Episode> getEpisodeByDate(String username, LocalDate date) {
        return episodeRepository.findByUsernameAndEpisodeDate(username, date);
    }

}