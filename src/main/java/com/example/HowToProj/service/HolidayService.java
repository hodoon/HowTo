package com.example.HowToProj.service;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.Event;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class HolidayService {

    private final Calendar calendarService;

    public HolidayService(Calendar calendarService) {
        this.calendarService = calendarService;
    }

    public List<Event> getKoreanHolidays() throws IOException {
        // 대한민국 공휴일 캘린더 ID
        String calendarId = "ko.south_korea#holiday@group.v.calendar.google.com";

        // 현재 날짜부터 1년간의 공휴일 가져오기
        Events events = calendarService.events().list(calendarId)
                .setTimeMin(new com.google.api.client.util.DateTime(System.currentTimeMillis()))
                .setTimeMax(new com.google.api.client.util.DateTime(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000))) // 1년
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }
}
