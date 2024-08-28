package com.example.HowToProj.dto;

import com.example.HowToProj.entity.Diary;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDto {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String tag;
    private boolean isShared;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<SharedContentDto> sharedContents; // 일기와 관련된 공유된 콘텐츠

    public static DiaryDto fromEntity(Diary diary) {
        if (diary == null) return null;

        return DiaryDto.builder()
                .id(diary.getId())
                .userId(diary.getUser().getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .tag(diary.getTag())
                .isShared(diary.isShared())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .sharedContents(diary.getSharedContents().stream()
                        .map(SharedContentDto::fromEntity)
                        .collect(Collectors.toSet()))
                .build();
    }
}
