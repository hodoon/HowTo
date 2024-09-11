package com.example.HowToProj.service;

import com.example.HowToProj.dto.QuizHistoryDto;
import com.example.HowToProj.dto.PointsDto;
import com.example.HowToProj.dto.QuizStatisticsDto;
import com.example.HowToProj.entity.QuizHistory;
import com.example.HowToProj.entity.Points;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.repository.QuizHistoryRepository;
import com.example.HowToProj.repository.PointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizHistoryService {

    @Autowired
    private QuizHistoryRepository quizHistoryRepository;

    @Autowired
    private PointsRepository pointsRepository;

    @Transactional
    public void recordQuizParticipation(User user, Long quizId, String question, String answerGiven, boolean isCorrect) {
        QuizHistory quizHistory = QuizHistory.builder()
                .user(user)
                .quizId(quizId)
                .question(question)
                .answerGiven(answerGiven)
                .isCorrect(isCorrect)
                .participationDate(LocalDateTime.now())
                .build();

        quizHistoryRepository.save(quizHistory);

        if (isCorrect) {
            updatePoints(user, 10); // 정답일 경우 10포인트 부여
        }
    }

    public List<QuizHistoryDto> getQuizHistoriesByUser(User user) {
        List<QuizHistory> quizHistories = quizHistoryRepository.findByUser(user);
        return quizHistories.stream().map(QuizHistoryDto::fromEntity).collect(Collectors.toList());
    }

    public List<QuizHistoryDto> getCorrectQuizHistoriesByUser(User user) {
        List<QuizHistory> quizHistories = quizHistoryRepository.findByUserAndIsCorrect(user, true);
        return quizHistories.stream().map(QuizHistoryDto::fromEntity).collect(Collectors.toList());
    }

    private void updatePoints(User user, int pointsToAdd) {
        Optional<Points> optionalPoints = pointsRepository.findByUser(user);

        Points points = optionalPoints.orElseGet(() -> Points.builder()
                .user(user)
                .totalPoints(0)
                .build());

        points.setTotalPoints(points.getTotalPoints() + pointsToAdd);
        pointsRepository.save(points);
    }

    public PointsDto getPointsByUser(User user) {
        Points points = pointsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Points not found for user ID: " + user.getId()));

        return PointsDto.fromEntity(points);
    }

    public QuizStatisticsDto getQuizStats(Long userId) {
        User user = new User();
        user.setId(userId);

        List<QuizHistory> history = quizHistoryRepository.findByUser(user);

        if (history.isEmpty()) {
            return new QuizStatisticsDto(0,0 ,0.0); // 기본값을 반환
        }

        int totalQuizzes = history.size();
        long correctAnswers = history.stream().filter(QuizHistory::isCorrect).count();
        double correctPercentage = (totalQuizzes > 0) ? (double) correctAnswers / totalQuizzes * 100 : 0.0;

        return QuizStatisticsDto.builder()
                .totalQuizzes(totalQuizzes)
                .correctAnswerPercentage(correctPercentage)
                .build();
    }

}
