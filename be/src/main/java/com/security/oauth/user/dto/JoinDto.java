package com.security.oauth.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinDto {

    private String name;

    private String email;

    private String password;

    private LocalDate birth;

}
