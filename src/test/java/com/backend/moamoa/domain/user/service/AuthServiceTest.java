package com.backend.moamoa.domain.user.service;

import com.backend.moamoa.domain.user.dto.response.TokenResponse;
import com.backend.moamoa.domain.user.oauth.filter.JwtFilter;
import com.backend.moamoa.domain.user.oauth.token.JwtProvider;
import com.backend.moamoa.global.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private JwtFilter jwtFilter;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private ValueOperations valueOperations;

    private final String REFRESH_TOKEN = "eyKjsaGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.KiNUK70RDCTWeRMqfN6YY_SAkkb8opFsAh_fwAntt4";
    private final String NEW_ACCESS_TOKEN = "ksJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.KiNUK70RDCTWeRMqfN6YY_SAkkb8opFsAh_fwAntt4";
    private final String NEW_REFRESH_TOKEN = "ksJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.KiNUK70RDCTWeRMqfN6YY_SAkkb8opFsAh_fwAntt4";

    private final String GRANT_TYPE = "Bearer";
    private final Long ValidTokenExpiry = 0L;
    private final Long InvalidTokenExpiry = 30 * 60 * 1000L;

    private String secretKey = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTY1MTY3OTg2NywiaWF0IjoxNjUxNjc5ODY3fQ.gU21HEnUdJCN6EW3Lc5gfpu4nsqnDcs321shbtuyI8s";

    @Test
    @DisplayName("만료된_토큰_정보로_재발급_요청한_경우")
    void reissue() throws Exception {
        Date now = new Date();
        String validToken = Jwts.builder()
                .setSubject("123456L")
                .claim("role", "ROLE_USER")
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .setExpiration(new Date(now.getTime() + ValidTokenExpiry))
                .compact();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie[] testCookies = new Cookie[]{new Cookie("refresh_token", REFRESH_TOKEN)};
        when(request.getCookies()).thenReturn(testCookies);

        when(JwtFilter.getAccessToken(request)).thenReturn(validToken);
        when(jwtProvider.ExpiredToken(any())).thenReturn(true);

        given(jwtProvider.parseClaims(any())).will(
                invocation -> {
                    try {
                        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())).build().parseClaimsJws(validToken).getBody();
                    } catch (ExpiredJwtException e) {
                        return e.getClaims();
                    }
                }
        );

        given(jwtProvider.createTokenResponse("123456L")).will(
                invocation -> TokenResponse.builder()
                        .grantType(GRANT_TYPE)
                        .accessToken(NEW_ACCESS_TOKEN)
                        .refreshToken(NEW_REFRESH_TOKEN)
                        .accessTokenExpireDate(30 * 60 * 1000L)
                        .refreshTokenExpireDate(7 * 30 * 60 * 1000L)
                        .build()
        );

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForValue().get("RT:" + "123456L")).willReturn(REFRESH_TOKEN);

        TokenResponse reissue = authService.reissue(request, response);

        assertThat(reissue.getGrantType(), is(equalTo(GRANT_TYPE)));
        assertThat(reissue.getAccessToken(), is(equalTo(NEW_ACCESS_TOKEN)));
        assertThat(reissue.getRefreshToken(), is(equalTo(NEW_REFRESH_TOKEN)));
        assertThat(reissue.getAccessTokenExpireDate(), is(equalTo(30 * 60 * 1000L)));
        assertThat(reissue.getRefreshTokenExpireDate(), is(equalTo(7 * 30 * 60 * 1000L)));
    }

    @Test
    @DisplayName("만료되지_않은_토큰_정보로_재발급_요청한_경우")
    void invalid_reissue() {
        Date now = new Date();
        String InvalidToken = Jwts.builder()
                .setSubject("123456L")
                .claim("role", "ROLE_USER")
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .setExpiration(new Date(now.getTime() + InvalidTokenExpiry))
                .compact();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(JwtFilter.getAccessToken(request)).thenReturn(InvalidToken);
        when(jwtProvider.ExpiredToken(any())).thenReturn(false);

        assertThatThrownBy(() -> authService.reissue(request, response))
                .isInstanceOf(CustomException.class);
    }

}
