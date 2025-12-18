# Confluence API 문서 등록

API 문서를 Confluence에 등록합니다.

## 입력 파라미터
- $ARGUMENTS: API 클래스 경로 또는 설명 (예: "UserController" 또는 "src/main/java/.../UserController.java")

## 작업 순서

1. **API 분석**: 지정된 API 클래스를 분석하여 다음 정보를 추출합니다:
   - 엔드포인트 URL 및 HTTP 메서드
   - Request/Response DTO 구조
   - 파라미터 설명
   - 에러 응답 코드

2. **문서 생성**: 다음 형식으로 Confluence 문서를 작성합니다:

```
## API 개요
- API 이름: [API 이름]
- 버전: [버전]
- 담당자: [담당자]

## 엔드포인트 목록

### [HTTP Method] [URL]
**설명**: [기능 설명]

**Request**
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|-----|------|
| ... | ... | ... | ... |

**Response**
| 필드 | 타입 | 설명 |
|-----|------|------|
| ... | ... | ... |

**에러 코드**
| 코드 | 메시지 | 설명 |
|-----|-------|------|
| ... | ... | ... |
```

3. **Confluence 등록**: Atlassian MCP 도구를 사용하여 Confluence에 페이지를 생성하거나 업데이트합니다.
   - 사용 가능한 MCP 도구: `confluence_create_page`, `confluence_update_page`, `confluence_search`
   - Space Key와 상위 페이지 ID가 필요할 수 있습니다.

## 실행 방법

```
/confluence-api-doc UserController
```

## 주의사항
- Atlassian MCP 서버 연결이 필요합니다. `/mcp` 명령으로 인증 상태를 확인하세요.
- Confluence Space Key를 알고 있어야 합니다.
