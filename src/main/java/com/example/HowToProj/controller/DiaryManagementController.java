package com.example.HowToProj.controller;

import com.example.HowToProj.dto.DiaryDto;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.service.DiaryManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/diaries")
public class DiaryManagementController {

    @Autowired
    private DiaryManagementService diaryManagementService;

    @PostMapping
    public DiaryDto createDiary(@PathVariable Long userId, @RequestBody DiaryDto diaryDto) {
        User user = new User();
        user.setId(userId);
        return diaryManagementService.createDiary(user, diaryDto.getTitle(), diaryDto.getContent(), diaryDto.getTag(), diaryDto.isShared());
    }

    @PutMapping("/{diaryId}")
    public DiaryDto updateDiary(@PathVariable Long diaryId, @RequestBody DiaryDto diaryDto) {
        return diaryManagementService.updateDiary(diaryId, diaryDto.getTitle(), diaryDto.getContent(), diaryDto.getTag(), diaryDto.isShared());
    }

    @GetMapping
    public List<DiaryDto> getDiariesByUser(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return diaryManagementService.getDiariesByUser(user);
    }

    @GetMapping("/{diaryId}")
    public DiaryDto getDiaryById(@PathVariable Long diaryId) {
        return diaryManagementService.getDiaryById(diaryId);
    }

    @GetMapping("/shared")
    public List<DiaryDto> getSharedDiaries() {
        return diaryManagementService.getSharedDiaries();
    }
}
