package org.example.jjyuup.schedule.service;

import lombok.RequiredArgsConstructor;
import org.example.jjyuup.schedule.dto.ScheduleRequestDto;
import org.example.jjyuup.schedule.dto.ScheduleResponseDto;
import org.example.jjyuup.schedule.entity.Schedule;
import org.example.jjyuup.schedule.repository.ScheduleRepository;
import org.example.jjyuup.user.entity.User;
import org.example.jjyuup.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저는 존재하지 않습니다!") // 없다면 예외 처리 404
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
     * 모든 일정 조회(삭제 되지않은 일정)
     * Hibernate:
     *     select
     *         s1_0.id,
     *         s1_0.content,
     *         s1_0.created_at,
     *         s1_0.deleted,
     *         s1_0.modified_at,
     *         s1_0.title,
     *         s1_0.user_id
     *     from
     *         schedules s1_0
     *     where
     *         not(s1_0.deleted)
     *
     *  사용자의 아이디로 조회
     *  Hibernate:
     *     select
     *         u1_0.id,
     *         u1_0.created_at,
     *         u1_0.deleted,
     *         u1_0.email,
     *         u1_0.modified_at,
     *         u1_0.name,
     *         u1_0.password
     *     from
     *         users u1_0
     *     where
     *         u1_0.id=?
     */
    // 일정 전체 조회  - 자신정보 모두 표시 남의 일정은 null로 설정, 삭제된 유저의 일정은 조회되지 않는다.
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findAll(Long userId) {

        List<Schedule> schedules = scheduleRepository.findByDeletedFalse();
//        List<Schedule> schedules = scheduleRepository.findAllByUserId(user.getId());
        List<ScheduleResponseDto> scheduleResponse = new ArrayList<>();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일정을 찾을 수 없습니다.")
        );
        for (Schedule schedule : schedules) {
            // softDelete가 true라면 일정을 조회할 수 없다. 삭제된 유저의 일정은 조회할 수 없다.
            if (schedule.getUser().isDeleted()) { // 일정의 유저의 삭제여부를 확인해야 한다..
                continue;
            } // 유저가 삭제되지 않았다면..
            if (Objects.equals(user.getId(), schedule.getUser().getId())) {
                scheduleResponse.add(new ScheduleResponseDto(
                        schedule.getId(),
                        schedule.getUser().getId(),
                        schedule.getTitle(),
                        schedule.getContent(),
                        schedule.getCreatedAt(),
                        schedule.getModifiedAt()
                ));
            } else {
                scheduleResponse.add(new ScheduleResponseDto(
                        schedule.getId(),
                        schedule.getTitle(),
                        schedule.getContent(),
                        schedule.getCreatedAt()
                ));
            }
        }
        return scheduleResponse;
    }

    /**
     * 일정 검증
     * select *
     * from schedule
     * where id=?
     *
     * 유저 검증
     * select *
     * from users
     * where id=?
     */
// 일정 단건 조회
// 단건 조회에 본인 글이면 댓글까지 보이게 타인의 글이면 댓글이 안보이게 - 여기서 삭제된 유저의 일정이라면 출력 하지 않는다..
    @Transactional(readOnly = true)
    public ScheduleResponseDto findById(Long userId, Long id) {
        // 입력한 게시글의 아이디가 존재하지 않을 때
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일정을 찾을 수 없습니다.")
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일정을 찾을 수 없습니다.")
        );
        // softDelete가 true라면 일정을 조회할 수 없다. 삭제된 유저의 일정은 조회할 수 없다.
        if (schedule.getUser().isDeleted() || schedule.isDeleted()) { // 일정의 유저의 삭제여부를 확인해야 한다..
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 일정을 조회할 수 없습니다.");
        }

        if (Objects.equals(user.getId(), schedule.getUser().getId())) {
            return new ScheduleResponseDto(
                    schedule.getId(),
                    schedule.getUser().getId(),
                    schedule.getTitle(),
                    schedule.getContent(),
                    schedule.getCreatedAt(),
                    schedule.getModifiedAt()
            );
        } else {
            return new ScheduleResponseDto(
                    schedule.getId(),
                    schedule.getTitle(),
                    schedule.getContent(),
                    schedule.getCreatedAt()
            );
        }
    }

    /**
     * select *
     * from schedules
     * where id=?;
     * <p>
     * update
     *      schedules
     * set
     *      content=?, // 업데이트 할 내용을 작성
     *      deleted=?,
     *      modified_at=?,
     *      title=?
     *      user_id=?
     * where
     *      id=? // 일정 아이디
     */

    /*
     더티체킹이란 영속성 컨텍스트가 관리하는 엔티티의 변경 사항을 감지하고, 트랜잭션이 끝날 때 자동으로 DB에 반영하는 것이다.
     DB에 update sql을 날려 실제 테이블의 데이터까지 갱신한다는 것이 핵심!!
    */
