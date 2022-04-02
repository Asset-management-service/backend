package com.backend.moamoa.global.utils;

import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtil {

    private final UserRepository userRepository;

    public User findCurrentUser() {
        return userRepository.findByUserId(SecurityUtil.getCurrentMemberId());
    }
}
