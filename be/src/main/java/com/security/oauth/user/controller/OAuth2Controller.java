package com.security.oauth.user.controller;

import com.security.oauth.user.domain.User;
import com.security.oauth.user.dto.OAuth2JoinDto;
import com.security.oauth.user.dto.UserDto;
import com.security.oauth.user.jwt.JwtProvider;
import com.security.oauth.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuth2Controller {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JwtProvider jwtProvider;

    // 쿠키를 헤더로 변경하는게 이렇게 더럽고 복잡한건가 잘 모르겠네
    // 깔끔한 코드 보고 싶다
    @GetMapping("/endpoint")
    public ResponseEntity<?> getUser(@CookieValue(value = "access", required = false) String accessCookie,
                                     @CookieValue(value = "refresh", required = false) String refreshCookie,
                                     HttpServletResponse response) {
        if (accessCookie == null || refreshCookie == null) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }

        String email = jwtProvider.getUsername(accessCookie);

        User user = userService.getUser(email);

        log.info("access token: {}", accessCookie);
        log.info("refresh token: {}", refreshCookie);

        deleteCookie(response);

        if(user.isNew()) {
            return ResponseEntity.status(CREATED).header("access", accessCookie).
                    header("refresh", refreshCookie)
                    .body("새로운 소셜 계정 생성");
        }

        return ResponseEntity.ok().header("access", accessCookie).
                header("refresh", refreshCookie)
                .body(modelMapper.map(user, UserDto.class));
    }

    // 소셜 회원가입
    @PutMapping("/api/v1/users/oauth/join")
    public ResponseEntity<UserDto> joinOAuth2User(@RequestBody OAuth2JoinDto joinDto, Principal principal) {
        UserDto userDto = userService.joinOAuth2User(joinDto, principal.getName());
        return ResponseEntity.ok(userDto);
    }

    private void deleteCookie(HttpServletResponse response) {
        Cookie accessCookie = getCookie("access");
        Cookie refreshCookie = getCookie("refresh");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    private Cookie getCookie(String name) {
        Cookie cookie = new Cookie(name, null);

        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        return cookie;
    }

}
