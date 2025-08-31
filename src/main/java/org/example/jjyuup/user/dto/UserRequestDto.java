package org.example.jjyuup.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserRequestDto {

    @NotBlank(message = "이름을 입력해 주세요!")
    private String name;

    @NotBlank(message = "이메일을 입력해 주세요!")
    private String email;

    @NotBlank(message = "비밀번호를 입력해 주세요!")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상을 입력해 주세요!")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[+*~_.-])[A-Za-z0-9+*~_.-]+$",
            message = "대소문자 영문, 숫자, 특수문자(+*~_.-)를 최소 1글자 이상 포함해야 합니다.")
    private String password;
}
