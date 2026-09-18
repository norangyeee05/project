package com.project.mobility.service;

import com.project.mobility.domain.Member;
import com.project.mobility.domain.Role;
import com.project.mobility.dto.MemberRegisterForm;
import com.project.mobility.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    //회원 가입시 이미 사용 중인 아이디와 이메일 인지 여부 확인
    @Transactional
    public boolean doubleCheck(MemberRegisterForm form){
        if (memberRepository.existsByUserid(form.getUserid())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (memberRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        return true;
    }

    //관리자용 회원 목록
    @Transactional(readOnly = true)
    public Page<Member> findAll(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "id")
        );
        if (keyword == null || keyword.isBlank()) {
            return memberRepository.findAll(pageable);
        }
        return memberRepository.searchMemberList( keyword, pageable );
    }

    //회원 상세보기 및 마이페이지
    @Transactional(readOnly = true)
    public Member findByUserid(String userid) {
        return memberRepository.findByUserid(userid)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다.")
                );
    }

    //회원 가입
    @Transactional
    public void register(MemberRegisterForm form) {
        if (memberRepository.existsByUserid(form.getUserid())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (memberRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        Member member = Member.builder()
                .userid(form.getUserid())
                .password(passwordEncoder.encode(form.getPassword()))
                .name(form.getName())
                .tel(form.getTel())
                .email(form.getEmail())
                .role(Role.USER)
                .enabled(true)
                .build();

        memberRepository.save(member);
    }

    //회원 정보 수정
    @Transactional
    public void update(String userid, MemberRegisterForm form) {
        Member member = memberRepository.findByUserid(userid)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        member.setName(form.getName());
        member.setTel(form.getTel());
        member.setEmail(form.getEmail());

        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            member.setPassword(passwordEncoder.encode(form.getPassword()));
        }
    }

    //회원 활성/비활성 변경
    @Transactional
    public void changeEnabled(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다.") ); member.setEnabled(!member.isEnabled());
    }

    //관리자용 회원 삭제
    @Transactional
    public void delete(Long id) {
        memberRepository.deleteById(id);
    }

    //전체 회원 수 조회
    public long countMembers() {
        return memberRepository.count();
    }

    //일반 회원 수 조회
    public long countUsers() {
        return memberRepository.countByRole(Role.USER);
    }

    //관리자 회원 수 조회
    public long countAdmins() {
        return memberRepository.countByRole(Role.ADMIN);
    }
    // 회원 탈퇴 (비활성화)
    @Transactional
    public void disable(String userid) {
        Member member = memberRepository.findByUserid(userid)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        member.setEnabled(false);
    }
}