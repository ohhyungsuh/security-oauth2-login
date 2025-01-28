package com.security.oauth.config;

import com.security.oauth.user.handler.OAuth2FailureHandler;
import com.security.oauth.user.handler.OAuth2SuccessHandler;
import com.security.oauth.user.jwt.JwtFilter;
import com.security.oauth.user.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JwtFilter jwtFilter;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/error");
    }

    private final String[] whitelist = {
            "/", "/login", "/logout", "/auth/success",
            "/css/**", "/error"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // csrf 비활성화 -> cookie를 사용하지 않으면 꺼도 됨 (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어)
                // jwt 발급해서 stateless 상태로 관리하기 때문에 꺼도 된다
                .csrf(AbstractHttpConfigurer::disable)

                // 기본 loginForm 비활성화 - jwt + oauth2 사용하기 때문에
                .formLogin(AbstractHttpConfigurer::disable)

                // 기본 인증 로그인 비활성화 - jwt + oauth2 사용하기 때문에
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션 비활성화 - jwt에서 주로 설정하며, 인증 정보를 매 요청마다 클라이언트가 서버에 보내야 한다
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .cors(cors -> cors.configurationSource(setCorsConfigurationSource()))

                // whitelist로 설정 하는 것이 깔끔
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whitelist).permitAll()
                        .anyRequest().authenticated())

                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    // CorsConfigurationSource의 구현체는 UrlBasedCorsConfigurationSource
    // WebMvcConfig에 설정하는 것과 차이?
    public CorsConfigurationSource setCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // 프론트 주소 필요
        configuration.setAllowedMethods(List.of("POST", "GET", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));

//        // 쿠키 필요 없어서 우선 주석
//        configuration.setAllowCredentials(true);
//
//        configuration.setExposedHeaders(List.of("Authorization"));
//        configuration.setExposedHeaders(List.of("Set-Cookie"));


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);


        return source;
    }
}
