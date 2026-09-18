package com.project.mobility.repository;

import com.project.mobility.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

//QueryDSL
public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    //Query Method -> 반환타입과 조합된 메서드 이름에 맞춰 선언(활성화)만 하면 됨
    // 전체 게시글 최신순 페이징
    Page<Board> findAllByOrderByIdDesc(Pageable pageable);

    // 제목 or 내용 검색
    Page<Board> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title, String content, Pageable pageable
    );

    //특정 userid 가진 사람의 글 목록(Page<Board>)으로 반환
    Page<Board> findByMemberUserid(String userid, Pageable pageable);

    //특정 userid 가진 사람의 글 상세보기 1건 반환
    Optional<Board> findByIdAndMemberUserid(Long id, String userid);

    //JPQL로 특정 글 번호(board.id)를 조건으로 게시판 정보와 회원 정보를 모두 상세 검색
    @Query("select b from Board b join fetch b.member where b.id = :id")
    Optional<Board> findDetailById(@Param("id") Long id);

    //JPQL로 특정 회원 번호(member.id)를 조건으로 글 수를 반환
    @Query("select count(b) from Board b where b.member.id = :memberId")
    long countByMemberIdJpql(@Param("memberId") Long memberId);

    //JPQL로 글을 읽은 횟수(hits)를 글을 읽을 때마다 증가하는 문장
    @Modifying
    @Query("update Board b set b.hits = b.hits + 1 where b.id = :id")
    int increaseViewCount(@Param("id") Long id);
}
