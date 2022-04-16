package com.backend.moamoa.domain.user.oauth.token;

import com.backend.moamoa.domain.user.entity.enums.RoleType;
import com.backend.moamoa.domain.user.dto.response.TokenResponse;
import com.backend.moamoa.domain.user.oauth.service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;

    private final CustomUserDetailsService userDetailsService;

    private final Long tokenExpiry = 30 * 60 * 1000L; // 30 minutes
    private final Long refreshTokenExpiry = 7 * 24 * 60 * 60 * 1000L; // 14 day

    private static final String AUTHORITIES_KEY = "role";
    private static final String GRANT_TYPE = "Bearer";

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Claims parseClaims(String token) {
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
                .refreshTokenExpireDate(refreshTokenExpiry)
                .build();
    }

    public boolean validationToken(String authToken) {
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

    public boolean ExpiredToken(String authToken) {
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
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

}

