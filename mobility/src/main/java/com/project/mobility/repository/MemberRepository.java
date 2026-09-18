package com.project.mobility.repository;

import com.project.mobility.domain.Member;
import com.project.mobility.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserid(String userid); //기본 타입을 Member의 Long으로 지정했기에, userid의 경우 불러와야 함
    boolean existsByUserid(String userid); //현재 해당 아이디 존재 여부 확인
    boolean existsByEmail(String email); //이메일 검증
    long countByRole(Role role); //Role.USER, Role.ADMIN, p176 참고

    //선택해서 활용하면 돼
    List<Member> findByEmail(String keyword);
    List<Member> findByRoleOrderByCreatedAtDesc(Role role);
    List<Member> findByEnabledTrue();
    List<Member> findByRoleIn(Collection<Role> role);
    long countByCreatedAtAfter(LocalDateTime dateTime);
    List<Member> findTop10ByOrderByCreatedAtDesc();
    Page<Member> findByRole(Role role, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.enabled = true")
            Optional<Member> findActiveByEmail(@Param("email") String email);
    @Query(value = "SELECT * FROM Member WHERE YEAR(created_at) = :year", nativeQuery = true)
            List<Member> findByJoinYear(@Param("year") int year);
    @Modifying
    @Query("UPDATE Member m SET m.role = :role WHERE m.id IN :ids")
    int bulkUpdateRole(@Param("role") Role role, @Param("ids") List<Long> ids);

    //select * from member where name like '%' & keyword + '%';
    List<Member> findByNameContaining(String keyword);
    List<Member> findByNameStartingWith(String prefix);
    List<Member> findByNameEndingWith(String suffix);
    List<Member> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT m FROM Member m " +
            "WHERE m.name LIKE %:keyword% " +
            "OR m.email LIKE %:keyword% " +
            "OR m.userid LIKE %:keyword% " +
            "OR m.tel LIKE %:keyword%")
    Page<Member> searchMemberList( //Param로 받아서 뒤에 keyword로 전달, keyword 하나로 해도 됨!
            @Param("keyword") String keyword,
            Pageable pageable);
}
