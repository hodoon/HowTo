package com.example.HowToProj.dto;

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
    private int points;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
