package org.example.jjyuup.schedule.repository;

import org.example.jjyuup.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByUserId(Long userId); // 유저 아이디로 생성한 일정이 있을 수도 있고 없을 수도 있다.

    Optional<Schedule> findAllByUserIdAndDeletedFalse(Long userId);

    List<Schedule> findByDeletedFalse();
}