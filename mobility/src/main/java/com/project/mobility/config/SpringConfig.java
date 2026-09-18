package com.project.mobility.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity //나는 메서드로 호출 가능하게 할래
public class SpringConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        //인가 방법 (http 요청에 의한)
        .authorizeHttpRequests(auth -> auth
               //모든 사용자 대한 접근 -> permitAll() | **: css/?/? 등 안에 있는 폴더까지 권한 부여됨
              .requestMatchers("/", "/login", "/members/register", "/css/**", "/js/**", "/images/**","/error","/favicon.ico")
              .permitAll()
               //최고관리자(ADMIN) 대한 접근 -> hsaRole("ADMIN")
              .requestMatchers("/admin/**").hasRole("ADMIN")
               //회원(USER) 대한 접근 -> hasRole("USER")
              .requestMatchers("/boards/new").authenticated()
              .requestMatchers("/boards/edit/*", "/boards/delete/*").authenticated()
              .anyRequest().permitAll()
        )
        //로그인 방법
        .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/") //로그인 성공시 인덱스 파일로
                .failureUrl("/login?error=true")
                .permitAll()
        )
        //로그아웃 방법
        .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID") //쿠키 삭제
        )
        //예외 페이지(403 페이지: 권한 없는데 요청하는거, 404: 없는 url 요청하는거)
        .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
        );
        return http.build();
    }
}
