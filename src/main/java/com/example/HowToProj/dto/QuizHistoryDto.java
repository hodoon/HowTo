package com.example.HowToProj.dto;

import com.example.HowToProj.entity.QuizHistory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizHistoryDto {

    private Long id;
    private Long userId;
    private Long quizId;
    private String question;
    private String answerGiven;
    private boolean isCorrect;
    private LocalDateTime participationDate;

    public static QuizHistoryDto fromEntity(QuizHistory quizHistory) {
        if (quizHistory == null) return null;

        return QuizHistoryDto.builder()
                .id(quizHistory.getId())
                .userId(quizHistory.getUser().getId())
                .quizId(quizHistory.getQuizId())
                .question(quizHistory.getQuestion())
                .answerGiven(quizHistory.getAnswerGiven())
                .isCorrect(quizHistory.isCorrect())
                .participationDate(quizHistory.getParticipationDate())
                .build();
    }
}
