package com.example.HowToProj.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "points_history")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class PointsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "points_history_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 포인트를 획득하거나 사용한 사용자

    @Column(name = "points_change", nullable = false)
    private int pointsChange; // 포인트의 증가 또는 감소

    @Column(name = "description", length = 255)
    private String description; // 포인트의 획득 또는 사용에 대한 설명

    @CreatedDate
    @Column(name = "change_date", updatable = false)
    private LocalDateTime changeDate; // 포인트 변화가 발생한 날짜
}
