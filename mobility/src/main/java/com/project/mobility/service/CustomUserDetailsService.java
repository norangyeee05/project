package com.project.mobility.service;

import com.project.mobility.domain.Member;
import com.project.mobility.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    //id(X), userid(O) -> username(O)
    @Override
    public UserDetails loadUserByUsername(String userid)
            throws UsernameNotFoundException {

        Member member = memberRepository.findByUserid(userid)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "회원을 찾을 수 없습니다: " + userid
                        )
                );

        return User.builder()
                .username(member.getUserid())
                .password(member.getPassword())
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_" + member.getRole().name()
                        )
                )
                .disabled(!member.isEnabled())
                .build();
    }
}