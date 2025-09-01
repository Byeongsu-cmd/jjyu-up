package org.example.jjyuup.schedule.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleResponseDto {
    private final Long id;
    private final Long userId;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    // 본인의 일정 데이터를 반환할 때
    public ScheduleResponseDto(Long id, Long userId, String title, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    // 타인의 일정 데이터를 반환할 때
    public ScheduleResponseDto(Long id, String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.userId = null;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = null;
    }
}
