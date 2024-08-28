package com.example.HowToProj.dto;

import com.example.HowToProj.entity.HabitRecord;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitRecordDto {
    private Long id;
    private Long habitId;
    private LocalDate date;
    private boolean achieved;

    public static HabitRecordDto fromEntity(HabitRecord habitRecord) {
        if (habitRecord == null) return null;

        return HabitRecordDto.builder()
                .id(habitRecord.getId())
                .habitId(habitRecord.getHabit().getId())
                .date(habitRecord.getDate())
                .achieved(habitRecord.isAchieved())
                .build();
    }
}
