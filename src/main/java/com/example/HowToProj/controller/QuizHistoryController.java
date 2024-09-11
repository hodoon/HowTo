package com.example.HowToProj.controller;

import com.example.HowToProj.dto.QuizHistoryDto;
import com.example.HowToProj.dto.QuizStatisticsDto;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.service.QuizHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/quiz-history")
public class QuizHistoryController {

    @Autowired
    private QuizHistoryService quizHistoryService;

    @PostMapping
    public void recordQuizParticipation(@PathVariable Long userId, @RequestBody QuizHistoryDto quizHistoryDto) {
        User user = new User();
        user.setId(userId);
        quizHistoryService.recordQuizParticipation(user, quizHistoryDto.getQuizId(), quizHistoryDto.getQuestion(), quizHistoryDto.getAnswerGiven(), quizHistoryDto.isCorrect());
    }

    @GetMapping("/quiz")
    public List<QuizHistoryDto> getQuizHistoriesByUser(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return quizHistoryService.getQuizHistoriesByUser(user);
    }

    @GetMapping("/correct-quiz")
    public List<QuizHistoryDto> getCorrectQuizHistoriesByUser(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return quizHistoryService.getCorrectQuizHistoriesByUser(user);
    }

    @GetMapping("/stats")
    public QuizStatisticsDto getQuizStats(@PathVariable Long userId) {
        return quizHistoryService.getQuizStats(userId);
    }
}
