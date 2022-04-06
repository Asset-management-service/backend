package com.backend.moamoa.domain.user.repository;

import com.backend.moamoa.domain.user.entity.UserMailAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MailAuthRepository extends JpaRepository<UserMailAuth, Long> {
    // 해당 이메일과 키, 만료 기간 이내에 토큰 가져오기
    Optional<UserMailAuth> findByMailAndAuthKeyAndExpirationDateAfterAndExpiredIsFalse(String mail, String authkey, LocalDateTime now);

}
