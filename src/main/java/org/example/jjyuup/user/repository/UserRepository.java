package org.example.jjyuup.user.repository;

import org.example.jjyuup.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * select *
     * from users
     * where deleted = false;
     */
    List<User> findByDeletedFalse();

    /**
     * select case when count(*) > 0 THEN TRUE ELSE FALSE END
     * from users
     * where email = ?;
     */
    boolean existsByEmail(String email); // 입력한 이메일이 DB에 존재하는 지 여부 파악

    /**
     * select *
     * from users
     * where email = ?;
     */
    User findByEmail(String email);
}
