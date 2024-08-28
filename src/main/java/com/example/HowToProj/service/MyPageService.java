package com.example.HowToProj.service;

import com.example.HowToProj.dto.UserActivityDto;
import com.example.HowToProj.dto.UserDto;
import com.example.HowToProj.dto.UserSettingsDto;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.entity.UserActivity;
import com.example.HowToProj.entity.UserSettings;
import com.example.HowToProj.exception.NotFoundMemberException;
import com.example.HowToProj.repository.UserActivityRepository;
import com.example.HowToProj.repository.UserRepository;
import com.example.HowToProj.repository.UserSettingsRepository;
import com.example.HowToProj.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyPageService {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final UserActivityRepository userActivityRepository;

    public MyPageService(UserRepository userRepository, UserSettingsRepository userSettingsRepository, UserActivityRepository userActivityRepository) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.userActivityRepository = userActivityRepository;
    }

    @Transactional(readOnly = true)
    public UserDto getUserDetails() {
        String currentUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new NotFoundMemberException("현재 로그인한 사용자를 찾을 수 없습니다."));

        User user = userRepository.findOneWithAuthoritiesByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundMemberException("사용자를 찾을 수 없습니다."));

        return UserDto.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserSettingsDto getUserSettings() {
        String currentUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new NotFoundMemberException("현재 로그인한 사용자를 찾을 수 없습니다."));

        User user = userRepository.findOneWithAuthoritiesByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundMemberException("사용자를 찾을 수 없습니다."));

        UserSettings userSettings = userSettingsRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundMemberException("사용자 설정을 찾을 수 없습니다."));

        return UserSettingsDto.fromEntity(userSettings);
    }

    @Transactional(readOnly = true)
    public List<UserActivityDto> getUserActivities() {
        String currentUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new NotFoundMemberException("현재 로그인한 사용자를 찾을 수 없습니다."));

        User user = userRepository.findOneWithAuthoritiesByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundMemberException("사용자를 찾을 수 없습니다."));

        List<UserActivity> activities = userActivityRepository.findByUserId(user.getId());
        return activities.stream()
                .map(this::toUserActivityDto)
                .collect(Collectors.toList());
    }

    private UserActivityDto toUserActivityDto(UserActivity userActivity) {
        return UserActivityDto.builder()
                .id(userActivity.getId())
                .userId(userActivity.getUser().getId())
                .activityType(userActivity.getActivityType())
                .activityDate(userActivity.getActivityDate())
                .build();
    }
}
