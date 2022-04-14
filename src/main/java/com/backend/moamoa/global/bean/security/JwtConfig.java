package com.backend.moamoa.global.bean.security;

import com.backend.moamoa.domain.user.oauth.token.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public JwtProvider provider() {
        return new JwtProvider(secret);
    }
}
