package com.security.oauth.user.controller;

import com.security.oauth.user.domain.User;
import com.security.oauth.user.dto.JoinDto;
import com.security.oauth.user.dto.OAuth2JoinDto;
import com.security.oauth.user.dto.UserDto;
import com.security.oauth.user.dto.UserInfoDto;
import com.security.oauth.user.service.UserService;
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
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    // 일반 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> joinUser(@RequestBody JoinDto joinDto) {
        userService.joinUser(joinDto);
        return ResponseEntity.ok("join success");
    }

    @GetMapping("/my-info")
    public ResponseEntity<UserInfoDto> getMyInfo(Principal principal) {
        UserInfoDto userInfoDto = userService.getMyInfo(principal.getName());
        return ResponseEntity.ok(userInfoDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(Principal principal) {
        User user = userService.getUser(principal.getName());
        return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
    }

}
