package com.example.HowToProj.service;

import com.example.HowToProj.dto.CalendarEventDto;
import com.example.HowToProj.entity.CalendarEvent;
import com.example.HowToProj.repository.CalendarEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CalendarEventService {

    @Autowired
    private CalendarEventRepository calendarEventRepository;

    @Transactional
    public CalendarEventDto createOrUpdateEvent(CalendarEventDto calendarEventDto) {
        CalendarEvent event = calendarEventDto.toEntity();
        CalendarEvent savedEvent = calendarEventRepository.save(event);
        return CalendarEventDto.fromEntity(savedEvent);
    }

    public CalendarEventDto getEventById(Long eventId) {
        Optional<CalendarEvent> event = calendarEventRepository.findById(eventId);
        return event.map(CalendarEventDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public List<CalendarEventDto> getAllEvents() {
        List<CalendarEvent> events = calendarEventRepository.findAll();
        return events.stream().map(CalendarEventDto::fromEntity).toList();
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        calendarEventRepository.deleteById(eventId);
    }
}
