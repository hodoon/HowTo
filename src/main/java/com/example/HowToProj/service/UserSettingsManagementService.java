package com.example.HowToProj.service;

import com.example.HowToProj.dto.UserSettingsDto;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.entity.UserSettings;
import com.example.HowToProj.exception.NotFoundMemberException;
import com.example.HowToProj.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserSettingsManagementService {

    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public UserSettingsManagementService(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    @Transactional(readOnly = true)
    public UserSettingsDto getUserSettings(Long userId) {
        UserSettings userSettings = userSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundMemberException("User settings not found for user id: " + userId));
        return UserSettingsDto.fromEntity(userSettings);
    }

    @Transactional
    public UserSettingsDto updateUserSettings(Long userId, UserSettingsDto userSettingsDto) {
        UserSettings userSettings = userSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundMemberException("User settings not found for user id: " + userId));

        // 사용자 설정 업데이트
        userSettings.setNotificationEnabled(userSettingsDto.isNotificationEnabled());
        userSettings.setTheme(userSettingsDto.getTheme());
        userSettings.setLanguage(userSettingsDto.getLanguage());
        userSettings.setUpdatedAt(LocalDateTime.now());

        // 저장
        UserSettings updatedUserSettings = userSettingsRepository.save(userSettings);
        return UserSettingsDto.fromEntity(updatedUserSettings);
    }

    @Transactional
    public UserSettings updateNotificationSettings(Long userId, boolean notificationEnabled) {
        Optional<UserSettings> optionalSettings = userSettingsRepository.findByUserId(userId);

        UserSettings settings = optionalSettings.orElseGet(() -> UserSettings.builder()
                .userId(User.builder().id(userId).build()) // Placeholder for user
                .notificationEnabled(notificationEnabled)
                .build());

        settings.setNotificationEnabled(notificationEnabled);
        return userSettingsRepository.save(settings);
    }



    @Transactional
    public UserSettingsDto createUserSettings(User userId, UserSettingsDto userSettingsDto) {
        // 새로운 사용자 설정 엔티티 생성
        UserSettings userSettings = UserSettings.builder()
                .userId(userId)
                .notificationEnabled(userSettingsDto.isNotificationEnabled())
                .theme(userSettingsDto.getTheme())
                .language(userSettingsDto.getLanguage())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 저장
        UserSettings newUserSettings = userSettingsRepository.save(userSettings);
        return UserSettingsDto.fromEntity(newUserSettings);
    }
}
