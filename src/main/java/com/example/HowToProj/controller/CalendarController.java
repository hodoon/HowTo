package com.example.HowToProj.controller;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.Event;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@Tag(name = "Calendar API", description = "Operations related to Google Calendar")
public class CalendarController {

    @Autowired
    private Calendar googleCalendarService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    // 대한민국 공휴일 캘린더 ID
    private static final String HOLIDAY_CALENDAR_ID = "ko.south_korea#holiday@group.v.calendar.google.com";

    @GetMapping("/holidays")
    public ResponseEntity<List<Event>> getHolidays(OAuth2AuthenticationToken authentication) throws IOException {
        // OAuth2AuthorizedClient를 통해 Access Token을 가져옴
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        // 현재 날짜와 1년 후 날짜 설정
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime oneYearLater = new DateTime(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)); // 1년 후

        // 공휴일 이벤트 가져오기
        Events events = googleCalendarService.events().list(HOLIDAY_CALENDAR_ID)
                .setTimeMin(now)
                .setTimeMax(oneYearLater)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> holidayEvents = events.getItems();

        return ResponseEntity.ok(holidayEvents);
    }
}
