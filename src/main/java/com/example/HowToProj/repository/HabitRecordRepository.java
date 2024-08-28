package com.example.HowToProj.repository;

import com.example.HowToProj.entity.HabitRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HabitRecordRepository extends JpaRepository<HabitRecord, Long> {
    List<HabitRecord> findByHabitIdAndDateBetween(Long habitId, LocalDate startDate, LocalDate endDate);
}
