package com.backend.moamoa.global.config;

import com.backend.moamoa.domain.user.oauth.properties.CorsProperties;
import com.backend.moamoa.global.config.AppProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        CorsProperties.class,
        AppProperties.class
})
public class AppConfig {
}
