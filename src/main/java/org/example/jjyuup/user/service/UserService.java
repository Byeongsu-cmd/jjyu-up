package org.example.jjyuup.user.service;

import lombok.RequiredArgsConstructor;
import org.example.jjyuup.common.config.PasswordEncoder;
import org.example.jjyuup.user.dto.DeleteUserRequest;
import org.example.jjyuup.user.dto.UserLoginRequestDto;
import org.example.jjyuup.user.dto.UserRequestDto;
import org.example.jjyuup.user.dto.UserResponseDto;
import org.example.jjyuup.user.entity.User;
import org.example.jjyuup.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional // 하나의 작업 단위
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
     * 이메일 중복 여부 확인
     * select
     * u1_0.id
     * from
     * users u1_0
     * where
     * u1_0.email=?
     * limit
     * ?
     * <p>
     * insert
     * into
     * users
     * (created_at, deleted, email, modified_at, name, password)
     * values
     * (?, ?, ?, ?, ?, ?)
     */
    // 회원 가입
    public UserResponseDto create(UserRequestDto userRequestDto) {
        // 이메일 중복 여부를 먼저 파악하고 생성해야하기 때문에 먼저 검증
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "유효하지 않은 이메일입니다."); // 409
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
     * 전체 조회
     * select
     * u1_0.id,
     * u1_0.created_at,
     * u1_0.deleted,
     * u1_0.email,
     * u1_0.modified_at,
     * u1_0.name,
     * u1_0.password
     * from
     * users u1_0
     * where
     * not(u1_0.deleted)
     * <p>
     * 유저명을 파람으로 입력 시
     * select
     * u1_0.id,
     * u1_0.created_at,
     * u1_0.deleted,
     * u1_0.email,
     * u1_0.modified_at,
     * u1_0.name,
     * u1_0.password
     * from
     * users u1_0
     * where
     * not(u1_0.deleted)
     * 둘다 같은 쿼리문이 나간다.
     */
    // 동적 쿼리로 하면 간단할 수 있다...  like 쿼리를 사용하는 것이 더 좋을 수 있다 "like %" ("" 이면 전체 조회)
    // dto를 명확하게 분리하는 것이 더 좋을 것 같다.
    // 전체 조회니 굳이 리스트에서 목차를 보내줄 때 나눌 필요가?... 단건 조회할 때도 자신의 정보만 조회할 수도 있게하면 좋지않을까?..
    // stream 공부를 좀 하는 것이 좋을 듯하다..
    // 먼저 실실적으로 UI 상으로 어떻게 보일지를 고려하여 개발하면 좋을 것 같다.. sns 를 직접 찾아보면서 직관적으로 생각을 해보자!
    // for 문을 10개일 때보다 2중 for문이 더 비용이 많이 든다.
    // 페이징은 거의 필수 사용자가 많다면 메모리가 터진다... 슬라이드로 내리는 것도 페이징 처리다!! 버튼으로 구현되어 있는 페이지만 페이징처리가 아니다.
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
     * 사용자 아이디로 전체 조회
     * select
     * u1_0.id,
     * u1_0.created_at,
     * u1_0.deleted,
     * u1_0.email,
     * u1_0.modified_at,
     * u1_0.name,
     * u1_0.password
     * from
     * users u1_0
     * where
     * u1_0.id=?
     * <p>
     * 이메일로 사용자 존재 여부 확인
     * Hibernate:
     * select
     * u1_0.id
     * from
     * users u1_0
     * where
     * u1_0.email=?
     * limit
     * ?
     * <p>
     * 사용자 정보 업데이트
     * Hibernate:
     * update
     * users
     * set
     * deleted=?,
     * email=?,
     * modified_at=?,
     * name=?,
     * password=?
     * where
     * id=?
     */
    // 유저 정보 수정 - 이메일과 유저명만 변경가능
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        // 저장된 데이터의 유저와 로그인한 유저가 같은 유저라면
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.CONFLICT, "로그인 정보가 유효하지 않습니다.")
        );

        // 이메일 중복 여부를 먼저 파악하고 생성해야하기 때문에 먼저 검증
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "유효하지 않은 이메일입니다."); // 409
        }

        // 입력한 비밀번호가 저장된 비밀번호와 같은 지 검증
        boolean passwordMatch = passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword());
        if (!passwordMatch) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }

        // 입력받은 비밀번호를 암호화
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());

        user.updateUser(
                userRequestDto.getName(),
                userRequestDto.getEmail(),
                encodedPassword
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
     * 사용자 조회
     * Hibernate:
     * select
     * u1_0.id,
     * u1_0.created_at,
     * u1_0.deleted,
     * u1_0.email,
     * u1_0.modified_at,
     * u1_0.name,
     * u1_0.password
     * from
     * users u1_0
     * where
     * u1_0.id=?
     * <p>
     * 사용자 정보 수정 (softDelete 라서 deleted가 true로 변환된다.)
     * Hibernate:
     * update
     * users
     * set
     * deleted=?,
     * email=?,
     * modified_at=?,
     * name=?,
     * password=?
     * where
     * id=?
     */
    // 유저 정보 삭제 - 비밀번호 검증를 입력 받아 한 번 더 검증! - 삭제하고 자동 로그아웃 실행하도록
    public void delete(Long id, DeleteUserRequest deleteUserRequest) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.CONFLICT, "로그인 정보가 유효하지 않습니다.")
        );
        if (!passwordEncoder.matches(deleteUserRequest.getPassword(), user.getPassword())) { // 입력한 비밀번호 검증
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다!");
        }
        user.softDelete(); // deleted = true
        userRepository.save(user); // DB에 boolean 값 적용
    }

    /**
     * 사용자 조회 (이메일로 조회)
     * Hibernate:
     * select
     * u1_0.id,
     * u1_0.created_at,
     * u1_0.deleted,
     * u1_0.email,
     * u1_0.modified_at,
     * u1_0.name,
     * u1_0.password
     * from
     * users u1_0
     * where
     * u1_0.email=?
     * <p>
     * 사용자 정보 수정(softDelete 라 false로 변환하여 다시 복원)
     * Hibernate:
     * update
     * users
     * set
     * deleted=?,
     * email=?,
     * modified_at=?,
     * name=?,
     * password=?
     * where
     * id=?
     */
    // 삭제된 유저 정보 복원 - 본인이라는 검증이 필요하다... 복원 키로 뭘 주는 것이 좋을까? 일단 수정하지 못하게 한 비밀번호로 검증하는 것이 좋을 것 같긴하다...
    public void restore(UserLoginRequestDto userLoginRequestDto) {
        User user = userRepository.findByEmail(userLoginRequestDto.getEmail());
        // 이메일은 유니크하니 이메일로 유저 분류
        if (!Objects.equals(user.getEmail(), userLoginRequestDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "입력한 정보가 일치하지 않습니다.");
        }
        // 비밀번호 검증
        if (!passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())) { // 입력한 비밀번호 검증
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다!");
        }
        // 삭제 컬럼의 boolean 값을 false 변환하여 유저 정보를 복원한다.
        if (user.isDeleted()) {
            user.restore(); // 엔티티에 있는 restore 메서드를 호출하여 boolean 값을 false로 변환
            //아래의 코드를 작성하지 않으면 더티체킹
            //userRepository.save(user); // 변경된 정보 저장 (save의 merge 기능을 활용하여 적용할 수 있다.)
            userRepository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제된 유저가 아닙니다.");
        }
    }

    /**
     * select
     * u1_0.id,
     * u1_0.created_at,
     * u1_0.deleted,
     * u1_0.email,
     * u1_0.modified_at,
     * u1_0.name,
     * u1_0.password
     * from
     * users u1_0
     * where
     * u1_0.email=?
     */
    // 커스텀 익셉션도 생각해보자! 예외에 대한 후 처리를 할려면 이것이 유용하다! (도메인 기준으로 나누는 것이 무난하게 좋다!)
    // 코드 상 예외가 터지면 어디서 예외가 터졌는지 구분하기가 힘들다..
    // 로그인
    public UserResponseDto login(UserLoginRequestDto userLoginRequestDto) {
        User user = userRepository.findByEmail(userLoginRequestDto.getEmail());
        if (user.isDeleted()) { // 삭제된 유저라면..
            throw new ResponseStatusException(HttpStatus.CONFLICT, "아이디 혹은 비밀번호가 일치하지 않습니다.");
        }
        if (!Objects.equals(user.getEmail(), userLoginRequestDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디 혹은 비밀번호가 일치하지 않습니다!");
        }
        if (!passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디 혹은 비밀번호가 일치하지 않습니다!");
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
