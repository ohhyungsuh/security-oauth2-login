package com.security.oauth.user.domain;

import com.security.oauth.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
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

    private boolean isNew;

    // 일반 회원가입 생성자
    private User(String name, String email, String password, LocalDate birth) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birth = birth;

        this.provider = Provider.LOCAL;
        this.role = Role.USER;
        this.isDeleted = false;
        this.isNew = false;
    }

    // 소셜 회원가입 생성자
    private User(String name, String email, Provider provider) {
        this.name = name;
        this.email = email;
        this.provider = provider;

        this.password = "";
        this.role = Role.USER;
        this.isDeleted = false;
        this.isNew = true;
    }

    public static User joinLocalUser(String name, String email, String password, LocalDate birth) {
        return new User(name, email, password, birth);
    }

    public static User joinOAuth2User(String name, String email, Provider provider) {
        return new User(name, email, provider);
    }

    // OAuth2 회원가입시 사용
    public void completeSignup(LocalDate birth) {
        this.birth = birth;
        this.isNew = false;
    }
}
