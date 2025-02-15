package com.security.oauth.user.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtFilter 실행됨: {}", request.getRequestURI());

        // 특정 URI 요청은 필터링 제외
        if (request.getRequestURI().equals("/endpoint")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = request.getHeader("access");
        String refreshToken = request.getHeader("refresh");

        log.info("access token: {}", accessToken);
        log.info("refresh token: {}", refreshToken);

        if(accessToken == null || accessToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        validateTokens(response, accessToken, refreshToken);

        setAuthentication(accessToken);

        filterChain.doFilter(request, response);
    }

    private void validateTokens(HttpServletResponse response, String accessToken, String refreshToken) {
        if(!jwtProvider.validateToken(accessToken)) {
            if(jwtProvider.validateToken(refreshToken)) {
                String newAccessToken = jwtProvider.reissueAccessToken(refreshToken);
                response.setHeader("access", newAccessToken);
            }

            log.debug("Refresh token is expired");
        }

    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        log.info("최종 SecurityContext Authentication: {}", authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
