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
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("refresh");

        if(accessToken == null || accessToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("access token: {}", accessToken);
        log.debug("refresh token: {}", refreshToken);

        validateTokens(response, accessToken, refreshToken);

        setAuthentication(accessToken);

        filterChain.doFilter(request, response);
    }

    private void validateTokens(HttpServletResponse response, String accessToken, String refreshToken) {
        if(!jwtProvider.validateToken(accessToken)) {
            if(jwtProvider.validateToken(refreshToken)) {
                String newAccessToken = jwtProvider.reissueAccessToken(refreshToken);
                response.setHeader("Authorization", "Bearer " + newAccessToken);
            }

            log.debug("Refresh token is expired");
        }

    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
