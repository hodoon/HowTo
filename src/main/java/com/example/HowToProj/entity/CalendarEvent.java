package com.example.HowToProj.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_events")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(name = "calendar_id", nullable = false)
    private String calendarId; // Google Calendar ID

    @Column(name = "google_event_id", nullable = false)
    private String eventId; // Google Calendar Event ID

    @Column(name = "summary", length = 255)
    private String summary; // Event Summary (e.g., Holiday Name)

    @Column(name = "description", length = 1000)
    private String description; // Event Description

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // Event Start Time

    @Column(name = "end_time")
    private LocalDateTime endTime; // Event End Time (optional)

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Entity creation timestamp

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Entity update timestamp
}
