package com.example.HowToProj.repository;

import com.example.HowToProj.entity.Habit;
import com.example.HowToProj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUser(User user);
}
