package com.example.HowToProj.repository;

import com.example.HowToProj.entity.Points;
import com.example.HowToProj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointsRepository extends JpaRepository<Points, Long> {
    Optional<Points> findByUser(User user);
}
