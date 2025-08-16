package com.example.backend.Episode;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.*;

@Entity
@Table(name = "episodes")
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "episode_id")    
    private int episode_id;

    @Column(name = "user_id")
    private int user_id;

    @Column(name = "episode_date")
    private LocalDate episode_date;

    @Column(name = "trigger_ids")
    private int[] trigger_ids;

    @Column(name = "menstrual_period")
    private boolean menstrual_period;

    @Column(name = "morning_severity")
    private int morning_severity;

    @Column(name = "afternoon_severity")
    private int afternoon_severity;

    @Column(name = "evening_severity")
    private int evening_severity;

    @Column(name = "notes")
    private String notes;

    public Episode(int episode_id, int user_id, LocalDate episode_date, int[] trigger_ids, boolean menstrual_period,
                   int morning_severity, int afternoon_severity, int evening_severity, String notes) {
        this.episode_id = episode_id;
        this.user_id = user_id;
        this.episode_date = episode_date;
        this.trigger_ids = trigger_ids;
        this.menstrual_period = menstrual_period;
        this.morning_severity = morning_severity;
        this.afternoon_severity = afternoon_severity;
        this.evening_severity = evening_severity;
        this.notes = notes;
    }

    public int getEpisodeId() {
        return episode_id;
    }

    public int getUserId() {
        return user_id;
    }

    public LocalDate getEpisodeDate() {
        return episode_date;
    }

    public int[] getTriggerIds() {
        return trigger_ids;
    }

    public boolean isMenstrualPeriod() {
        return menstrual_period;
    }

    public int getMorningSeverity() {
        return morning_severity;
    }

    public int getAfternoonSeverity() {
        return afternoon_severity;
    }

    public int getEveningSeverity() {
        return evening_severity;
    }

    public String getNotes() {
        return notes;
    }
}
