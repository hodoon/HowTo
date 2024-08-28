package com.example.HowToProj.repository;

import com.example.HowToProj.entity.PointsHistory;
import com.example.HowToProj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointsHistoryRepository extends JpaRepository<PointsHistory, Long> {
    List<PointsHistory> findByUser(User user);
}
