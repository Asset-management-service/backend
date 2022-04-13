package com.backend.moamoa.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate redisTemplate;

    public void setValues(String token, String email) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(token, email, Duration.ofMinutes(3));
    }

    public String getValues(String token) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(token);
    }

    public void delValues(String token) {
        redisTemplate.delete(token.substring(7));
    }
}
