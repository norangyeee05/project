package com.project.mobility.config;

import com.project.mobility.domain.Member;
import com.project.mobility.domain.Role;
import com.project.mobility.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Bean
    CommandLineRunner initAdmin() {
        return args -> {
            if (!memberRepository.existsByUserid("admin")) {
                memberRepository.save(Member.builder()
                        .userid("admin")
                        .password(passwordEncoder.encode("admin1234"))
                        .name("관리자")
                        .tel("010-0000-0000")
                        .email("admin@git.local")
                        .role(Role.ADMIN)
                        .enabled(true)
                        .build());
            }
        };
    }
}
