# Confluence 문서화 Skill

Confluence에 프로젝트 문서, API 스펙, Client 가이드를 생성하고 관리합니다.

## 트리거 조건

다음과 같은 요청 시 이 skill을 사용합니다:
- "Confluence에 문서 올려줘", "컨플루언스 문서 작성해줘"
- "API 문서 만들어줘", "API 스펙 작성해줘"
- "클라이언트용 API 가이드", "외부 API 문서"
- "프로젝트 개요 문서", "프로젝트 문서화"

---

## MCP 연결 정보

MCP 도구 호출 시 필요한 정보는 다음 도구로 조회합니다:

```yaml
# Cloud ID 조회
mcp__atlassian__getAccessibleAtlassianResources

# 개인 공간 정보 조회
mcp__atlassian__getConfluenceSpaces (type: personal, favoritedBy: 현재 사용자 ID)

# 사용자 정보 조회
mcp__atlassian__atlassianUserInfo
```

---

## 문서 유형

### 1. 프로젝트 개요 문서

**용도**: 프로젝트/모듈의 전반적인 정보 문서화

**요청 예시**:
- "backend-lab 프로젝트 문서 Confluence에 올려줘"
- "board 모듈 개요 문서 만들어줘"

**템플릿**:

```markdown
# [프로젝트명]

## 개요
- **목적**: [프로젝트 목적]
- **담당팀**: [담당팀]
- **담당자**: [담당자]
- **최종 수정일**: [날짜]

## 기술 스택

| 구분 | 기술 | 버전 |
|------|------|------|
| Language | Java | 17 |
| Framework | Spring Boot | 4.0.0 |
| Build Tool | Gradle | 9.2.1 |

## 모듈 구조

[ASCII 다이어그램]

## 주요 기능

### 1. [기능명]
- **설명**: [기능 설명]
- **관련 모듈**: [모듈명]

## 환경 설정

### 로컬 개발 환경
[설정 방법]

### 빌드 및 실행
[빌드 명령어]

## 관련 문서
- [아키텍처 문서](링크)
- [API 스펙 문서](링크)
```

---

### 2. 내부 API 스펙 문서

**용도**: 백엔드 개발자용 상세 API 스펙

**요청 예시**:
- "BoardController API 스펙 문서 만들어줘"
- "MemberController 내부 API 문서 작성해줘"

**작업 순서**:
1. Controller 클래스 분석
2. Request/Response DTO 구조 추출
3. 검증 규칙, 에러 코드 정리
4. Confluence 페이지 생성

**템플릿**:

```markdown
# [API명] API 스펙

## 개요
- **버전**: v1.0.0
- **Base URL**: /api/v1/[리소스]
- **담당자**: [담당자]
- **최종 수정일**: [날짜]

## 인증
- **방식**: [Bearer Token / Session]
- **헤더**: `Authorization: Bearer {token}`

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | /api/v1/resources | 목록 조회 | O |
| POST | /api/v1/resources | 생성 | O |
| GET | /api/v1/resources/{id} | 상세 조회 | O |
| PUT | /api/v1/resources/{id} | 수정 | O |
| DELETE | /api/v1/resources/{id} | 삭제 | O |

---

## API 상세

### [GET] /api/v1/resources

[API 설명]

#### Request

**Query Parameters**

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|-----|--------|------|
| page | Integer | N | 0 | 페이지 번호 |
| size | Integer | N | 20 | 페이지 크기 |

**Headers**

| 헤더 | 필수 | 설명 |
|------|-----|------|
| Authorization | O | Bearer {accessToken} |

#### Response

**Success (200 OK)**

| 필드 | 타입 | 설명 |
|-----|------|------|
| content | Array | 결과 목록 |
| totalElements | Long | 전체 건수 |

#### Error Responses

| 상태 코드 | 에러 코드 | 메시지 |
|----------|----------|--------|
| 400 | INVALID_PARAMETER | 잘못된 파라미터 |
| 401 | UNAUTHORIZED | 인증 필요 |
| 404 | NOT_FOUND | 리소스 없음 |

---

## DTO 정의

### [Resource]Request

| 필드 | 타입 | 필수 | 검증 | 설명 |
|-----|------|-----|------|------|
| name | String | O | @NotBlank, @Size(max=100) | 이름 |

### [Resource]Response

| 필드 | 타입 | 설명 |
|-----|------|------|
| id | Long | ID |
| name | String | 이름 |
| createdAt | DateTime | 생성일시 |

---

## 에러 코드 정의

| 코드 | HTTP Status | 메시지 | 설명 |
|-----|-------------|--------|------|
| RESOURCE_NOT_FOUND | 404 | 리소스를 찾을 수 없습니다 | 존재하지 않음 |
| DUPLICATE_NAME | 409 | 중복된 이름입니다 | 이름 중복 |
```

