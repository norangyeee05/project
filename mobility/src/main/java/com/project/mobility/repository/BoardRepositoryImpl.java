package com.project.mobility.repository;

import com.project.mobility.domain.Board;
import com.project.mobility.domain.QBoard;
import com.project.mobility.domain.QMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Board> searchByQuerydsl(String keyword, String userid, Pageable pageable) {
        QBoard b = QBoard.board;
        QMember m = QMember.member;

        BooleanBuilder condition = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            condition.and(
                    b.title.containsIgnoreCase(keyword)
                            .or(b.content.containsIgnoreCase(keyword))
                            .or(m.name.containsIgnoreCase(keyword))
            );
        }

        if (userid != null && !userid.isBlank()) {
            condition.and(m.userid.eq(userid));
        }

        List<Board> content = jpaQueryFactory
                .selectFrom(b)
                .join(b.member, m).fetchJoin()
                .where(condition)
                .orderBy(b.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(b.count())
                .from(b)
                .join(b.member, m)
                .where(condition)
                .fetchOne(); //조회하는 게 1건일 때, 여러건일 때 쓰면 오류가 남

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
