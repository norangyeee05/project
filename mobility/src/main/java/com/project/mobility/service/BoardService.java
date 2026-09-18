package com.project.mobility.service;

import com.project.mobility.domain.Board;
import com.project.mobility.domain.Member;
import com.project.mobility.dto.BoardForm;
import com.project.mobility.repository.BoardRepository;
import com.project.mobility.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    //목록
    public Page<Board> findBoards(String keyword, String userid, int page) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), 10);
        return boardRepository.searchByQuerydsl(keyword, userid, pageable);
    }

    //글 상세 보기
    public Board findDetail(Long id) { //옵셔널 처리(Optional<Board> => Board)
        return boardRepository.findDetailById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
    }

    //글 등록
    @Transactional
    public Long create(BoardForm form, String userid) { //BoardForm => Board
        Member member = memberRepository.findByUserid(userid)
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

        Board board = Board.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .member(member)
                .build();

        return boardRepository.save(board).getId();
    }

    //글 수정
    @Transactional
    public void update(Long id, BoardForm form, String userid, boolean admin) {
        Board board = findDetail(id);
        if (!admin && !board.getMember().getUserid().equals(userid)) {
            throw new org.springframework.security.access.AccessDeniedException("수정 권한이 없습니다.");
        }
        board.setTitle(form.getTitle());
        board.setContent(form.getContent());
        boardRepository.save(board);
    }

    //글 삭제
    @Transactional
    public void delete(Long id, String userid, boolean admin) {
        Board board = findDetail(id);
        if (!admin && !board.getMember().getUserid().equals(userid)) {
            throw new org.springframework.security.access.AccessDeniedException("삭제 권한이 없습니다.");
        }
        boardRepository.delete(board);
    }

    //읽은 횟수 증가
    @Transactional
    public void incrementHits(Long id) {
        boardRepository.increaseViewCount(id);
    }

    //Board => BoardForm
    public BoardForm toForm(Board board) {
        BoardForm form = new BoardForm();
        form.setTitle(board.getTitle());
        form.setContent(board.getContent());
        return form;
    }

    public long countByMemberId(Long memberId) {
        return boardRepository.countByMemberIdJpql(memberId);
    }
}