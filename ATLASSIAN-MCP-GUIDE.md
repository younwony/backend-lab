# Atlassian MCP 연동 가이드

Claude Code에서 Atlassian (Confluence, Jira) 연동 사용법

## 목차

1. [MCP 서버 연결 상태](#mcp-서버-연결-상태)
2. [사용자 정보](#사용자-정보)
3. [권한 범위](#권한-범위)
4. [Confluence 사용법](#confluence-사용법)
5. [Jira 사용법](#jira-사용법)
6. [진행 과정 기록](#진행-과정-기록)

---

## MCP 서버 연결 상태

```
┌─────────────────────────────────────────────────────────┐
│  Atlassian MCP Server                                   │
│  ✔ connected                                            │
├─────────────────────────────────────────────────────────┤
│  Cloud ID: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx        │
│  Site URL: https://{your-site}.atlassian.net            │
└─────────────────────────────────────────────────────────┘
```

### 연결 확인 방법

- `/mcp` 명령어로 MCP 서버 연결 상태 확인
- `mcp__atlassian__getAccessibleAtlassianResources` 도구로 Cloud ID 조회

---

## 사용자 정보

`mcp__atlassian__atlassianUserInfo` 도구로 조회:

| 항목 | 설명 |
|------|------|
| account_id | 사용자 고유 ID |
| name | 사용자 이름 |
| email | 이메일 주소 |
| account_status | 계정 상태 (active/inactive) |

---

## 권한 범위

### Confluence 권한

| 권한 | 설명 |
|------|------|
| `read:page:confluence` | 페이지 읽기 |
| `read:space:confluence` | 공간 읽기 |
| `read:comment:confluence` | 댓글 읽기 |
| `read:confluence-user` | 사용자 정보 읽기 |
| `search:confluence` | 검색 |
| `write:page:confluence` | 페이지 작성/수정 |
| `write:comment:confluence` | 댓글 작성 |

### Jira 권한

| 권한 | 설명 |
|------|------|
| `read:jira-work` | 이슈 조회 |
| `write:jira-work` | 이슈 생성/수정 |

---

## Confluence 사용법

### 개인 공간 정보

`mcp__atlassian__getConfluenceSpaces` (type: personal)로 조회:

```
┌─────────────────────────────────────────────────────────┐
│  Personal Space                                         │
├─────────────────────────────────────────────────────────┤
│  Name     : {username}                                  │
│  Key      : ~{space_key}                                │
│  Space ID : {numeric_id}                                │
│  Homepage : {homepage_id}                               │
│  Status   : current (활성)                              │
├─────────────────────────────────────────────────────────┤
│  URL: https://{site}.atlassian.net/wiki/spaces/~{key}   │
└─────────────────────────────────────────────────────────┘
```

### 주요 도구

#### 1. 공간 조회

```
mcp__atlassian__getConfluenceSpaces
├── cloudId: Cloud ID 또는 사이트 URL
├── type: personal | global | collaboration | knowledge_base
└── status: current | archived
```

#### 2. 페이지 조회

```
mcp__atlassian__getConfluencePage
├── cloudId: Cloud ID
└── pageId: 페이지 ID (URL에서 추출 가능)
```

#### 3. 페이지 생성

```
mcp__atlassian__createConfluencePage
├── cloudId: Cloud ID
├── spaceId: 공간 ID (숫자)
├── title: 페이지 제목
├── body: 페이지 내용 (Markdown 또는 ADF)
├── contentFormat: markdown | adf (기본: markdown)
├── parentId: 상위 페이지 ID (선택)
└── subtype: live (Live Doc 생성 시)
```

#### 4. 페이지 수정

```
mcp__atlassian__updateConfluencePage
├── cloudId: Cloud ID
├── pageId: 페이지 ID
├── body: 수정할 내용
├── title: 새 제목 (선택)
└── versionMessage: 버전 메시지 (선택)
```

#### 5. 검색 (CQL)

```
mcp__atlassian__searchConfluenceUsingCql
├── cloudId: Cloud ID
├── cql: CQL 쿼리 (예: "title ~ 'meeting' AND type = page")
└── limit: 결과 수 제한
```

#### 6. 통합 검색 (Rovo)

```
mcp__atlassian__search
└── query: 검색어 (Jira + Confluence 통합 검색)
```

### CQL 쿼리 예시

```sql
-- 제목으로 검색
title ~ "API 문서"

-- 특정 공간에서 검색
space = "{space_key}" AND title ~ "테스트"

-- 최근 수정된 페이지
lastModified >= "2025-01-01" ORDER BY lastModified DESC

-- 특정 라벨 검색
label = "api-doc"
```

---

## Jira 사용법

### 주요 도구

#### 1. 이슈 조회

```
mcp__atlassian__getJiraIssue
├── cloudId: Cloud ID
└── issueIdOrKey: 이슈 ID 또는 키 (예: PROJ-123)
```

#### 2. 이슈 생성

```
mcp__atlassian__createJiraIssue
├── cloudId: Cloud ID
├── projectKey: 프로젝트 키
├── issueTypeName: 이슈 유형 (Task, Bug, Story 등)
├── summary: 제목
├── description: 설명 (Markdown)
└── assignee_account_id: 담당자 ID (선택)
```

#### 3. 이슈 검색 (JQL)

```
mcp__atlassian__searchJiraIssuesUsingJql
├── cloudId: Cloud ID
├── jql: JQL 쿼리
└── maxResults: 결과 수 제한 (기본: 50, 최대: 100)
```

### JQL 쿼리 예시

```sql
-- 내 이슈
assignee = currentUser()

-- 특정 프로젝트의 열린 이슈
project = PROJ AND status != Done

-- 최근 생성된 이슈
created >= -7d ORDER BY created DESC
```

---

## 진행 과정 기록

### 초기 설정 체크리스트

#### 1. MCP 서버 연결 확인

```
✅ Atlassian MCP 서버 연결 성공
✅ 사용자 인증 완료
✅ Cloud ID 확인
```

#### 2. 권한 확인

```
✅ Confluence 읽기/쓰기 권한 확인
✅ Jira 읽기/쓰기 권한 확인
```

#### 3. 개인 공간 확인

```
✅ 개인 공간 존재 확인
✅ Space ID 조회 완료
```

#### 4. 테스트 페이지 생성

```
✅ 페이지 생성 성공
   - 제목: 테스트 페이지
   - 결과: 페이지 ID 및 URL 반환됨
```

---

## Confluence 문서화 Skill

자연어로 요청하면 자동으로 Confluence 문서를 생성합니다.

**Skill 파일**: `.claude/skills/confluence-docs.md`

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Confluence 문서 체계                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────┐                                                        │
│  │  프로젝트 문서   │  "프로젝트 개요 문서 만들어줘"                          │
│  └────────┬────────┘                                                        │
│           │                                                                  │
│           ├──► 프로젝트 개요                                                 │
│           ├──► 아키텍처 문서                                                 │
│           └──► 개발 환경 설정                                                │
│                                                                              │
│  ┌─────────────────┐                                                        │
│  │   API 문서       │                                                        │
│  └────────┬────────┘                                                        │
│           │                                                                  │
│           ├──► 내부 API 스펙      "BoardController API 스펙 작성해줘"       │
│           │    (개발자용 상세)                                               │
│           │                                                                  │
│           └──► Client API 가이드  "클라이언트용 API 가이드 만들어줘"        │
│                (외부 제공용)                                                 │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 사용법

### 자연어 요청 예시

슬래시 커맨드 없이 자연어로 요청하면 됩니다:

| 문서 유형 | 요청 예시 |
|----------|----------|
| 프로젝트 개요 | "backend-lab 프로젝트 문서 Confluence에 올려줘" |
| | "board 모듈 개요 문서 만들어줘" |
| 내부 API 스펙 | "BoardController API 스펙 문서 작성해줘" |
| | "MemberController 내부 API 문서 만들어줘" |
| Client API 가이드 | "board API 클라이언트 가이드 만들어줘" |
| | "외부용 회원 API 문서 작성해줘" |
| | "파트너사 제공용 API 가이드" |

---

## 문서 유형별 특징

### 1. 프로젝트 개요 문서

프로젝트/모듈의 전반적인 정보를 문서화합니다.

**포함 내용:**
- 프로젝트 목적 및 개요
- 기술 스택 (Java, Spring Boot, Gradle 버전)
- 모듈 구조 및 의존성
- 주요 기능 목록
- 환경 설정 방법

### 2. 내부 API 스펙 문서

백엔드 개발자용 상세 API 스펙을 문서화합니다.

**포함 내용:**
- 엔드포인트 목록 및 HTTP 메서드
- Request/Response DTO 상세 구조
- 파라미터 검증 규칙 (@NotBlank, @Size 등)
- 에러 코드 및 예외 처리
- 인증/인가 요구사항

### 3. Client API 가이드

외부 클라이언트에게 제공할 API 가이드를 생성합니다.

**포함 내용:**
- 시작하기 (Base URL, 인증 방식)
- Quick Start 예제 (curl)
- SDK 사용 예제 (JavaScript, Java)
- 에러 처리 가이드
- FAQ

---

## 내부 API vs Client API 비교

| 구분 | 내부 API 스펙 | Client API 가이드 |
|------|--------------|------------------|
| **대상** | 백엔드 개발자 | 프론트엔드, 모바일, 외부 파트너 |
| **상세도** | 매우 상세 (DTO 검증 규칙 포함) | 필요한 정보만 간결하게 |
| **코드 예시** | Java 코드 중심 | curl, JavaScript, SDK 예시 |
| **에러 정보** | 모든 에러 코드 + 내부 처리 로직 | 공통 에러 + 조치 방법 |
| **보안 정보** | 인증/인가 상세 구현 | 인증 방법 + 토큰 발급 |

---

## 활용 시나리오

### 1. 신규 프로젝트 문서화

```
"my-project 프로젝트 개요 문서 Confluence에 올려줘"
"UserController API 스펙 문서 만들어줘"
"클라이언트용 API 통합 가이드 작성해줘"
```

### 2. API 변경 시 문서 업데이트

```
"BoardController API 스펙 문서 업데이트해줘"
"board 클라이언트 가이드 수정해줘"
```

### 3. 외부 파트너 연동 지원

```
"파트너사에 제공할 결제 API 가이드 만들어줘"
```

### 4. Jira 이슈 연동

```
"PROJ-123 관련 API 문서 작성해줘"
```

---

## 참고 자료

- [Confluence REST API 문서](https://developer.atlassian.com/cloud/confluence/rest/)
- [Jira REST API 문서](https://developer.atlassian.com/cloud/jira/platform/rest/)
- [CQL 문법 가이드](https://developer.atlassian.com/cloud/confluence/advanced-searching-using-cql/)
- [JQL 문법 가이드](https://support.atlassian.com/jira-software-cloud/docs/what-is-advanced-search-in-jira-cloud/)
