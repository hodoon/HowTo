package com.example.HowToProj.dto;

import com.example.HowToProj.entity.UserActivity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityDto {

    private Long id;
    private Long userId;
    private String activityType;
    private LocalDateTime activityDate;

    public static UserActivityDto fromEntity(UserActivity userActivity) {
        if (userActivity == null) return null;

        return UserActivityDto.builder()
                .id(userActivity.getId())
                .userId(userActivity.getUser().getId()) // User의 ID를 매핑
                .activityType(userActivity.getActivityType())
                .activityDate(userActivity.getActivityDate()) // 필드명 일치시킴
                .build();
    }
}
