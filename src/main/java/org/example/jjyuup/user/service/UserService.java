package org.example.jjyuup.user.service;

import lombok.RequiredArgsConstructor;
import org.example.jjyuup.user.dto.UserRequestDto;
import org.example.jjyuup.user.dto.UserResponseDto;
import org.example.jjyuup.user.entity.User;
import org.example.jjyuup.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

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
    // 유저 등록
    public UserResponseDto create(UserRequestDto userRequestDto) {
        User user = new User(
                userRequestDto.getName(),
                userRequestDto.getEmail(),
                userRequestDto.getPassword()
        );
        if (Objects.equals(user.getEmail(), userRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일 입니다.");
        }
        userRepository.save(user);
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
    // 유저 전체 조회 (삭제되지 않은 유저)
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findByDeletedFalse();

        List<UserResponseDto> userResponses = new ArrayList<>();

        for (User user : users) {
            userResponses.add(new UserResponseDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getCreatedAt(),
                    user.getModifiedAt()
            ));
        }
        return userResponses;
    }

    /**
     * select *
     * from users
     * where id=?
     */
    // 유저 단건 조회
    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow( // 이전에 생성이 한 번이라도 되었던 아이디인지 검증
                () -> new IllegalArgumentException("존재하지 않는 유저입니다.")
        );
        if (user.isDeleted()) {
            // 위와 같은 메세지로 출력하는 이유는 보안상 이렇게 통일 시키는 것이 좋을 것 같아서.. 하지만 테스트를 해야하니 뒤에 1을 붙임.
            throw new IllegalStateException("존재하지 않는 유저입니다.1");
        }

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
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
    // 유저 정보 수정
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id).orElseThrow( // 이전에 생성이 한 번이라도 되었던 아이디인지 검증
                () -> new IllegalArgumentException("존재하지 않는 유저입니다.")
        );
        if (user.isDeleted()) {
            // 위와 같은 메세지로 출력하는 이유는 보안상 이렇게 통일 시키는 것이 좋을 것 같아서.. 하지만 테스트를 해야하니 뒤에 1을 붙임.
            throw new IllegalStateException("존재하지 않는 유저입니다.1");
        }

        user.updateUser(
                userRequestDto.getName(),
                userRequestDto.getEmail(),
                userRequestDto.getPassword()
        );
        if (Objects.equals(user.getEmail(), userRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일 입니다.");
        }

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
                () -> new IllegalArgumentException("존재하지 않는 유저입니다.")
        );
        if (user.isDeleted()) {
            // 위와 같은 메세지로 출력하는 이유는 보안상 이렇게 통일 시키는 것이 좋을 것 같아서.. 하지만 테스트를 해야하니 뒤에 1을 붙임.
            throw new IllegalStateException("존재하지 않는 유저입니다.1");
        }
        user.softDelete(); // deleted = true
        userRepository.save(user); // DB에 boolean 값 적용
    }

    // 삭제된 유저 정보 복원
    public void restore(Long id) {
        User user = userRepository.findById(id).orElseThrow( // 이전에 생성이 한 번이라도 되었던 아이디인지 검증
                () -> new IllegalArgumentException("존재하지 않았던 유저입니다.")
        );
        if (user.isDeleted()) {
            user.restore(); // 엔티티에 있는 restore 메서드를 호출하여 boolean 값을 false로 변환
            //아래의 코드를 작성하지 않으면 더티체킹
            //userRepository.save(user); // 변경된 정보 저장 (save의 merge 기능을 활용하여 적용할 수 있다.)
        } else {
            throw new IllegalStateException("삭제된 유저가 아닙니다!");
        }
    }
}
