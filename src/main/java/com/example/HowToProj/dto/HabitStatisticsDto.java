package com.example.HowToProj.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitStatisticsDto {
    private Long habitId;
    private String habitName;
    private int totalDays;
    private int achievedDays;
    private double achievementRate;
    private int consecutiveDays;
}
