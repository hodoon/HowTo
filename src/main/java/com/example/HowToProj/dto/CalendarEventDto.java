package com.example.HowToProj.dto;

import com.example.HowToProj.entity.CalendarEvent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CalendarEventDto {

    private Long id;
    private String calendarId;
    private String eventId;
    private String summary;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CalendarEventDto fromEntity(CalendarEvent event) {
        return CalendarEventDto.builder()
                .id(event.getId())
                .calendarId(event.getCalendarId())
                .eventId(event.getEventId())
                .summary(event.getSummary())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    public CalendarEvent toEntity() {
        return CalendarEvent.builder()
                .id(this.id)
                .calendarId(this.calendarId)
                .eventId(this.eventId)
                .summary(this.summary)
                .description(this.description)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
