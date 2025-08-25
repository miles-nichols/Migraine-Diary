package com.example.backend.MedicineDailyLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// A Spring Data repository must be an interface that extends JpaRepository
@Repository
public interface MedicineDailyLogRepository extends JpaRepository<MedicineDailyLog, Long> {
}