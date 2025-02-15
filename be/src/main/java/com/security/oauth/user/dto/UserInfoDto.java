package com.security.oauth.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoDto {

    private String name;
    private String email;
    private String birth;
}
