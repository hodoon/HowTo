package com.example.HowToProj.repository;

import com.example.HowToProj.entity.QuizHistory;
import com.example.HowToProj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizHistoryRepository extends JpaRepository<QuizHistory, Long> {
    List<QuizHistory> findByUser(User user);
    List<QuizHistory> findByUserAndIsCorrect(User user, boolean isCorrect);
}
