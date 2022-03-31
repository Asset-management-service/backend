package com.backend.moamoa.domain.user.repository;

import com.backend.moamoa.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
