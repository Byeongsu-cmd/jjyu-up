package org.example.jjyuup.user.service;

import lombok.RequiredArgsConstructor;
import org.example.jjyuup.common.config.PasswordEncoder;
import org.example.jjyuup.user.dto.UserLoginRequestDto;
import org.example.jjyuup.user.dto.UserRequestDto;
import org.example.jjyuup.user.dto.UserResponseDto;
import org.example.jjyuup.user.entity.User;
import org.example.jjyuup.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * - [ ]  사용자는 생성(회원가입), 조회, 수정, 삭제(탈퇴)할 수 있습니다. ✓
     * - [ ]  유저는 아래와 같은 필드를 가집니다. ✓
     * - [ ]  `유저명`, `이메일`, `작성일` , `수정일` 필드 ✓
     * - [ ]  `작성일`, `수정일` 필드는 `JPA Auditing`을 활용합니다. ✓
     * - [ ]  연관관계 구현 ✓
     * - [ ]  일정은 이제 `작성 유저명` 필드 대신 `유저 고유 식별자` 필드를 가집니다.
     */

    /**
     * insert into users
     * (created_at, email, modified_at, name, password)
     * values
     * (?, ?, ?, ?, ?)
     */
    // 회원 가입
    public UserResponseDto create(UserRequestDto userRequestDto) {

        // 이메일 중복 여부를 먼저 파악하고 생성해야하기 때문에 먼저 검증
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new ResponseStatusException(CONFLICT, "유효하지 않은 이메일입니다."); // 409
        }

        // 입력받은 비밀번호를 암호화
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());

        // 입력받은 값을 기반으로 new 하여 User 객체 생성
        User user = new User(
                userRequestDto.getName(),
                userRequestDto.getEmail(),
                encodedPassword // 암호화된 비밀번호 저장
        );

        // user를 save 하여 DB에 저장
        userRepository.save(user);

        // UserResponseDto가 반환 값이니 new 해서 객체를 생성하여 반환
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }

    /**
     * select *
     * from users
     */
    // 유저 전체 조회 (삭제되지 않은 유저) or 유저명으로 조회
    @Transactional(readOnly = true)
    public List<UserResponseDto> findUsers(Long id, String name) {
        List<User> users = userRepository.findByDeletedFalse(); // 회원 상태를 유지하고 있는 유저 리스트

        List<UserResponseDto> userResponses = new ArrayList<>(); // 반환 값에 맞춘 새로운 리스트

        if (name == null) {
            for (User user : users) {
                if (Objects.equals(user.getId(), id)) {
                    userResponses.add(new UserResponseDto( // 로그인한 유저가 저장된 유저의 데이터와 같다면(본인 데이터)
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getCreatedAt(),
                            user.getModifiedAt()
                    ));
                } else {
                    userResponses.add(new UserResponseDto( // 로그인한 유저와 저장된 유저의 데이터가 다르다면(타인의 데이터)
                            user.getName(),
                            user.getCreatedAt()
                    ));
                }
            }
        }

        // 이름을 입력했다면...name != null;
        for (User user : users) {
            if (Objects.equals(user.getName(), name) && Objects.equals(user.getId(), id)) {
                userResponses.add(new UserResponseDto( // 로그인한 유저가 저장된 유저의 데이터와 같다면(본인 데이터)
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getCreatedAt(),
                        user.getModifiedAt()
                ));
            } else if (Objects.equals(user.getName(), name)) {
                userResponses.add(new UserResponseDto( // 로그인한 유저와 저장된 유저의 데이터가 다르다면(타인의 데이터)
                        user.getName(),
                        user.getCreatedAt()
                ));
            }
        }
        return userResponses;
    }

    /**
     * update users
     * set
     * email=?,
     * modified_at=?,
     * name=?,
     * password=?
     * where id=?
     */
    // 유저 정보 수정 - 이메일과 유저명만 변경가능
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id).orElseThrow( // 이전에 생성이 한 번이라도 되었던 아이디인지 검증
                () -> new ResponseStatusException(NOT_FOUND, "존재하지 않는 유저입니다.") // 404
        );

        if (user.isDeleted()) {
            // 위와 같은 메세지로 출력하는 이유는 보안상 이렇게 통일 시키는 것이 좋을 것 같아서.. 하지만 테스트를 해야하니 뒤에 1을 붙임.
            throw new ResponseStatusException(NOT_FOUND, "존재하지 않는 유저입니다.1"); // 404
        }

        // 이메일 중복 여부를 먼저 파악하고 생성해야하기 때문에 먼저 검증
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new ResponseStatusException(CONFLICT, "유효하지 않은 이메일입니다."); // 409
        }

        // 입력한 비밀번호가 저장된 비밀번호와 같은 지 검증
        boolean passwordMatch = passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword());
        if (!passwordMatch) {
            throw new ResponseStatusException(BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }

        // 입력받은 비밀번호를 암호화
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());

        user.updateUser(
                userRequestDto.getName(),
                userRequestDto.getEmail(),
                encodedPassword
        );

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }

    /**
     * select *
     * from users
     * where id=?
     * <p>
     * delete
     * from users
     * where id=?
     */
    // 유저 정보 삭제
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow( // 이전에 생성이 한 번이라도 되었던 아이디인지 검증
                () -> new ResponseStatusException(NOT_FOUND, "존재하지 않는 유저입니다.") // 404
        );
        if (user.isDeleted()) {
            // 위와 같은 메세지로 출력하는 이유는 보안상 이렇게 통일 시키는 것이 좋을 것 같아서.. 하지만 테스트를 해야하니 뒤에 1을 붙임.
            throw new ResponseStatusException(NOT_FOUND, "존재하지 않는 유저입니다.1"); // 404
        }
        user.softDelete(); // deleted = true
        userRepository.save(user); // DB에 boolean 값 적용
    }

    // 삭제된 유저 정보 복원
    public void restore(Long id) {
        User user = userRepository.findById(id).orElseThrow( // 이전에 생성이 한 번이라도 되었던 아이디인지 검증
                () -> new ResponseStatusException(NOT_FOUND, "존재하지 않는 유저입니다.") // 404
        );
        if (user.isDeleted()) {
            user.restore(); // 엔티티에 있는 restore 메서드를 호출하여 boolean 값을 false로 변환
            //아래의 코드를 작성하지 않으면 더티체킹
            //userRepository.save(user); // 변경된 정보 저장 (save의 merge 기능을 활용하여 적용할 수 있다.)
        } else {
            throw new ResponseStatusException(BAD_REQUEST, "삭제된 유저가 아닙니다.");
        }
    }

    // 로그인
    public UserResponseDto login(UserLoginRequestDto userLoginRequestDto) {
        User user = userRepository.findByEmail(userLoginRequestDto.getEmail());
        if (!Objects.equals(user.getEmail(), userLoginRequestDto.getEmail())) {
            throw new ResponseStatusException(BAD_REQUEST, "아이디 혹은 비밀번호가 일치하지 않습니다!");
        }
        if (!passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(BAD_REQUEST, "아이디 혹은 비밀번호가 일치하지 않습니다!");
        }

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }
    // 로그아웃은 세션을 종료 시키면 되니 굳이 서비스를 만들 필요가 없다..
}
