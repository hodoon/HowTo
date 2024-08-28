package com.example.HowToProj.dto;

import com.example.HowToProj.entity.SharedContent;
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
    private Long diaryId; // 공유된 일기의 ID
    private String contentType;
    private Long contentIdInType;
    private Long sharedWithUserId;
    private LocalDateTime sharedAt;
    private LocalDateTime updatedAt;

    public static SharedContentDto fromEntity(SharedContent sharedContent) {
        if (sharedContent == null) return null;

        return SharedContentDto.builder()
                .id(sharedContent.getId())
                .userId(sharedContent.getUser().getId())
                .diaryId(sharedContent.getDiary().getId()) // 공유된 일기 ID
                .contentType(sharedContent.getContentType())
                .contentIdInType(sharedContent.getContentIdInType())
                .sharedWithUserId(sharedContent.getSharedWith().getId())
                .sharedAt(sharedContent.getSharedAt())
                .updatedAt(sharedContent.getUpdatedAt())
                .build();
    }
}
