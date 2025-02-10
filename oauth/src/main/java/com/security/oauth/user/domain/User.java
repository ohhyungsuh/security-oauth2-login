package com.security.oauth.user.domain;

import com.security.oauth.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private boolean isDeleted;

    @Builder
    public User(String name, String email) {
        this.name = name;
        this.email = email;

        this.role = Role.USER;
        this.isDeleted = false;
    }
}
