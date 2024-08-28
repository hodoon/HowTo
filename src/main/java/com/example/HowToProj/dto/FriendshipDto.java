package com.example.HowToProj.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipDto {

    private Long id;
    private Long userId;
    private Long friendId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
