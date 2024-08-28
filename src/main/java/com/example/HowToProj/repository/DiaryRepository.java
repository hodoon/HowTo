package com.example.HowToProj.repository;

import com.example.HowToProj.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // 사용자 ID를 기준으로 일기를 조회하고, 생성 날짜 기준으로 내림차순 정렬
    List<Diary> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 공개된 일기를 조회 (isShared가 true인 경우)
    List<Diary> findByIsSharedTrue();
}
