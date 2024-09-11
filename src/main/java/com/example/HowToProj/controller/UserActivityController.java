package com.example.HowToProj.controller;

import com.example.HowToProj.dto.UserActivityDto;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.service.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/activities")
public class UserActivityController {

    @Autowired
    private UserActivityService userActivityService;

    @PostMapping
    public void recordUserActivity(@PathVariable Long userId, @RequestParam String activityType) {
        User user = new User();
        user.setId(userId);
        userActivityService.recordUserActivity(user, activityType);
    }

    @GetMapping
    public List<UserActivityDto> getUserActivities(@PathVariable Long userId) {
        return userActivityService.getUserActivities(userId);
    }
}
