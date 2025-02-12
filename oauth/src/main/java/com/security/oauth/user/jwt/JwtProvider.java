package com.security.oauth.user.jwt;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    String secret;
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

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("category", category)
                .claim("authorities", authorities)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    /*
     * @param jwt the compact serialized JWT to parse
     * @return the parsed JWT instance
     * @throws MalformedJwtException    if the specified JWT was incorrectly constructed (and therefore invalid).
     *                                  Invalid JWTs should not be trusted and should be discarded.
     * @throws SignatureException       if a JWS signature was discovered, but could not be verified.  JWTs that fail
     *                                  signature validation should not be trusted and should be discarded.
     * @throws SecurityException        if the specified JWT string is a JWE and decryption fails
     * @throws ExpiredJwtException      if the specified JWT is a Claims JWT and the Claims has an expiration time
     *                                  before the time this method is invoked.
     * @throws IllegalArgumentException if the specified string is {@code null} or empty or only whitespace.
     */

    // todo: 검증 예외처리 필요
    public boolean validateToken(String token) {
        try {
            Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    /*
     * Jwts.parser() -> JwtParserBuilder 객체 반환을 통해 파싱과 검증 옵션 설정 가능
     * verifyWith(secretKey) -> 검증을 위한 키 설정
     * build() -> Jwt 파싱 작업할 수 있는 JwtParser 객체 반환(헤더, 페이로드, 서명 검증 및 파싱)
     * parseSignedClaims(token) -> Jws<Claims> 객체. Jws는 서명 완료된 JWT를 나타냄
     * getPayload() -> JWT 페이로드 반환
     *
     * public interface Jws<T> {
     *     Header getHeader();    // JWT 헤더
     *     T getPayload();        // JWT 페이로드
     *     String getSignature(); // JWT 서명
     * }
     */
    private Claims parseClaims(String refreshToken) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token");
        }

    }

    public String reissueAccessToken(String refreshToken) {
        Authentication authentication = getAuthentication(refreshToken);
        return generateAccessToken(authentication);
    }

    /*
     * The principal and credentials should be set with an Object that provides the respective property
     *
     * This constructor should only be used by AuthenticationManager or AuthenticationProvider implementations
       that are satisfied with producing a trusted (i. e. isAuthenticated() = true) authentication token.
     * Params: principal credentials authorities
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get("authorities").toString());

        User userPrincipal = new User(claims.getSubject(), "", authorities);
        log.debug("User: {}", userPrincipal);
        // credentials?
        return new UsernamePasswordAuthenticationToken(userPrincipal, token, authorities);
    }
}
