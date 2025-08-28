package org.example.jjyuup.schedule.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<ScheduleResponseDto> save(
            @Valid @RequestBody ScheduleRequestDto scheduleRequestDto
    ) {
        // 201 상태코드
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.save(scheduleRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> findAll() {
        // 200 상태코드
        return ResponseEntity.ok(scheduleService.findAll());
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> findById(
            @PathVariable Long scheduleId
    ) {
        // 200 상태코드
        return ResponseEntity.ok(scheduleService.findById(scheduleId));
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> update(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleRequestDto scheduleRequestDto
    ){
        // 200 상태코드
        return ResponseEntity.ok(scheduleService.update(scheduleId, scheduleRequestDto));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteById(
            @PathVariable Long scheduleId
    ){
        scheduleService.deleteById(scheduleId);
        // 204 상태코드
        return ResponseEntity.noContent().build();
    }
}