---

### 3. Client API 가이드

**용도**: 프론트엔드, 모바일, 외부 파트너에게 제공

**요청 예시**:
- "board API 클라이언트 가이드 만들어줘"
- "외부용 회원 API 문서 작성해줘"
- "파트너사 제공용 API 가이드"

**템플릿**:

```markdown
# [서비스명] API 가이드

## 시작하기

### Base URL

| 환경 | URL |
|------|-----|
| Production | https://api.example.com |
| Staging | https://staging-api.example.com |

### 인증 방식

Authorization: Bearer {ACCESS_TOKEN}

#### 토큰 발급

1. 로그인 API 호출
2. 응답에서 accessToken 획득
3. 이후 API 호출 시 Header에 포함

### 공통 응답 형식

**성공**
{
  "success": true,
  "data": { ... }
}

**에러**
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "에러 메시지"
  }
}

---

## Quick Start

### 1단계: 인증

curl -X POST "https://api.example.com/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "****"}'

### 2단계: API 호출

curl -X GET "https://api.example.com/api/v1/resources" \
  -H "Authorization: Bearer {TOKEN}"

---

## API 목록

| API | Method | Endpoint | 설명 |
|-----|--------|----------|------|
| 목록 | GET | /api/v1/resources | 목록 조회 |
| 상세 | GET | /api/v1/resources/{id} | 상세 조회 |
| 등록 | POST | /api/v1/resources | 신규 등록 |
| 수정 | PUT | /api/v1/resources/{id} | 수정 |
| 삭제 | DELETE | /api/v1/resources/{id} | 삭제 |

---

## SDK 사용법

### JavaScript

const client = new ApiClient({
  baseUrl: 'https://api.example.com',
  accessToken: 'your-token'
});

const resources = await client.resources.list({ page: 0 });

### Java

@Autowired
private ResourceApiClient client;

Page<ResourceResponse> resources = client.getResources(0, 10);

---

## 에러 처리

| 코드 | Status | 설명 | 조치 |
|-----|--------|------|------|
| UNAUTHORIZED | 401 | 인증 실패 | 토큰 재발급 |
| FORBIDDEN | 403 | 권한 없음 | 권한 확인 |
| NOT_FOUND | 404 | 리소스 없음 | ID 확인 |
| RATE_LIMITED | 429 | 요청 한도 초과 | 잠시 후 재시도 |

### Rate Limiting
- 기본: 100 requests / minute
- 인증 사용자: 1000 requests / minute

---

## FAQ

### Q: 토큰 만료 시?
A: Refresh Token으로 새 Access Token 발급

### Q: CORS 에러?
A: 허용 도메인 등록 요청 필요
```

---

## MCP 도구 사용법

### 페이지 생성

```
mcp__atlassian__createConfluencePage
- cloudId: {조회한 Cloud ID}
- spaceId: {조회한 Space ID}
- title: 페이지 제목
- body: 마크다운 내용
- contentFormat: "markdown"
- parentId: 상위 페이지 ID (선택)
```

### 페이지 수정

```
mcp__atlassian__updateConfluencePage
- cloudId: {조회한 Cloud ID}
- pageId: 페이지 ID
- body: 수정할 내용
- title: 새 제목 (선택)
```

### 페이지 검색

```
mcp__atlassian__searchConfluenceUsingCql
- cloudId: {조회한 Cloud ID}
- cql: "title ~ 'API' AND space = '{Space Key}'"
```

---

## 작업 흐름

1. **요청 분석**: 문서 유형 파악 (프로젝트/API스펙/Client가이드)
2. **코드 분석**: 해당 모듈/Controller 코드 읽기
3. **템플릿 적용**: 위 템플릿에 맞춰 내용 작성
4. **Confluence 등록**: MCP 도구로 페이지 생성
5. **결과 보고**: 생성된 페이지 URL 제공

---

## 주의사항

- 민감 정보(비밀번호, API 키 등)는 마스킹 처리
- Client 가이드는 내부 구현 세부사항 제외
- 기존 페이지가 있으면 업데이트 여부 확인
- 페이지 생성 후 URL 반드시 안내
