package com.security.oauth.user.service;

import com.security.oauth.user.domain.User;
import com.security.oauth.user.dto.OAuth2UserDto;
import com.security.oauth.user.dto.PrincipalUserDetails;
import com.security.oauth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    // 리소스 서버에서 제공되는 정보를 받아오는 작업이다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        /*
         * Get the OAuth 2.0 token attributes
         * the OAuth 2.0 token attributes
         * scope로 받아온 것들
         */

        // super.loadUser(userRequest)은 OAuth2User 객체이며, 출력 값은 속성, 권한 등이다. 그걸 Map에 넣어주자.
        // 참고로 naver는 "response" 키를 가진 Value 안에 값들이 있다.
        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();

        log.info("OAuth2User loadUser attributes: {}", attributes);

        /*
         * Returns the identifier for the registration.
         * Returns: the identifier for the registration
         * "google", "naver" 이런 식
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2User loadUser registrationId: {}", registrationId);

        // 서비스에 따라 유저 정보를 빼오는 방법이 다르기 때문에 내부적으로 분기를 나눠 처리하자! 전처리 작업
        OAuth2UserDto oAuth2UserDto = OAuth2UserDto.of(attributes, registrationId);

        /*
         * 서비스에서 받아온 정보가 중복될 가능성이 있으면 UUID 등을 통해 사용자를 식별할 수 있는 고유값을 만들어야 하지만,
         * 여기서는 email이 unique하기 때문에 괜찮다.
         */

        Optional<User> findUser = userRepository.findByEmail(oAuth2UserDto.getEmail());

        if(findUser.isEmpty()) {
            userRepository.save(oAuth2UserDto.toUser());
            return new PrincipalUserDetails(oAuth2UserDto.toUser(), attributes);
        }

        return new PrincipalUserDetails(findUser.get(), attributes);
    }

//    private User getOrSave(OAuth2UserDto oAuth2UserDto) {
//        User user = userRepository.findByEmail(oAuth2UserDto.getEmail())
//                .orElseGet(oAuth2UserDto::toUser);
//
//        return userRepository.save(user);
//    }
}
