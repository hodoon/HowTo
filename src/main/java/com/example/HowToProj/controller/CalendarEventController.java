package com.example.HowToProj.controller;

import com.example.HowToProj.dto.CalendarEventDto;
import com.example.HowToProj.service.CalendarEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar-events")
public class CalendarEventController {

    @Autowired
    private CalendarEventService calendarEventService;

    @PostMapping
    public CalendarEventDto createOrUpdateEvent(@RequestBody CalendarEventDto calendarEventDto) {
        return calendarEventService.createOrUpdateEvent(calendarEventDto);
    }

    @GetMapping("/{eventId}")
    public CalendarEventDto getEventById(@PathVariable Long eventId) {
        return calendarEventService.getEventById(eventId);
    }

    @GetMapping
    public List<CalendarEventDto> getAllEvents() {
        return calendarEventService.getAllEvents();
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable Long eventId) {
        calendarEventService.deleteEvent(eventId);
    }
}