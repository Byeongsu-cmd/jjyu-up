package org.example.jjyuup.schedule.repository;

import org.example.jjyuup.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    void deleteByUserIdAndId(Long userId, Long id); // 유저 아이디와 일정의 아이디를 검증

    List<Schedule> findAllByUserId(Long userId);
}