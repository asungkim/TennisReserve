package com.project.tennis.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // salt(비밀번호마다 고유한 랜덤값)를 사용하여 비밀번호 암호화(해싱)하는 인코더 // 단방향 해싱 함수라서 복호화 불가
        return new BCryptPasswordEncoder();
    }
}
