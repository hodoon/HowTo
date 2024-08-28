package com.example.HowToProj.service;

import com.example.HowToProj.dto.DiaryDto;
import com.example.HowToProj.dto.SharedContentDto;
import com.example.HowToProj.entity.Diary;
import com.example.HowToProj.entity.SharedContent;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.exception.NotFoundDiaryException;
import com.example.HowToProj.repository.DiaryRepository;
import com.example.HowToProj.repository.SharedContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DiaryManagementService {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private SharedContentRepository sharedContentRepository;

    @Transactional
    public DiaryDto createDiary(User user, String title, String content, String tag, boolean isShared) {
        Diary diary = Diary.builder()
                .user(user)
                .title(title)
                .content(content)
                .tag(tag)
                .isShared(isShared)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Diary savedDiary = diaryRepository.save(diary);
        return DiaryDto.fromEntity(savedDiary);
    }

    @Transactional
    public DiaryDto updateDiary(Long diaryId, String title, String content, String tag, boolean isShared) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundDiaryException("Diary not found with ID: " + diaryId));

        diary.setTitle(title);
        diary.setContent(content);
        diary.setTag(tag);
        diary.setShared(isShared);
        diary.setUpdatedAt(LocalDateTime.now());

        Diary updatedDiary = diaryRepository.save(diary);
        return DiaryDto.fromEntity(updatedDiary);
    }

    public List<DiaryDto> getDiariesByUser(User user) {
        List<Diary> diaries = diaryRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return diaries.stream().map(DiaryDto::fromEntity).collect(Collectors.toList());
    }

    public List<DiaryDto> getSharedDiaries() {
        List<Diary> sharedDiaries = diaryRepository.findByIsSharedTrue();
        return sharedDiaries.stream().map(DiaryDto::fromEntity).collect(Collectors.toList());
    }

    public DiaryDto getDiaryById(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundDiaryException("Diary not found with ID: " + diaryId));
        return DiaryDto.fromEntity(diary);
    }

    @Transactional
    public void shareDiary(Long diaryId, User sharedWith) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundDiaryException("Diary not found with ID: " + diaryId));

        if (diary.isShared()) {
            SharedContent sharedContent = SharedContent.builder()
                    .user(diary.getUser())
                    .diary(diary)
                    .contentType("DIARY")
                    .contentIdInType(diary.getId())
                    .sharedWith(sharedWith)
                    .sharedAt(LocalDateTime.now())
                    .build();

            sharedContentRepository.save(sharedContent);
        } else {
            throw new RuntimeException("Diary is not shared. Cannot share it with others.");
        }
    }
}
