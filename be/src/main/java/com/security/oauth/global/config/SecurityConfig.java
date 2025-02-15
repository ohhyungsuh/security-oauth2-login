package com.security.oauth.global.config;

import com.security.oauth.user.handler.OAuth2FailureHandler;
import com.security.oauth.user.handler.OAuth2SuccessHandler;
import com.security.oauth.user.jwt.CustomLoginFilter;
import com.security.oauth.user.jwt.JwtFilter;
import com.security.oauth.user.jwt.JwtProvider;
import com.security.oauth.user.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JwtFilter jwtFilter;

    private final JwtProvider jwtProvider;
    private final AuthenticationConfiguration authenticationConfiguration;

    // 홈 화면, css, error 페이지, 회원가입 및 로그인 api
    private final String[] whitelist = {
            "/", "/login", "/endpoint",
            "/oauth/callback", "/oauth/join",
            "/api/v1/users/oauth/join",
            "/api/v1/users/join",
            "/css/**", "/error"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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

                .addFilterAt(new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CorsConfigurationSource의 구현체는 UrlBasedCorsConfigurationSource
    // WebMvcConfig에 설정하는 것과 차이?
    public CorsConfigurationSource setCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // 프론트 주소 필요
        configuration.setAllowedMethods(List.of("POST", "GET", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));


        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Set-Cookie", "access", "refresh"));

//        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
