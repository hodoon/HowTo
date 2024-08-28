package com.example.HowToProj.dto;

import com.example.HowToProj.entity.PointsHistory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsHistoryDto {

    private Long id;
    private Long userId;
    private int pointsChange;
    private String description;
    private LocalDateTime changeDate;

    public static PointsHistoryDto fromEntity(PointsHistory pointsHistory) {
        if (pointsHistory == null) return null;

        return PointsHistoryDto.builder()
                .id(pointsHistory.getId())
                .userId(pointsHistory.getUser().getId())
                .pointsChange(pointsHistory.getPointsChange())
                .description(pointsHistory.getDescription())
                .changeDate(pointsHistory.getChangeDate())
                .build();
    }
}
