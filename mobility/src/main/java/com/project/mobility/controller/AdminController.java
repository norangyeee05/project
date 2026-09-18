package com.project.mobility.controller;

import com.project.mobility.service.BoardService;
import com.project.mobility.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.project.mobility.domain.Member;
import com.project.mobility.domain.Board;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;
    private final BoardService boardService;

    // 대시보드
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalMembers", memberService.countMembers());
        model.addAttribute("totalUsers", memberService.countUsers());
        model.addAttribute("totalAdmins", memberService.countAdmins());
        return "admin/dashboard";
    }

    // 회원 목록
    @GetMapping("/members")
    public String members(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "") String keyword,
                          Model model) {

        Page<Member> members = memberService.findAll(page, 10, keyword);
        model.addAttribute("members", members);
        return "admin/members";
    }

    // 회원 활성/비활성
    @PostMapping("/members/toggle/{id}")
    public String toggle(@PathVariable Long id) {
        memberService.changeEnabled(id);
        return "redirect:/admin/members";
    }

    // 회원 강퇴
    @PostMapping("/members/del/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberService.delete(id);
        return "redirect:/admin/members";
    }

    // 게시글 목록
    @GetMapping("/boards")
    public String boards(@RequestParam(defaultValue = "") String keyword,
                         @RequestParam(defaultValue = "") String userid,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {

        Page<Board> boards = boardService.findBoards(keyword, userid, page);
        model.addAttribute("boards", boards);
        return "admin/boards";
    }

    // 게시글 상세
    @GetMapping("/boards/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.findDetail(id));
        return "admin/board-detail";
    }

    // 게시글 수정 폼
    @GetMapping("/boards/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", boardService.toForm(boardService.findDetail(id)));
        model.addAttribute("id", id);
        return "admin/board-edit";
    }

    // 게시글 수정
    @PostMapping("/boards/edit/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute("form") com.project.mobility.dto.BoardForm form) {

        boardService.update(id, form, null, true); // admin=true
        return "redirect:/admin/boards";
    }

    // 게시글 삭제
    @PostMapping("/boards/del/{id}")
    public String deleteBoard(@PathVariable Long id) {
        boardService.delete(id, null, true); // admin=true
        return "redirect:/admin/boards";
    }
}