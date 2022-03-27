package com.backend.moamoa.global.bean;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
@Configuration
public class QueryDslConfig {

    private final EntityManager em;

    @Bean
    public JPAQueryFactory QueryDslConfig(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}
