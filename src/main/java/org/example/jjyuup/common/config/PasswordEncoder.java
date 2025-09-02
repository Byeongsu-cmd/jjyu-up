package org.example.jjyuup.common.config;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component // spring이 자동으로 스캔하여 스프링 빈으로 등록하여 다른 클래스에서 @Autowired 또는 생성자 주입으로 이 passwordEncoder를 사용할 수 있다.
public class PasswordEncoder {

    /**
     * 평문 비밀번호(rawPassword)를 해싱해서 반환합니다.
     * BCrypt는 단방향 해시 함수다. 즉, 해시 값으로 원본 비밀번호를 복원하는 것은 불가능하다.
     * withDefaults() : BCrypt 해시 생성기를 가져온다.
     * hashToString() : 비밀번호를 해싱해 문자열로 반환한다.
     * BCrypt.MIN_COST : 해싱 난이도의 최소값 다만 보안적으로 약할 수 있다..
     * rawPassword
     */
    public String encode(String rawPassword) {
        return BCrypt.withDefaults().hashToString(BCrypt.MIN_COST, rawPassword.toCharArray());
    }

    /**
     * 사용자가 입력한 평문 비밀번호와 DB에 저장된 해시를 비교한다.
     * BCrypt.verifyer().verify() :
     * 1. 저장된 해시 문자열에서 cost값,salt,해시 알고리즘 정보를 읽어온다. - salt(무작위 값)
     * 2. 동일한 반식으로 rawPassword를 해싱
     * 3. 두 값을 비교
     * 결론 result.verified 가 true면 비밀번호가 일치한다.
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword);
        return result.verified;
    }
}