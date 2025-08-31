package org.example.jjyuup.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jjyuup.common.consts.Const;
import org.example.jjyuup.user.dto.UserRequestDto;
import org.example.jjyuup.user.dto.UserResponseDto;
import org.example.jjyuup.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // 유저 전체 조회
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll(
            @SessionAttribute(name = Const.SESSION_KEY) Long id, // 로그인 세션 키
            @RequestParam(required = false) String name // 파람 값으로 유저 정보 조회
    ) {
        return ResponseEntity.ok(userService.findUsers(id, name));
    }

    // 유저 정보 수정
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        return ResponseEntity.ok(userService.update(userId, userRequestDto));
    }

    // 유저 정보 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long userId
    ) {
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    // 삭제된 유저 정보 복원
    @PostMapping("/{userId}/restoreUsers")
    public ResponseEntity<Void> restore(
            @PathVariable Long userId
    ){
        userService.restore(userId);
        return ResponseEntity.ok().build();
    }
}
