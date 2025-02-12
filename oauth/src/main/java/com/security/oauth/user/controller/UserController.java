package com.security.oauth.user.controller;

import com.security.oauth.user.dto.JoinDto;
import com.security.oauth.user.dto.OAuth2JoinDto;
import com.security.oauth.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/my")
    public String my() {
        return "로그인 필요한 내 화면";
    }

    // 일반 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> joinUser(@RequestBody JoinDto joinDto) {
        userService.joinUser(joinDto);
        return ResponseEntity.ok("join success");
    }

    // 소셜 회원가입
    @PostMapping("/oauth2-join")
    public ResponseEntity<?> joinOAuth2User(@RequestBody OAuth2JoinDto joinDto) {
        // todo: 소셜 회원가입 추가 정보 입력 기능 구현
        userService.joinOAuth2User(joinDto);
        return ResponseEntity.ok("oauth2 join success");
    }

}
