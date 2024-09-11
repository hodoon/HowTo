package com.example.HowToProj.service;

import com.example.HowToProj.dto.PointsDto;
import com.example.HowToProj.dto.PointsHistoryDto;
import com.example.HowToProj.dto.QuizStatisticsDto;
import com.example.HowToProj.entity.Points;
import com.example.HowToProj.entity.PointsHistory;
import com.example.HowToProj.entity.QuizHistory;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.exception.NotFoundMemberException;
import com.example.HowToProj.repository.PointsRepository;
import com.example.HowToProj.repository.PointsHistoryRepository;
import com.example.HowToProj.repository.QuizHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PointsService {

    @Autowired
    private PointsRepository pointsRepository;

    @Autowired
    private PointsHistoryRepository pointsHistoryRepository;

    @Autowired
    private QuizHistoryRepository quizHistoryRepository;

    @Transactional
    public void addPoints(User user, int pointsToAdd, String description) {
        Points points = pointsRepository.findByUser(user)
                .orElseGet(() -> Points.builder()
                        .user(user)
                        .totalPoints(0)
                        .build());

        points.setTotalPoints(points.getTotalPoints() + pointsToAdd);
        pointsRepository.save(points);

        // Save points history
        PointsHistory pointsHistory = PointsHistory.builder()
                .user(user)
                .pointsChange(pointsToAdd)
                .description(description)
                .changeDate(LocalDateTime.now())
                .build();

        pointsHistoryRepository.save(pointsHistory);
    }

    @Transactional
    public PointsDto usePoints(User user, int pointsToUse, String description) {
        Points points = pointsRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundMemberException("User not found"));

        if (points.getTotalPoints() < pointsToUse) {
            throw new RuntimeException("Not enough points");
        }

        points.setTotalPoints(points.getTotalPoints() - pointsToUse);
        pointsRepository.save(points);

        // 포인트 사용 내역 저장
        PointsHistory pointsHistory = PointsHistory.builder()
                .user(user)
                .pointsChange(-pointsToUse)
                .description(description)
                .changeDate(LocalDateTime.now())
                .build();

        pointsHistoryRepository.save(pointsHistory);

        // 포인트 DTO 반환
        return PointsDto.fromEntity(points);
    }

    public PointsDto getPointsByUser(User user) {
        Points points = pointsRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundMemberException("User not found"));

        return PointsDto.fromEntity(points);
    }

    public List<PointsHistoryDto> getPointsHistoryByUser(User user) {
        List<PointsHistory> pointsHistories = pointsHistoryRepository.findByUser(user);
        return pointsHistories.stream().map(PointsHistoryDto::fromEntity).collect(Collectors.toList());
    }

    public QuizStatisticsDto getQuizStats(Long userId) {
        User user = new User();
        user.setId(userId);

        // QuizHistory에서 사용자 퀴즈 기록을 가져옴
        List<QuizHistory> history = quizHistoryRepository.findByUser(user);

        if (history.isEmpty()) {
            return QuizStatisticsDto.builder()
                    .totalQuizzes(0)
                    .totalPoints(0)
                    .correctAnswerPercentage(0.0)
                    .build(); // 기본값 반환
        }

        int totalQuizzes = history.size();
        long correctAnswers = history.stream().filter(QuizHistory::isCorrect).count();
        double correctPercentage = (double) correctAnswers / totalQuizzes * 100;

        // PointsHistory에서 사용자의 총 포인트를 계산
        List<PointsHistory> pointsHistoryList = pointsHistoryRepository.findByUser(user);
        int totalPoints = pointsHistoryList.stream().mapToInt(PointsHistory::getPointsChange).sum();

        return QuizStatisticsDto.builder()
                .totalQuizzes(totalQuizzes)
                .totalPoints(totalPoints)
                .correctAnswerPercentage(correctPercentage)
                .build();
    }

}
