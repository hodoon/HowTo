package com.example.HowToProj.service;

import com.example.HowToProj.dto.HabitDto;
import com.example.HowToProj.dto.HabitRecordDto;
import com.example.HowToProj.dto.HabitStatisticsDto;
import com.example.HowToProj.entity.Habit;
import com.example.HowToProj.entity.HabitRecord;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.exception.NotFoundHabitException;
import com.example.HowToProj.repository.HabitRepository;
import com.example.HowToProj.repository.HabitRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HabitTrackingService {

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private HabitRecordRepository habitRecordRepository;

    @Transactional
    public HabitDto createHabit(User user, String habitName, int targetFrequency, LocalDate startDate, LocalDate endDate) {
        Habit habit = Habit.builder()
                .user(user)
                .habitName(habitName)
                .targetFrequency(targetFrequency)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        Habit savedHabit = habitRepository.save(habit);
        return HabitDto.fromEntity(savedHabit);
    }

    @Transactional
    public HabitDto updateHabit(Long habitId, String habitName, int targetFrequency, LocalDate startDate, LocalDate endDate) {
        Optional<Habit> optionalHabit = habitRepository.findById(habitId);

        if (optionalHabit.isPresent()) {
            Habit habit = optionalHabit.get();
            habit.setHabitName(habitName);
            habit.setTargetFrequency(targetFrequency);
            habit.setStartDate(startDate);
            habit.setEndDate(endDate);

            Habit updatedHabit = habitRepository.save(habit);
            return HabitDto.fromEntity(updatedHabit);
        } else {
            throw new NotFoundHabitException("Habit not found with ID: " + habitId);
        }
    }

    @Transactional
    public HabitRecordDto recordHabitAchievement(Long habitId, LocalDate date, boolean achieved) {
        Optional<Habit> optionalHabit = habitRepository.findById(habitId);

        if (optionalHabit.isPresent()) {
            Habit habit = optionalHabit.get();
            HabitRecord habitRecord = HabitRecord.builder()
                    .habit(habit)
                    .date(date)
                    .achieved(achieved)
                    .build();

            HabitRecord savedHabitRecord = habitRecordRepository.save(habitRecord);
            return HabitRecordDto.fromEntity(savedHabitRecord);
        } else {
            throw new NotFoundHabitException("Habit not found with ID: " + habitId);
        }
    }

    public List<HabitDto> getHabitsByUser(User user) {
        List<Habit> habits = habitRepository.findByUser(user);
        return habits.stream().map(HabitDto::fromEntity).collect(Collectors.toList());
    }

    public HabitDto getHabitById(Long habitId) {
        Optional<Habit> optionalHabit = habitRepository.findById(habitId);
        return optionalHabit.map(HabitDto::fromEntity).orElseThrow(() -> new NotFoundHabitException("Habit not found with ID: " + habitId));
    }

    public HabitStatisticsDto getHabitStatistics(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new NotFoundHabitException("Habit not found with ID: " + habitId));

        List<HabitRecord> records = habitRecordRepository.findByHabitIdAndDateBetween(
                habitId, habit.getStartDate(), habit.getEndDate());

        long totalDays = habit.getEndDate().toEpochDay() - habit.getStartDate().toEpochDay() + 1;
        long achievedDays = records.stream().filter(HabitRecord::isAchieved).count();

        double achievementRate = totalDays > 0 ? (double) achievedDays / totalDays * 100 : 0.0;

        return HabitStatisticsDto.builder()
                .habitId(habitId)
                .habitName(habit.getHabitName())
                .totalDays((int) totalDays)
                .achievedDays((int) achievedDays)
                .achievementRate(achievementRate)
                .build();
    }
}
