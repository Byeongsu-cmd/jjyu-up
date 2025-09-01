package org.example.jjyuup.schedule.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jjyuup.common.consts.Const;
import org.example.jjyuup.schedule.dto.ScheduleRequestDto;
import org.example.jjyuup.schedule.dto.ScheduleResponseDto;
import org.example.jjyuup.schedule.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor // final 필드의 생성자 생성하는 기능 밖에 없다.
@RequestMapping("/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    // @Component를 통해 등록된 빈은 다른 곳에서 @Autowired를 사용해 의존성을 주입할 수 있다.
    //하나만 있을 경우는 오토와이어드 생략해도 빈에등록가능 왜냐 창구가 하나기때문에
//    @Autowired
//    public ScheduleController(ScheduleService scheduleService) {
//        this.scheduleService = scheduleService;
//    }

//    public ScheduleController(ScheduleService scheduleService) {
//        this.scheduleService = scheduleService;
//    }

//    public ScheduleController(){
//    }

    // 일정 등록
    @PostMapping
    public ResponseEntity<ScheduleResponseDto> save(
            @SessionAttribute(name = Const.SESSION_KEY) Long userId, // 로그인 세션 키
            @Valid @RequestBody ScheduleRequestDto scheduleRequestDto
    ) {
        // 201 상태코드
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.save(userId, scheduleRequestDto));
    }

    // 일정 전체 조회
    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> findAll(
            @SessionAttribute(name = Const.SESSION_KEY) Long userId // 로그인 세션 키
    ) {
        // 200 상태코드
        return ResponseEntity.ok(scheduleService.findAll(userId));
    }

    // 일정 단건 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> findById(
            @SessionAttribute(name = Const.SESSION_KEY) Long userId, // 로그인 세션 키
            @PathVariable Long scheduleId
    ) {
        // 200 상태코드
        return ResponseEntity.ok(scheduleService.findById(userId, scheduleId));
    }

    // 일정 수정
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> update(
            @SessionAttribute(name = Const.SESSION_KEY) Long userId, // 로그인 세션 키
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleRequestDto scheduleRequestDto
    ) {
        // 200 상태코드
        return ResponseEntity.ok(scheduleService.update(userId, scheduleId, scheduleRequestDto));
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @SessionAttribute(name = Const.SESSION_KEY) Long userId, // 로그인 세션 키
            @PathVariable Long scheduleId
    ) {
        scheduleService.deleteSchedule(userId, scheduleId);
        // 204 상태코드
        return ResponseEntity.noContent().build();
    }

    // 일정 복원
    @PostMapping("/{scheduleId}")
    public ResponseEntity<Void> restoreSchedule(
            @SessionAttribute(name = Const.SESSION_KEY) Long userId, // 로그인 세션 키
            @PathVariable Long scheduleId
    ){
        scheduleService.restoreSchedule(userId, scheduleId);
        return ResponseEntity.noContent().build();
    }
}
