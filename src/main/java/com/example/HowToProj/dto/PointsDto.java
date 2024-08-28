package com.example.HowToProj.dto;

import com.example.HowToProj.entity.Points;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsDto {

    private Long id;
    private Long userId;
    private int totalPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PointsDto fromEntity(Points points) {
        if (points == null) return null;

        return PointsDto.builder()
                .id(points.getId())
                .userId(points.getUser().getId())
                .totalPoints(points.getTotalPoints())
                .createdAt(points.getCreatedAt())
                .updatedAt(points.getUpdatedAt())
                .build();
    }
}
