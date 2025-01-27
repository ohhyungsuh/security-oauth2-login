package com.security.oauth.user.dto;

import com.security.oauth.user.domain.User;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class OAuth2UserDto {

    private String name;
    private String email;

    /*
     * 구글, 깃헙, 네이버 등 서비스에 따라 제공되는 데이터가 모두 다르다.
     * 따라서 각 서비스에 따라 데이터를 파싱하는 메서드를 만들어야 한다.
     */
    public static OAuth2UserDto of(Map<String, Object> attributes, String registrationId) {
        if (registrationId.equalsIgnoreCase("google")) {
            return ofGoogle(attributes);
        }
        // 필요한 서비스 별로 구현

        // null인 경우 처리
        return null;
    }

    private static OAuth2UserDto ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserDto.builder()
                .name(attributes.get("name").toString())
                .email(attributes.get("email").toString())
                .build();
    }

    public User toUser() {
        return User.builder()
                .name(name)
                .email(email)
                .build();
    }
}
