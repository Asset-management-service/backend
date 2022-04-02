package com.backend.moamoa.global.utils;

import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.repository.UserRepository;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtil {

    private final UserRepository userRepository;

    public User findCurrentUser() {

        User user = userRepository.findByUserId(SecurityUtil.getCurrentMemberId());

        if (user == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }
        return user;
    }

}
