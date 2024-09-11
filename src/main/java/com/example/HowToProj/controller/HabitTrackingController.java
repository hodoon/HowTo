package com.example.HowToProj.controller;

import com.example.HowToProj.dto.HabitDto;
import com.example.HowToProj.dto.HabitStatisticsDto;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.service.HabitTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/habits")
public class HabitTrackingController {

    @Autowired
    private HabitTrackingService habitTrackingService;

    @PostMapping
    public HabitDto createHabit(@PathVariable Long userId, @RequestBody HabitDto habitDto) {
        User user = new User();
        user.setId(userId);
        return habitTrackingService.createHabit(user, habitDto.getHabitName(), habitDto.getTargetFrequency(), habitDto.getStartDate(), habitDto.getEndDate());
    }

    @PutMapping("/{habitId}")
    public HabitDto updateHabit(@PathVariable Long habitId, @RequestBody HabitDto habitDto) {
        return habitTrackingService.updateHabit(habitId, habitDto.getHabitName(), habitDto.getTargetFrequency(), habitDto.getStartDate(), habitDto.getEndDate());
    }

    @GetMapping
    public List<HabitDto> getHabitsByUser(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return habitTrackingService.getHabitsByUser(user);
    }

    @GetMapping("/{habitId}")
    public HabitDto getHabitById(@PathVariable Long habitId) {
        return habitTrackingService.getHabitById(habitId);
    }

    @GetMapping("/stats/{habitId}")
    public HabitStatisticsDto getHabitStats(@PathVariable Long habitId) {
        return habitTrackingService.getHabitStats(habitId);
    }
}
