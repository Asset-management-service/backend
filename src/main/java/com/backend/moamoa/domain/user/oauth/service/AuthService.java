package com.backend.moamoa.domain.user.oauth.service;

import com.backend.moamoa.domain.user.oauth.dto.request.TokenRequest;
import com.backend.moamoa.domain.user.oauth.dto.response.TokenResponse;
import com.backend.moamoa.domain.user.oauth.provider.AuthToken;
import com.backend.moamoa.domain.user.oauth.provider.AuthTokenProvider;
import com.backend.moamoa.global.config.AppProperties;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final RedisTemplate redisTemplate;

    @Transactional
    public TokenResponse reissue(TokenRequest tokenRequest) {

        AuthToken authToken = tokenProvider.convertAuthToken(tokenRequest.getAccessToken());
        Claims claims = authToken.getExpiredTokenClaims();

        if (claims == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        String userId = claims.getSubject();

        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + userId);

        if (ObjectUtils.isEmpty(refreshToken) || !refreshToken.equals(tokenRequest.getRefreshToken())) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        TokenResponse tokenResponse = tokenProvider.createTokenResponse(userId, appProperties);
        redisTemplate.opsForValue()
                .set("RT:" + userId, tokenResponse.getRefreshToken(), appProperties.getAuth().getRefreshTokenExpiry(), TimeUnit.MILLISECONDS);

        return tokenResponse;
    }

}
