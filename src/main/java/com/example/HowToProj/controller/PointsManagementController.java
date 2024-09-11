package com.example.HowToProj.controller;

import com.example.HowToProj.dto.PointsDto;
import com.example.HowToProj.dto.PointsHistoryDto;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/points")
public class PointsManagementController {

    @Autowired
    private PointsService pointsService;

    @GetMapping
    public PointsDto getPointsByUser(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return pointsService.getPointsByUser(user);
    }

    @PostMapping("/use")
    public PointsDto usePoints(@PathVariable Long userId, @RequestParam int amount, @RequestParam String description) {
        // User 객체를 생성하고 userId를 설정합니다.
        User user = new User();
        user.setId(userId);

        // 포인트를 사용하고 결과를 반환합니다.
        pointsService.usePoints(user, amount, description);

        // 포인트를 반환할 때는 포인트 DTO를 반환하는 것이 일반적입니다.
        return pointsService.getPointsByUser(user);
    }

    @GetMapping("/history")
    public List<PointsHistoryDto> getPointsHistoryByUser(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return pointsService.getPointsHistoryByUser(user);
    }
}
