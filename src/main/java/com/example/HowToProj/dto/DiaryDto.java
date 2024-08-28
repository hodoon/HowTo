package com.example.HowToProj.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDto {

    private Long id;
    private Long userId;
    private String content;
    private String tag;
    private boolean isShared;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
