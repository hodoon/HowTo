package com.example.HowToProj.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shared_content")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class SharedContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 콘텐츠를 소유한 사용자

    @ManyToOne
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary; // 공유된 일기

    @Column(name = "content_type", length = 50, nullable = false)
    private String contentType; // 콘텐츠의 유형 (예: "DIARY")

    @Column(name = "content_id_in_type", nullable = false)
    private Long contentIdInType; // 콘텐츠 ID (예: 일기 ID)

    @ManyToOne
    @JoinColumn(name = "shared_with_user_id", nullable = false)
    private User sharedWith; // 공유된 사용자

    @CreatedDate
    @Column(name = "shared_at", updatable = false)
    private LocalDateTime sharedAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
