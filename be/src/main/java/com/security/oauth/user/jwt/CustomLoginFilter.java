package com.security.oauth.user.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.oauth.user.domain.User;
import com.security.oauth.user.dto.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtProvider jwtProvider;

    public CustomLoginFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String accessToken = jwtProvider.generateAccessToken(authResult);
        String refreshToken = jwtProvider.generateRefreshToken(authResult);
        response.setHeader("access", accessToken);
        response.setHeader("refresh", refreshToken);

        log.info("생성된 access token: {}", accessToken);
        log.info("생성된 refresh token: {}", refreshToken);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
