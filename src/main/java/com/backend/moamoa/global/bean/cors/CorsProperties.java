package com.backend.moamoa.global.bean.cors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    private String allowedOrigins;

    private String allowedMethods;

    private String allowedHeaders;

    private Long maxAge;

}
