package com.backend.moamoa.domain.user.service;

import com.backend.moamoa.domain.user.dto.request.LogoutRequest;
import com.backend.moamoa.domain.user.dto.request.TokenRequest;
import com.backend.moamoa.domain.user.dto.response.TokenResponse;
import com.backend.moamoa.domain.user.oauth.token.JwtProvider;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
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

    private final JwtProvider jwtProvider;
    private final RedisTemplate redisTemplate;
    private final long refreshTokenExpiry = 604800000;

    @Transactional
    public TokenResponse reissue(TokenRequest tokenRequest) {
        String accessToken = tokenRequest.getAccessToken();

        if (!jwtProvider.getExpiredTokenClaims(accessToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String userId = jwtProvider.getUserInfo(accessToken).getSubject();
        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + userId);

        if (ObjectUtils.isEmpty(refreshToken) || !refreshToken.equals(tokenRequest.getRefreshToken())) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        TokenResponse tokenResponse = jwtProvider.createTokenResponse(userId);
        redisTemplate.opsForValue().set("RT:" + userId, tokenResponse.getRefreshToken(), refreshTokenExpiry, TimeUnit.MILLISECONDS);

        return tokenResponse;
    }

    public Boolean logout(LogoutRequest logoutRequest) {
        String accessToken = logoutRequest.getAccessToken();

        if (!jwtProvider.getTokenClaims(accessToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String userId = jwtProvider.getUserInfo(accessToken).getSubject();

        if (redisTemplate.opsForValue().get("RT:" + userId) != null) {
            redisTemplate.delete("RT:" + userId);
        }

        Long expiration = jwtProvider.getExpiration(logoutRequest.getAccessToken());
        redisTemplate.opsForValue().set(logoutRequest.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

        return true;
    }

}
