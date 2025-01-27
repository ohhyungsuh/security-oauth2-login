package com.security.oauth.user.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Date;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}") String secret;
    @Value("${jwt.expiration.access}")
    private Long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${jwt.expiration.refresh}")
    private Long REFRESH_TOKEN_EXPIRE_TIME;
    private SecretKey secretKey;

    // Spring이 해당 클래스의 빈을 초기화한 뒤, 딱 한 번 호출되는 메서드
    // todo : @PostConstruct 없이 그냥 바로 객체 생성될 때 실행하도록 하면?
    @PostConstruct
    protected void initSecretKey() {
        this.secretKey = new SecretKeySpec(secret.getBytes(UTF_8),
                Jwts.SIG.HS512.key().build().getAlgorithm());
    }

    // 생성
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME, "access");
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME, "refresh");
    }

    private String generateToken(Authentication authentication, Long expirationMs, String category) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        log.debug("Generating access token for user: {}", authentication.getName());

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("category", category)
                .claim("authorities", authorities)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    // 검증 및 refresh token 발급은 나중에


    private Claims parseClaims(String refreshToken) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(refreshToken)
                .getPayload();
    }
}
