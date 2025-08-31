package org.example.jjyuup.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginRequestDto {

    /**
     * 이미 생성할 때 정규식으로 조건을 검증했기 때문에 여기에 추가로 정규식을 넣지 않아도 될 것으로 판단.
     * null, 공백이 아니도록 @NotBlank 사용
     */
    @NotBlank(message = "이메일을 입력해 주세요!")
    private String email;

    @NotBlank(message = "비밀번호를 입력해 주세요!")
    private String password;
}
