package com.security.oauth.user.dto;

import com.security.oauth.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class PrincipalUserDetails implements OAuth2User, UserDetails {

    private final User user;
    private final Map<String, Object> attributes;

    /*
     * 사용자 권한 반환하는건데, User 엔티티에서 따로 정해줘서 그거 반환
     * SimpleGrantedAuthority는 GrantedAuthority 인터페이스의 구현체
     */
    // OAuth2User, UserDetails 모두 구현 필요한 메소드임
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 만약 role이 여러개면 바꿔주기
        return List.of(new SimpleGrantedAuthority(user.getRole().getValue()));
    }

    /************************************************************/

    // OAuth2User 메소드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // AuthenticationPrincipal 상속, OAuth2 서비스의 주된 식별자 반환
    @Override
    public String getName() {
        return user.getEmail();
    }

    /************************************************************/

    // UserDetails 메소드들 - 기본 인증, 인가에 필요
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 이건 안 하는 게 좋음
    @Override
    public String getPassword() {
        return "";
    }

    /*
     * 기본 인증, 인가에 필요한 주된 식별자 반환(로그인 아이디)
     * 필요하면 로그인 아이디 추가
     */
    @Override
    public String getUsername() {
        return null;
    }
}
