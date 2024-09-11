package com.example.HowToProj.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizStatisticsDto {
    private int totalQuizzes;
    private int totalPoints;
    private double correctAnswerPercentage;
}
