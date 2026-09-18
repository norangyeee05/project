# 🚗 Mobility

회원가입/로그인, 게시판 CRUD, 관리자 페이지를 갖춘 Spring Boot 기반 웹 애플리케이션입니다.

## 기술 스택

| 구분 | 내용 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 (모듈화 스타터: `spring-boot-starter-webmvc` 등) |
| View | Thymeleaf + Bootstrap 5.3.3 (CDN) |
| Security | Spring Security (폼 로그인, 권한별 접근 제어) |
| DB / ORM | PostgreSQL, Spring Data JPA, QueryDSL 5.1.0 |
| Build | Gradle |

## 주요 기능

- **회원**: 회원가입, 로그인/로그아웃, 마이페이지, 정보 수정, 탈퇴(비활성화)
- **게시판**: 목록/검색/페이징, 상세(조회수 증가), 작성, 수정·삭제(작성자 본인만)
- **관리자**: 대시보드(회원/관리자 수 통계), 회원 관리(검색·활성/비활성·삭제), 게시글 관리(검색·상세·수정·삭제)

## 프로젝트 구조

```
com.project.mobility
├── config       # Security, QueryDSL, 초기 관리자 계정 생성(DataInitializer)
├── controller   # Admin / Board / Member / Form 컨트롤러
├── domain       # Board, Member, Role
├── dto          # BoardForm, MemberRegisterForm
├── repository   # JPA + QueryDSL
└── service      # BoardService, MemberService, CustomUserDetailsService
```

```
templates/
├── index.html, login.html
├── admin/     dashboard, members, boards, board-detail, board-edit
├── members/   register, mypage, edit
├── boards/    list, detail, form, edit
├── error/     403, 404
└── fragments/ navbar, footer   (공통 레이아웃)
```

## 실행 방법

1. PostgreSQL에 `mobility` 데이터베이스 생성 (`application.yml`에서 접속 정보 수정 가능)
2. 아래 명령으로 실행

   ```bash
   ./gradlew bootRun
   ```

3. `http://localhost:8081` 접속
4. 기본 관리자 계정(최초 실행 시 자동 생성): `admin` / `admin1234`

## 권한 정책

| 경로 | 접근 조건 |
|---|---|
| `/`, `/login`, `/members/register`, 정적 리소스 | 전체 허용 |
| `/boards/new`, `/boards/edit/**`, `/boards/delete/**` | 로그인 필요 (수정·삭제는 작성자 본인만) |
| `/admin/**` | `ROLE_ADMIN`만 |

## 알려진 이슈 / TODO

- [ ] `MemberController.delete()`의 `redirect:/logout`이 GET 요청이라, 기본 설정(POST만 처리)의 Spring Security 로그아웃과 매치되지 않음
- [ ] DTO의 `@NotBlank` 등 Bean Validation 애너테이션이 컨트롤러에서 `@Valid` 없이 선언되어 실제로 검증이 동작하지 않음
- [ ] `templates/board/`, `templates/member/`(단수형) 빈 폴더 정리 필요
