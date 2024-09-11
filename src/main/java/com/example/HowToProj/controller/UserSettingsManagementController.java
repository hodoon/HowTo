package com.example.HowToProj.controller;

import com.example.HowToProj.dto.UserSettingsDto;
import com.example.HowToProj.service.UserSettingsManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/settings")
public class UserSettingsManagementController {

    @Autowired
    private UserSettingsManagementService userSettingsManagementService;

    @GetMapping
    public UserSettingsDto getUserSettings(@PathVariable Long userId) {
        return userSettingsManagementService.getUserSettings(userId);
    }

    @PutMapping
    public UserSettingsDto updateUserSettings(@PathVariable Long userId, @RequestBody UserSettingsDto userSettingsDto) {
        return userSettingsManagementService.updateUserSettings(userId, userSettingsDto);
    }
}
