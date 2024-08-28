package com.example.HowToProj.dto;

import com.example.HowToProj.entity.Habit;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitDto {
    private Long id;
    private Long userId;
    private String habitName;
    private int targetFrequency;
    private LocalDate startDate;
    private LocalDate endDate;

    public static HabitDto fromEntity(Habit habit) {
        if (habit == null) return null;

        return HabitDto.builder()
                .id(habit.getId())
                .userId(habit.getUser().getId())
                .habitName(habit.getHabitName())
                .targetFrequency(habit.getTargetFrequency())
                .startDate(habit.getStartDate())
                .endDate(habit.getEndDate())
                .build();
    }
}
