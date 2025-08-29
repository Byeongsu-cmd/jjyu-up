package org.example.jjyuup.schedule.service;

import lombok.RequiredArgsConstructor;
import org.example.jjyuup.schedule.dto.ScheduleRequestDto;
import org.example.jjyuup.schedule.dto.ScheduleResponseDto;
import org.example.jjyuup.schedule.entity.Schedule;
import org.example.jjyuup.schedule.repository.ScheduleRepository;
import org.example.jjyuup.user.entity.User;
import org.example.jjyuup.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor  // final 필드의 생성자 생성
@Transactional
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    /**
     * - [ ]  일정을 생성, 조회, 수정, 삭제할 수 있습니다.
     * - [ ]  일정은 아래 필드를 가집니다.
     * - [ ]  `작성 유저명`, `할일 제목`, `할일 내용`, `작성일`, `수정일` 필드
     * - [ ]  `작성일`, `수정일` 필드는 `JPA Auditing`을 활용합니다.
     */

    /**
     * insert 문의 기본 역할은 데이터 삽입(추가,입력),
     * 추가 역할은 데이터 복사/통합
     * <p>
     * 유저의 아디를 먼저 찾고
     * select *
     * from users
     * where id =?
     * <p>
     * 일정을 등록
     * insert
     * into
     * schedules
     * (content, create_at, modified_at, title)
     * values
     * (?, ?, ?, ?)
     */
    // 일정 등록
    public ScheduleResponseDto save(Long userId, ScheduleRequestDto scheduleRequestDto) {
        User user = userRepository.findById(userId).orElseThrow( // 유저 객체를 생성 후 유저 repository에 있는 유저의 정보를 가져온다.
                () -> new IllegalArgumentException("해당 유저는 존재하지 않습니다!") // 없다면 예외 처리
        );

        Schedule schedule = new Schedule(
                scheduleRequestDto.getTitle(),
                scheduleRequestDto.getContent(),
                user
        );

        // insert SQL문을 만들어서 주입한다. 이거에 대해 한 번더 고민해보자! 2가지 역할을 가지고 있다.
        scheduleRepository.save(schedule);
        return new ScheduleResponseDto(
                schedule.getId(),
                user.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getCreatedAt(),
                schedule.getModifiedAt()
        );
    }

    /**
     * 유저 아이디 값을 받았을 경우
     * select *
     * from users
     * where id=?
     * <p>
     * select *
     * from schedules s left join users u on s.id = u.id
     * where u.id = ?;
     * <p>
     * 유저 아이디 값이 없을 경우
     * select *
     * from schedules
     */
    // 일정 조회
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findAll(Long userId) {

        List<Schedule> schedules;
        if (userId != null) { // 유저 아이디를 입력했다면..
            userRepository.findById(userId).orElseThrow( // 유저 객체를 생성 후 유저 repository에 있는 유저의 정보를 가져온다.
                    () -> new IllegalArgumentException("해당 유저는 존재하지 않습니다!") // 없다면 예외 처리
            );
            schedules = scheduleRepository.findAllByUserId(userId);
        } else {
            schedules = scheduleRepository.findAll();
        }

        List<ScheduleResponseDto> scheduleResponse = new ArrayList<>();
        for (Schedule schedule : schedules) {
            scheduleResponse.add(new ScheduleResponseDto(
                    schedule.getId(),
                    schedule.getUser().getId(),
                    schedule.getTitle(),
                    schedule.getContent(),
                    schedule.getCreatedAt(),
                    schedule.getModifiedAt()
            ));
        }
        return scheduleResponse;
    }

    /**
     * select *
     * from schedule
     * where id=?
     */
    // 일정 단건 조회
    // 단건 조회에 굳이? 유저 아이디가 필요할까? 일정 조회할 때는 검증을 제외하도록 하자
    @Transactional(readOnly = true)
    public ScheduleResponseDto findById(Long id) {
        // 입력한 게시글의 아이디가 존재하지 않을 때
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("찾을 수 없는 게시글입니다.")
        );

        return new ScheduleResponseDto(
                schedule.getId(),
                schedule.getUser().getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getCreatedAt(),
                schedule.getModifiedAt()
        );
    }

    /**
     * select *
     * from users
     * where id=?;
     * <p>
     * select *
     * from schedules
     * where id=?;
     * <p>
     * update schedules
     * set content=?, // 업데이트 할 내용을 작성
     * modified_at=?,
     * title=?
     * user_id=?
     * where id=? // 일정 아이디
     */

    /*
     더티체킹이란 영속성 컨텍스트가 관리하는 엔티티의 변경 사항을 감지하고, 트랜잭션이 끝날 때 자동으로 DB에 반영하는 것이다.
     DB에 update sql을 날려 실제 테이블의 데이터까지 갱신한다는 것이 핵심!!
    */
    // 일정 수정
    public ScheduleResponseDto update(Long userId, Long id, ScheduleRequestDto scheduleRequestDto) {
        if (userId != null) { // 유저 아이디를 입력했다면..
            userRepository.findById(userId).orElseThrow( // 유저 객체를 생성 후 유저 repository에 있는 유저의 정보를 가져온다.
                    () -> new IllegalArgumentException("해당 유저는 존재하지 않습니다!") // 없다면 예외 처리
            );
        }
        // 입력한 게시글의 아이디가 존재하지 않을 때
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("찾을 수 없는 게시글입니다.")
        );

        if (!Objects.equals(userId, schedule.getUser().getId())) {
            throw new IllegalArgumentException("해당 일정의 작성자가 아님으로 수정할 수 없습니다!");
        }

        schedule.updateSchedule(
                scheduleRequestDto.getTitle(),
                scheduleRequestDto.getContent()
        );
        return new ScheduleResponseDto(
                schedule.getId(),
                schedule.getUser().getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getCreatedAt(),
                schedule.getModifiedAt()
        );
    }

    /**
     * select *
     * from users
     * where id=?;
     * <p>
     * select *
     * from schedules
     * where id=?;
     * <p>
     * select *
     * from schedules s left join users u on s.id = u.id
     * where u.id = ? and s.id
     * <p>
     * delete
     * from schedule
     * where id=?;
     */
    // 일정 삭제
    public void deleteSchedule(Long userId, Long id) {
        userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저입니다!")
        );
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 일정입니다!")
        );
        if (!Objects.equals(userId, schedule.getUser().getId())) {
            throw new IllegalArgumentException("작성한 유저가 아니라 삭제가 불가능합니다!");
        }
        scheduleRepository.deleteByUserIdAndId(userId, id);
    }
}
