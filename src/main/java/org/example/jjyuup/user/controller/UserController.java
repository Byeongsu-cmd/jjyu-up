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
@RequestMapping("/users") // 이 클래스의 모든 엔드 포인트의 기본 경로를 /users로 묶었다.
public class UserController {
    private final UserService userService;

    /**
     * @SessionAttribute 이미 존재하느 세션의 속성을 매서드 파라미터로 바로 주입한다. 세션 값이 없다면 예외 처리
     * HttpServletRequest request : 세션을 직접 제어하거나, 세션 존재 여부를 확인하고 싶을 때 사용
     * @RequestParam 기본 값은 true로 필수 값을 받게 설정되지만 (required = false) 를 추가적으로 작성한다면
     *               필요할 때는 적고 아니라면 적지 않아도 되는 즉, 필수가 아닌 값을 받도록 설정이 된다.
     * @Valid Dto의 Bean Validation의 제약이 자동으로 검증되게 하는 어노테이션이다.
     */

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
        // 회원 정보 삭제 (softDelete) 실제로는 삭제하지 않지만 DB에 deleted 값이 true로 변환된다.
        userService.delete(id, deleteUserRequest);
        // 로그아웃
        HttpSession session = request.getSession(false); // 세션이 없다면 null로 반환
        if (session != null) {
            session.invalidate(); // 세션이 존재하면 invalidate로 세션 종료
        }
        return ResponseEntity.noContent().build(); // 삭제 후 바디가 없다.. 204

        /**
         * HttpServletRequest request 이것만 사용하는 경우
         *
         * HttpSession session1 = request.getSession(false);
         * if(session == null){
         *  예외처리
         * }
         * Long id =(Long)session1.getAttribute(Const.SESSION_KEY);
         * if(id == null){
         *  예외처리
         *  }
         *  userService.delete(id,deleteUserRequest);
         *  session.invalidate();
         *  return ResponseEntity.noContent().build();
         */

    }
}
