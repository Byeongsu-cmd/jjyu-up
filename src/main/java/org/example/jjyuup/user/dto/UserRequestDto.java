package org.example.jjyuup.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserRequestDto {

    /**
     * 회원 가입이나 회원 정보를 수정할 때는 이메일과 비밀번호의 조건을 맞춰야 하기 때문에 정규식으로 조건을 걸어주고,
     * null, 공백이 아니도록 @NotBlank 사용
     */
    @NotBlank(message = "이름을 입력해 주세요!")
    private String name;

    /**
     * 정규표현식으로 이메일 형식 검증용
     * 의미:
     * ^                 : 문자열 시작
     * [a-zA-Z0-9._%+-]+ : @ 앞부분, 알파벳 대소문자, 숫자, 점(.), 언더바(_), %, +, - 중 1개 이상
     * @                 : 반드시 @ 기호 포함
     * [a-zA-Z0-9.-]+    : 도메인 이름, 알파벳 대소문자, 숫자, 점(.), 하이픈(-) 중 1개 이상
     * \\.               : 실제 점(.) 문자. 정규식에서 특수문자 점(.)은 \\로 이스케이프
     * [a-zA-Z]{2,}      : 최상위 도메인(TLD), 최소 2글자 이상, 알파벳만 허용
     * $                 : 문자열 끝
     */

    @NotBlank(message = "이메일을 입력해 주세요!")
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "이메일 형식에 맞춰주세요!") // 이메일 어노테이션만 붙인다면
    private String email;

    /**
     * 정규표현식으로 비밀번호 형식 검증
     * 의미:
     * (?=.*[a-z])              : 최소 하나 이상의 소문자
     * (?=.*[A-Z])              : 최소 하나 이상의 대문자
     * (?=.*\\d)                : 최소 하나 이상의 숫자
     * (?=.*[@$!%*?&])          : 최소 하나 이상의 특수문자(@ $ ! % * ? &)
     * [A-Za-z\\d@$!%*?&]{8,16} : 전체 길이 8~16자, 허용되는 문자 집합
     */
    @NotBlank(message = "비밀번호를 입력해 주세요!")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상을 입력해 주세요!")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[+*~_.-])[A-Za-z0-9+*~_.-]+$",
            message = "대소문자 영문, 숫자, 특수문자(+*~_.-)를 최소 1글자 이상 포함해야 합니다.")
    private String password;
}
