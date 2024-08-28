package com.example.HowToProj.dto;

import com.example.HowToProj.entity.UserSettings;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsDto {

    private Long id;
    private Long userId;
    private boolean notificationEnabled; // 필드명 수정
    private String theme;
    private String language;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserSettingsDto fromEntity(UserSettings userSetting) {
        if (userSetting == null) return null;

        return UserSettingsDto.builder()
                .id(userSetting.getId())
                .userId(userSetting.getUser().getId()) // User의 ID를 매핑
                .notificationEnabled(userSetting.isNotificationEnabled()) // 필드명 수정
                .theme(userSetting.getTheme())
                .language(userSetting.getLanguage())
                .createdAt(userSetting.getCreatedAt())
                .updatedAt(userSetting.getUpdatedAt())
                .build();
    }
}
