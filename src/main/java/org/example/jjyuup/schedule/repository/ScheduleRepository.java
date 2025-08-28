package org.example.jjyuup.schedule.repository;

import org.example.jjyuup.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}