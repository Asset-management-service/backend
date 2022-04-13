package com.backend.moamoa.domain.user.oauth.provider;

import com.backend.moamoa.domain.user.enums.RoleType;
import com.backend.moamoa.domain.user.oauth.dto.response.TokenResponse;
import com.backend.moamoa.domain.user.oauth.exception.TokenValidFailedException;
import com.backend.moamoa.global.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
public class AuthTokenProvider {

    private final Key key;
    private static final String AUTHORITIES_KEY = "role";

    public AuthTokenProvider(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public AuthToken createAuthToken(String id, Date expiry) {
        return new AuthToken(id, expiry, key);
    }

    public AuthToken createAuthToken(String id, String role, Date expiry) {
        return new AuthToken(id, role, expiry, key);
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(token, key);
    }

    public Authentication getAuthentication(AuthToken authToken) {

        if (authToken.validate()) {

            Claims claims = authToken.getTokenClaims();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            log.debug("claims subject := [{}]", claims.getSubject());
            User principal = new User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
        } else {
            throw new TokenValidFailedException();
        }
    }

    public TokenResponse createTokenResponse(String userId, AppProperties appProperties) {

        Date now = new Date();
        RoleType roleType = RoleType.USER;

        AuthToken newAccessToken = createAuthToken(
                userId,
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        AuthToken authRefreshToken = createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + appProperties.getAuth().getRefreshTokenExpiry())
        );

        return TokenResponse.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken.getToken())
                .refreshToken(authRefreshToken.getToken())
                .accessTokenExpireDate(appProperties.getAuth().getTokenExpiry())
                .build();
    }

}

