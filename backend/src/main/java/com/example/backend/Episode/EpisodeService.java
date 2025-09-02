package com.example.backend.Episode;

import com.example.backend.MedicineDailyLog.MedicineDailyLog;
import com.example.backend.MedicineDailyLog.MedicineDailyLogRepository;
import com.example.backend.Users.User;
import com.example.backend.Users.UserRepository;
import org.springframework.transaction.annotation.Transactional; // Use the Spring Transactional annotation
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
    System.out.println("Received episode for username: " + episode.getUsername());
    System.out.println("Number of medicine logs received: " + (episode.getMedicineLogs() != null ? episode.getMedicineLogs().size() : 0));

    
    if (user != null) {
        episode.setUsername(user.getUsername());
        if (episode.getEpisodeDate() == null) {
            episode.setEpisodeDate(LocalDateTime.now().toLocalDate());
        }

        if (episode.getMedicineLogs() != null && !episode.getMedicineLogs().isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            
            for (MedicineDailyLog medicine : episode.getMedicineLogs()) {
                medicine.setEpisode(episode);
                medicine.setUsername(user.getUsername());
                medicine.setCreatedAt(now);
                medicine.setUpdatedAt(now);
                if (medicine.getMedicineLogDate() == null) {
                    medicine.setMedicineLogDate(now);
                }

                if (medicine.getTimeTaken() == null || medicine.getTimeTaken().trim().isEmpty()) {
                    medicine.setTimeTaken(LocalTime.now().toString());
                }
            }
        }
        
        try {
            episodeRepository.save(episode);
        } catch (Exception e) {
            System.err.println("--- FATAL ERROR: FAILED TO SAVE EPISODE ---");
        e.printStackTrace();
        System.err.println("--- END OF ERROR ---");
        throw new RuntimeException("Failed to save episode", e);
    }

    } else {
        throw new RuntimeException("User not found with username: " + episode.getUsername());
    }
}

    public List<Episode> getEpisodesPerUser(String username) {
        return episodeRepository.findByUsername(username);
    }
    
     @Transactional(readOnly = true)
    public List<Episode> getUserEpisodesByMonth(String username, int year, int month) {
        List<Episode> episodes = episodeRepository.findByUsernameAndMonth(username, year, month);
        for (Episode episode : episodes) {
            episode.getMedicineLogs().size(); 
        }
        return episodes;
    }
    
    public Optional<Episode> getEpisodeByDate(String username, LocalDate date) {
        return episodeRepository.findByUsernameAndEpisodeDate(username, date);
    }
}