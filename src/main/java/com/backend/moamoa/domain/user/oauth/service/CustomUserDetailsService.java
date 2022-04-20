package com.backend.moamoa.domain.user.oauth.service;

import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.oauth.entity.CustomUserDetails;
import com.backend.moamoa.domain.user.repository.UserRepository;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("customUserDetailsService");
        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can not find username."));

        return CustomUserDetails.create(user);
    }

}
