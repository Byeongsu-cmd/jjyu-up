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
     *  insert into users
     *  (created_at, email, modified_at, name, password)
     *  values
     *  (?, ?, ?, ?, ?)
     */
    // 유저 등록
    public UserResponseDto create(UserRequestDto userRequestDto) {
        User user = new User(
                userRequestDto.getName(),
                userRequestDto.getEmail(),
                userRequestDto.getPassword()
        );
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
    // 유저 전체 조회
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();
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
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저입니다.")
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
     * update users
     * set
     *      email=?,
     *      modified_at=?,
     *      name=?,
     *      password=?
     * where id=?
     */
    // 유저 정보 수정
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저입니다.")
        );

        user.updateUser(
                userRequestDto.getName(),
                userRequestDto.getEmail(),
                userRequestDto.getPassword()
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
     *
     * delete
     * from users
     * where id=?
     */
    // 유저 정보 삭제
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저입니다.")
        );
        userRepository.delete(user);
    }
}
