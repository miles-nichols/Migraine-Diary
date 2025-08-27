package com.example.backend.MedicineDailyLog;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.example.backend.Episode.*;
// import com.example.backend.Users.*;

@Entity
@Table(name = "medicine_daily_log")
public class MedicineDailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicince_log_id")
    private Long id;

    // Use @ManyToOne to map the episode_id foreign key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @Column(name = "username")  
    private String username; 

    @Column(name = "medicine_log_date")
    private LocalDateTime medicineLogDate;

    @Column(name = "medicine_name")
    private String medicineName;

    @Column(name = "dose")
    private String dose;

    @Column(name = "relief")
    private Integer relief;

    @Column(name = "time_taken")
    private String timeTaken;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MedicineDailyLog() {}

    public MedicineDailyLog(Long id, Episode episode, String username, LocalDateTime medicineLogDate, String medicineName,
                           String dose, Integer relief, String timeTaken, String notes,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.episode = episode;
        this.username = username;
        this.medicineLogDate = medicineLogDate;
        this.medicineName = medicineName;
        this.dose = dose;
        this.relief = relief;
        this.timeTaken = timeTaken;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getMedicineLogDate() {
        return medicineLogDate;
    }

    public void setMedicineLogDate(LocalDateTime medicineLogDate) {
        this.medicineLogDate = medicineLogDate;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public Integer getRelief() {
        return relief;
    }

    public void setRelief(Integer relief) {
        this.relief = relief;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}