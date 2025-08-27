package com.example.backend.Episode;

import java.time.LocalDate;
import jakarta.persistence.*;
import java.util.*;

import com.example.backend.MedicineDailyLog.MedicineDailyLog;

@Entity
@Table(name = "episodes")
public class Episode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "episode_id")    
    private int episodeId;

    @Column(name = "username")  
    private String username;       

    @Column(name = "episode_date")
    private LocalDate episodeDate;

   @Column(name = "trigger_ids")
    private Integer[] triggerIds;

    @Column(name = "menstrual_period")
    private boolean menstrualPeriod;

    @Column(name = "morning_severity")
    private int morningSeverity;

    @Column(name = "afternoon_severity")
    private int afternoonSeverity;

    @Column(name = "evening_severity")
    private int eveningSeverity;

    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicineDailyLog> medicines;

    // No-argument constructor REQUIRED by JPA
    public Episode() {
    }

    // All-argument constructor
    public Episode(int episodeId, String username, LocalDate episodeDate, Integer[] triggerIds, boolean menstrualPeriod,
                   int morningSeverity, int afternoonSeverity, int eveningSeverity, String notes) {
        this.episodeId = episodeId;
        this.username = username;
        this.episodeDate = episodeDate;
        this.triggerIds = triggerIds;
        this.menstrualPeriod = menstrualPeriod;
        this.morningSeverity = morningSeverity;
        this.afternoonSeverity = afternoonSeverity;
        this.eveningSeverity = eveningSeverity;
        this.notes = notes;
    }

    // Getter and Setter methods
    public int getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getEpisodeDate() {
        return episodeDate;
    }

    public void setEpisodeDate(LocalDate episodeDate) {
        this.episodeDate = episodeDate;
    }

    public Integer[] getTriggerIds() {
        return triggerIds;
    }

    public void setTriggerIds(Integer[] triggerIds) {
        this.triggerIds = triggerIds;
    }

    public boolean isMenstrualPeriod() {
        return menstrualPeriod;
    }

    public void setMenstrualPeriod(boolean menstrualPeriod) {
        this.menstrualPeriod = menstrualPeriod;
    }

    public int getMorningSeverity() {
        return morningSeverity;
    }

    public void setMorningSeverity(int morningSeverity) {
        this.morningSeverity = morningSeverity;
    }

    public int getAfternoonSeverity() {
        return afternoonSeverity;
    }

    public void setAfternoonSeverity(int afternoonSeverity) {
        this.afternoonSeverity = afternoonSeverity;
    }

    public int getEveningSeverity() {
        return eveningSeverity;
    }

    public void setEveningSeverity(int eveningSeverity) {
        this.eveningSeverity = eveningSeverity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

     public List<MedicineDailyLog> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<MedicineDailyLog> medicines) {
        this.medicines = medicines;
    }
}