package com.security.oauth.user.domain;

import com.security.oauth.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String password;

    private LocalDate birth;

    @Enumerated(value = EnumType.STRING)
    private Provider provider;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private boolean isDeleted;

    // 일반 회원가입 Builder
    @Builder(builderMethodName = "localUserBuilder")
    private User(String name, String email, String password, LocalDate birth) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birth = birth;

        this.provider = Provider.LOCAL;
        this.role = Role.USER;
        this.isDeleted = false;
    }

    // 소셜 회원가입 Builder
    @Builder(builderMethodName = "oAuth2UserBuilder")
    private User(String name, String email, Provider provider) {
        this.name = name;
        this.email = email;
        this.provider = provider;

        this.password = "";
        this.role = Role.USER;
        this.isDeleted = false;
    }

    public static User joinLocalUser(String name, String email, String password, LocalDate birth) {
        return User.localUserBuilder()
                .name(name)
                .email(email)
                .password(password)
                .birth(birth)
                .build();
    }

    public static User joinOAuth2User(String name, String email, Provider provider) {
        return User.oAuth2UserBuilder()
                .name(name)
                .email(email)
                .provider(provider)
                .build();
    }

    // OAuth2 회원가입시 사용
    public User updateOAuth2UserBirth(LocalDate birth) {
        this.birth = birth;
        return this;
    }
}
