package org.example.jjyuup.user.repository;

import org.example.jjyuup.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDeletedFalse();
}
