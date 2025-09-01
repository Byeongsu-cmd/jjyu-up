package org.example.jjyuup.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jjyuup.common.consts.Const;
import org.example.jjyuup.user.dto.UserLoginRequestDto;
import org.example.jjyuup.user.dto.UserRequestDto;
import org.example.jjyuup.user.dto.UserResponseDto;
import org.example.jjyuup.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userRequestDto));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(
            @Valid @RequestBody UserLoginRequestDto userLoginRequestDto,
            HttpServletRequest request
    ){
        UserResponseDto userResponse = userService.login(userLoginRequestDto);
        HttpSession session = request.getSession();
        session.setAttribute(Const.SESSION_KEY, userResponse.getId());
        return ResponseEntity.ok(userResponse);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request){

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().build();
    }

    // 삭제된 유저 정보 복원
    @PostMapping("/restoreUsers")
    public ResponseEntity<Void> restore(
            @Valid @RequestBody UserLoginRequestDto userLoginRequestDto
    ) {
        userService.restore(userLoginRequestDto);
        return ResponseEntity.ok().build();
    }
}
