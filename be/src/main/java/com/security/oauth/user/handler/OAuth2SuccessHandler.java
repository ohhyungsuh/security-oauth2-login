package com.security.oauth.user.handler;

import com.security.oauth.user.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String access = jwtProvider.generateAccessToken(authentication);
        String refresh = jwtProvider.generateRefreshToken(authentication);

        log.info("생성된 access token: {}", access);
        log.info("생성된 refresh token: {}", refresh);

//        // todo: 리프레시 토큰은 따로 관리 필요
//        response.setHeader("access", access);
//        response.setHeader("refresh", refresh);

        response.addCookie(createCookie("access", access));
        response.addCookie(createCookie("refresh", refresh));

        String redirectUrl = "http://localhost:5173/oauth/callback";
        response.sendRedirect(redirectUrl);
    }

    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

//        cookie.setSecure(true);
//        cookie.setAttribute("SameSite", "Strict"); // CSRF 방지
        return cookie;
    }
}
