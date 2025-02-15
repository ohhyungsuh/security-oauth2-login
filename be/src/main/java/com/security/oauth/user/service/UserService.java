package com.security.oauth.user.service;

import com.security.oauth.user.domain.Provider;
import com.security.oauth.user.domain.User;
import com.security.oauth.user.dto.JoinDto;
import com.security.oauth.user.dto.OAuth2JoinDto;
import com.security.oauth.user.dto.UserDto;
import com.security.oauth.user.dto.UserInfoDto;
import com.security.oauth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void joinUser(JoinDto joinDto) {

        Optional<User> user = userRepository.findByEmail(joinDto.getEmail());

        if(user.isPresent()) {
            if(user.get().getProvider().equals(Provider.LOCAL)) {
                log.info("이미 가입된 이메일입니다.");
            } else {
                log.info("소셜 로그인으로 가입된 이메일입니다.");
            }
            return;
        }

        User newUser = User.joinLocalUser(joinDto.getName(), joinDto.getEmail(),
                bCryptPasswordEncoder.encode(joinDto.getPassword()), joinDto.getBirth());

        userRepository.save(newUser);
    }

    @Transactional
    public UserDto joinOAuth2User(OAuth2JoinDto joinDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        user.completeSignup(joinDto.getBirth());

        return modelMapper.map(user, UserDto.class);
    }

    public UserInfoDto getMyInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        return modelMapper.map(user, UserInfoDto.class);
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
    }

}