// 일정 수정 - 본인 일정만 수정 가능
    public ScheduleResponseDto update(Long userId, Long id, ScheduleRequestDto scheduleRequestDto) {
        // 입력한 게시글의 아이디가 존재하지 않을 때
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "찾을 수 없는 게시글입니다.")
        );

        if (!Objects.equals(userId, schedule.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 일정의 작성자가 아님으로 수정할 수 없습니다!");
        }

        schedule.updateSchedule(
                scheduleRequestDto.getTitle(),
                scheduleRequestDto.getContent()
        );

        scheduleRepository.save(schedule); // save의 merge 기능 활용

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
     *  일정은 일정아이디(PK) 값으로 조회
     *  Hibernate:
     *     select
     *         s1_0.id,
     *         s1_0.content,
     *         s1_0.created_at,
     *         s1_0.deleted,
     *         s1_0.modified_at,
     *         s1_0.title,
     *         s1_0.user_id
     *     from
     *         schedules s1_0
     *     where
     *         s1_0.id=?
     *
     *  삭제를 softDelete로 사용하니 수정 (true로 변환하여 DB에 저장)
     *  Hibernate:
     *     update
     *         schedules
     *     set
     *         content=?,
     *         deleted=?,
     *         modified_at=?,
     *         title=?,
     *         user_id=?
     *     where
     *         id=?
     */
    // 일정 삭제 - 삭제가 이미 되어있는 상태라면 예외 처리
    public void deleteSchedule(Long userId, Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "찾을 수 없는 게시글입니다.")
        );
        if (!Objects.equals(userId, schedule.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 일정의 작성자가 아님으로 수정할 수 없습니다!");
        }
        if (schedule.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "찾을 수 없는 게시글입니다.");
        }
        //        scheduleRepository.deleteByUserIdAndId(userId, id);
        schedule.softDelete(); // boolean 값을 true로 변환
        scheduleRepository.save(schedule); // save의 merge 기능 활용
    }

    /**
     *  일정은 일정아이디(PK) 값으로 조회
     *  Hibernate:
     *     select
     *         s1_0.id,
     *         s1_0.content,
     *         s1_0.created_at,
     *         s1_0.deleted,
     *         s1_0.modified_at,
     *         s1_0.title,
     *         s1_0.user_id
     *     from
     *         schedules s1_0
     *     where
     *         s1_0.id=?
     *
     *  삭제를 softDelete로 사용하니 수정 (false로 변환하여 DB에 저장)
     *  Hibernate:
     *     update
     *         schedules
     *     set
     *         content=?,
     *         deleted=?,
     *         modified_at=?,
     *         title=?,
     *         user_id=?
     *     where
     *         id=?
     */
    // 일정 복원
    public void restoreSchedule(Long userId, Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow( // pk 값을 복구키 활용
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "찾을 수 없는 게시글입니다.")
        );
        // 일정의 유니크 값을 만들지 않아서 pk로 대체 하였고, 로그인 유저가 일정을 작성했던 유저라면 일정을 복원할 수 있다.
        // 로그인을 결국 해야하기에 회원 탈퇴나 로그인 상태가 아니라면 예외처리가 된다.
        if (!schedule.isDeleted() && Objects.equals(userId, schedule.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제된 일정이 아닙니다.");
        }
        schedule.restore();
        scheduleRepository.save(schedule);
    }
}
