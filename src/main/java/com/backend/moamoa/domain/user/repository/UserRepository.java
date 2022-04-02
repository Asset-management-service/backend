package com.backend.moamoa.domain.user.repository;

import com.backend.moamoa.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId);

    Optional<User> findById(Long userId);

}
