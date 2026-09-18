package com.project.mobility.controller;

import com.project.mobility.dto.BoardForm;
import com.project.mobility.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    // 글 목록
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "") String userid,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {

        Page<?> boards = boardService.findBoards(keyword, userid, page);
        model.addAttribute("boards", boards);
        return "boards/list";
    }

    // 글 상세
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        boardService.incrementHits(id);
        model.addAttribute("board", boardService.findDetail(id));
        return "boards/detail";
    }

    // 글 등록 폼
    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("form", new BoardForm());
        return "boards/form";
    }

    // 글 등록
    @PostMapping("/new")
    public String create(@ModelAttribute BoardForm form,
                         @AuthenticationPrincipal User user) {

        boardService.create(form, user.getUsername());
        return "redirect:/boards/list";
    }

    // 수정 폼
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", boardService.toForm(boardService.findDetail(id)));
        model.addAttribute("id", id);
        return "boards/edit";
    }

    // 수정 처리
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute BoardForm form,
                       @AuthenticationPrincipal User user) {

        boardService.update(id, form, user.getUsername(), false);
        return "redirect:/boards/detail/" + id;
    }

    // 삭제
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal User user) {

        boardService.delete(id, user.getUsername(), false);
        return "redirect:/boards/list";
    }
}