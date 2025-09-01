package org.example.jjyuup.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteUserRequest {
    @NotBlank(message = "비밀번호를 입력해 주세요!")
    private String password;
}
