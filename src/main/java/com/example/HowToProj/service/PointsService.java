package com.example.HowToProj.service;

import com.example.HowToProj.dto.PointsDto;
import com.example.HowToProj.dto.PointsHistoryDto;
import com.example.HowToProj.entity.Points;
import com.example.HowToProj.entity.PointsHistory;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.exception.NotFoundMemberException;
import com.example.HowToProj.repository.PointsRepository;
import com.example.HowToProj.repository.PointsHistoryRepository;
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
    public void usePoints(User user, int pointsToUse, String description) {
        Points points = pointsRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundMemberException("User not found"));

        if (points.getTotalPoints() < pointsToUse) {
            throw new RuntimeException("Not enough points");
        }

        points.setTotalPoints(points.getTotalPoints() - pointsToUse);
        pointsRepository.save(points);

        // Save points history
        PointsHistory pointsHistory = PointsHistory.builder()
                .user(user)
                .pointsChange(-pointsToUse)
                .description(description)
                .changeDate(LocalDateTime.now())
                .build();

        pointsHistoryRepository.save(pointsHistory);
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
}
