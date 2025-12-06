# Backend-Lab 프로젝트 설정

## 프로젝트 개요

Spring Boot 4.0 기반 멀티 모듈 Gradle 프로젝트

## 패키지 구조

### 기본 패키지 네이밍
```
dev.wony.backendlab.{모듈명}
```

### 모듈별 패키지
| 모듈 | 패키지 | 설명 |
|------|--------|------|
| common | `dev.wony.backendlab.common` | 공통 유틸리티 |
| api-test | `dev.wony.backendlab.apitest` | API 테스트 |
| library-sandbox | `dev.wony.backendlab.librarysandbox` | 라이브러리 샌드박스 |
| architecture-patterns | `dev.wony.backendlab.patterns` | 아키텍처 패턴 |
| macro | `dev.wony.backendlab.macro` | 웹 자동화 매크로 |
| board | `dev.wony.backendlab.board` | 게시판 REST API |
| claude-code | `dev.wony.backendlab.claudecode` | 계절 테마 웹앱 |
| antigravity | (정적 웹사이트) | 글래스모피즘 UI |

## 기술 스택

### 버전
- Java: 17
- Spring Boot: 4.0.0
- Gradle: 9.2.1

### 공통 의존성 (루트 build.gradle에서 관리)
- Lombok
- Apache Commons Lang3 3.14.0
- Apache Commons Collections4 4.4
- Google Guava 33.0.0-jre
- Spring Boot Test

## build.gradle 작성 규칙

### 모듈별 build.gradle
루트에서 공통 설정을 관리하므로 모듈별 build.gradle은 최소화:

```gradle
plugins {
    id 'org.springframework.boot'  // Spring Boot 애플리케이션인 경우에만
}

description = '{모듈명} - {설명}'

dependencies {
    // 모듈별 특정 의존성만 선언
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
}
```

### Spring Boot 4.0 의존성 변경사항
| 기존 (3.x) | 신규 (4.0) |
|------------|-----------|
| `spring-boot-starter-web` | `spring-boot-starter-webmvc` |
| 테스트: `spring-boot-starter-test`에 포함 | `spring-boot-webmvc-test`, `spring-boot-data-jpa-test` 별도 추가 |

## 코드 컨벤션

### 1. 상수 사용
```java
// ❌ 잘못된 예
throw new IllegalArgumentException("게시글이 존재하지 않습니다.");

// ✅ 올바른 예
private static final String BOARD_NOT_FOUND_MESSAGE = "게시글이 존재하지 않습니다.";
throw new IllegalArgumentException(BOARD_NOT_FOUND_MESSAGE);
```

### 2. 불변 객체 재사용
```java
// ❌ 잘못된 예
LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

// ✅ 올바른 예
private static final DateTimeFormatter DATE_TIME_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
LocalDateTime.now().format(DATE_TIME_FORMATTER);
```

### 3. 의존성 주입 (테스트 용이성)
```java
// ✅ 외부 주입 가능하도록 오버로딩
public NaverMacro(String id, String pw, String targetUrl) {
    this(id, pw, targetUrl, createDefaultDriver());
}

public NaverMacro(String id, String pw, String targetUrl, WebDriver driver) {
    this.driver = driver;
}
```

### 4. Lombok 사용
```java
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
public class Board {
    // ...
}
```

### 5. 네이밍 규칙
- 클래스: PascalCase (`BoardController`)
- 메서드/변수: camelCase (`findById`)
- 상수: UPPER_SNAKE_CASE (`BOARD_NOT_FOUND_MESSAGE`)
- 패키지: lowercase (`dev.wony.backendlab.board`)

## 테스트 코드 규칙

### Spring Boot 4.0 테스트 import
```java
// WebMvcTest
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

// DataJpaTest
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

// MockBean (Spring Boot 4.0에서 변경됨)
import org.springframework.test.context.bean.override.mockito.MockitoBean;
```

### 테스트 작성 패턴
```java
@WebMvcTest(BoardController.class)
@DisplayName("BoardController 테스트")
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean  // @MockBean 대신 사용
    private BoardService boardService;

    @Test
    @DisplayName("게시글 저장 요청 시 저장된 게시글을 반환한다")
    void saveBoard_ReturnsSavedBoard() throws Exception {
        // Given
        BoardDto requestDto = BoardDto.builder()
                .title("테스트 제목")
                .build();

        // When & Then
        mockMvc.perform(post("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }
}
```

### 테스트 규칙
- `@DisplayName`으로 테스트 의도 명확히 표현 (한글 권장)
- Given-When-Then 패턴 사용
- 하나의 테스트는 하나의 기능만 검증

## 프로젝트 구조

```
backend-lab/
├── build.gradle              # 루트 빌드 설정 (공통 의존성)
├── settings.gradle           # 모듈 포함 설정
├── common/                   # 공통 유틸리티
├── api-test/                 # API 테스트
├── library-sandbox/          # 라이브러리 실험
├── architecture-patterns/    # 아키텍처 패턴 (DDD 등)
├── macro/                    # Selenium 웹 자동화
├── board/                    # 게시판 REST API
├── claude-code/              # 계절 테마 Thymeleaf 웹앱
└── antigravity/              # 정적 웹사이트
```

## Git 워크플로우

### 커밋 규칙
- 모든 작업은 `git add`까지만 진행
- commit은 사용자가 명시적으로 요청 시에만 실행

### 커밋 메시지 형식
```
<type>: <subject>

<body>

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

**Type 종류:**
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `refactor`: 리팩토링
- `test`: 테스트 코드
- `docs`: 문서 수정
- `style`: 코드 포맷팅
- `chore`: 빌드/설정

## 빌드 및 테스트 명령어

```bash
# 전체 컴파일
./gradlew compileJava

# 모듈별 테스트
./gradlew :macro:test
./gradlew :board:test
./gradlew :claude-code:test

# 전체 테스트
./gradlew test
```

## 주의사항

### Spring Boot 4.0 마이그레이션
1. `@MockBean` → `@MockitoBean` 사용
2. 테스트 autoconfigure 패키지 경로 변경됨
3. `spring-boot-starter-web` → `spring-boot-starter-webmvc`

### 인코딩
- 소스 파일: UTF-8
- Windows 환경에서 한글 주석 사용 시 인코딩 주의
