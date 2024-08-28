package com.example.HowToProj.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_history")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class QuizHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_history_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 퀴즈를 참여한 사용자

    @Column(name = "quiz_id", nullable = false)
    private Long quizId; // 퀴즈 ID

    @Column(name = "question", length = 500, nullable = false)
    private String question; // 퀴즈 질문

    @Column(name = "answer_given", length = 500, nullable = false)
    private String answerGiven; // 사용자가 제공한 답변

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect; // 답변 정답 여부

    @CreatedDate
    @Column(name = "participation_date", updatable = false)
    private LocalDateTime participationDate; // 참여 날짜
}
