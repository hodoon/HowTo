package com.example.HowToProj.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedContentDto {

    private Long id;
    private Long userId;
    private String contentType;
    private Long contentIdInType;
    private Long sharedWithUserId;
    private LocalDateTime sharedAt;
    private LocalDateTime updatedAt;
}
