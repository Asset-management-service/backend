package com.backend.moamoa.domain.user.oauth.token;

import com.backend.moamoa.domain.user.entity.enums.RoleType;
import com.backend.moamoa.domain.user.dto.response.TokenResponse;
import io.jsonwebtoken.*;
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
public class JwtProvider {

    private final Key key;
    private final Long tokenExpiry = 30 * 60 * 1000L;
    private final Long refreshTokenExpiry = 7 * 24 * 60 * 60 * 1000L;

    private static final String AUTHORITIES_KEY = "role";
    private static final String GRANT_TYPE = "Bearer";

    public JwtProvider(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Authentication getAuthentication(String token) {
        Claims claims = this.getUserInfo(token);
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public Claims getUserInfo(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public TokenResponse createTokenResponse(String userId) {
        Date now = new Date();
        RoleType roleType = RoleType.USER;

        String newAccessToken = Jwts.builder()
                .setSubject(userId)
                .claim(AUTHORITIES_KEY, roleType.getCode())
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(new Date(now.getTime() + tokenExpiry))
                .compact();

        String authRefreshToken = Jwts.builder()
                .setSubject(userId)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(new Date(now.getTime() + refreshTokenExpiry))
                .compact();

        return TokenResponse.builder()
                .grantType(GRANT_TYPE)
                .accessToken(newAccessToken)
                .refreshToken(authRefreshToken)
                .accessTokenExpireDate(tokenExpiry)
                .build();
    }

    public boolean getTokenClaims(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();
            return true;
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return false;
    }

    public boolean getExpiredTokenClaims(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            return true;
        } catch (Exception e) {
            log.info("Invalid JWT token.");
        }
        return false;
    }

    public Long getExpiration(String authToken) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

}

