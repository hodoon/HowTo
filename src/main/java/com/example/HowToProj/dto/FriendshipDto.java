package com.example.HowToProj.dto;

import com.example.HowToProj.entity.Friendship;
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
    private Friendship.FriendshipStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FriendshipDto fromEntity(Friendship friendship) {
        if (friendship == null) return null;

        return FriendshipDto.builder()
                .id(friendship.getId())
                .userId(friendship.getUser().getId())
                .friendId(friendship.getFriend().getId())
                .status(friendship.getStatus())
                .createdAt(friendship.getCreatedAt())
                .updatedAt(friendship.getUpdatedAt())
                .build();
    }
}
