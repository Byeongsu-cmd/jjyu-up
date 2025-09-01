package org.example.jjyuup.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jjyuup.common.consts.Const;
import org.example.jjyuup.user.dto.DeleteUserRequest;
import org.example.jjyuup.user.dto.UserRequestDto;
import org.example.jjyuup.user.dto.UserResponseDto;
import org.example.jjyuup.user.service.UserService;
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

    // 유저 정보 수정 - 로그인한 유저만 자신의 정보 수정 가능..
    @PutMapping
    public ResponseEntity<UserResponseDto> update(
            @SessionAttribute(name = Const.SESSION_KEY) Long id, // 로그인 세션 키
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        return ResponseEntity.ok(userService.update(id, userRequestDto));
    }

    // 유저 정보 삭제
    @PostMapping
    public ResponseEntity<Void> delete(
            @SessionAttribute(name = Const.SESSION_KEY) Long id, // 컨트롤러 파라미터에서 바로 세션 값을 꺼낼 수 있어서 간결하고 직관적
            @Valid @RequestBody DeleteUserRequest deleteUserRequest,
            HttpServletRequest request // 세션을 직접 제어하거나, 세션 존재 여부를 확인하고 싶을 때 사용
    ) {
        // 회원 정보 삭제 (softDelete)
        userService.delete(id, deleteUserRequest);
        // 로그아웃
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }
}
