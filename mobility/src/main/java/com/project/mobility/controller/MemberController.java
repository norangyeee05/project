package com.project.mobility.controller;

import com.project.mobility.dto.MemberRegisterForm;
import com.project.mobility.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    // 회원가입 폼
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new MemberRegisterForm());
        return "members/register";
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String register(@ModelAttribute("form") MemberRegisterForm form) {
        memberService.register(form);
        return "redirect:/login";
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("member", memberService.findByUserid(user.getUsername()));
        return "members/mypage";
    }

    // 회원 수정 폼
    @GetMapping("/edit")
    public String editForm(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("member", memberService.findByUserid(user.getUsername()));
        return "members/edit";
    }

    // 회원 수정 처리
    @PostMapping("/edit")
    public String edit(@AuthenticationPrincipal User user,
                       @ModelAttribute MemberRegisterForm form) {

        memberService.update(user.getUsername(), form);
        return "redirect:/members/mypage";
    }

    // 회원 탈퇴 (비활성화)
    @GetMapping("/delete")
    public String delete(@AuthenticationPrincipal User user) {
        memberService.disable(user.getUsername());
        return "redirect:/logout";
    }
}