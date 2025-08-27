package org.example.jjyuup.user.repository;

import org.example.jjyuup.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
