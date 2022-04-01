package com.backend.moamoa.global.config.security;

import com.backend.moamoa.domain.user.oauth.properties.AppProperties;
import com.backend.moamoa.domain.user.oauth.properties.CorsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        CorsProperties.class,
        AppProperties.class
})
public class AppConfig {
}
