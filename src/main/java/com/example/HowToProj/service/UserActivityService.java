package com.example.HowToProj.service;

import com.example.HowToProj.dto.UserActivityDto;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.entity.UserActivity;
import com.example.HowToProj.exception.NotFoundMemberException;
import com.example.HowToProj.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;

    @Autowired
    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    @Transactional
    public void recordUserActivity(User userId, String activityType) {
        // 활동 기록 엔티티 생성
        UserActivity userActivity = UserActivity.builder()
                .user(userId)
                .activityType(activityType)
                .activityDate(LocalDateTime.now())
                .build();

        // 저장
        userActivityRepository.save(userActivity);
    }

    @Transactional(readOnly = true)
    public List<UserActivityDto> getUserActivities(Long userId) {
        List<UserActivity> userActivities = userActivityRepository.findByUserId(userId);
        if (userActivities.isEmpty()) {
            throw new NotFoundMemberException("No activities found for user id: " + userId);
        }
        return userActivities.stream()
                .map(UserActivityDto::fromEntity)
                .toList();
    }

    // 활동 분석 기능 예시 (일일 로그인 수 분석)
    @Transactional(readOnly = true)
    public long countUserLogins(Long userId) {
        // 'LOGIN'이라는 activityType으로 필터링하여 로그인 수를 카운트
        return userActivityRepository.findByUserId(userId).stream()
                .filter(activity -> "LOGIN".equals(activity.getActivityType()))
                .count();
    }
}
