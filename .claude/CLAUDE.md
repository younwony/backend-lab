# Backend-Lab 프로젝트

Spring Boot 4.0 기반 멀티 모듈 Gradle 프로젝트

## 기술 스택

- Java 17
- Spring Boot 4.0.0
- Gradle 9.2.1

## 설정 규칙

- 설정 파일: `application.yml` (properties 사용 금지)
- 패키지: `dev.wony.backendlab.{모듈명}`

## 모듈 구조

| 모듈 | 설명 |
|------|------|
| common | 공통 유틸리티 |
| api-test | API 테스트 |
| library-sandbox | 라이브러리 샌드박스 |
| architecture-patterns | 아키텍처 패턴 |
| macro | HTTP 매크로 (CLI) |
| board | 게시판 REST API |
| claude-code | Thymeleaf 웹앱 |
| antigravity | 정적 웹사이트 |

## 공통 의존성 (루트 build.gradle)

- Lombok
- Apache Commons Lang3
- Google Guava
- Logback
- Spring Boot Test, Mockito, OkHttp

## 코드 컨벤션

### 상수 사용
```java
private static final String ERROR_MESSAGE = "오류 메시지";
```

### 의존성 주입 (테스트 용이성)
```java
public HttpMacro() {
    this(HttpClients::createDefault);
}

public HttpMacro(HttpClientFactory clientFactory) {
    this.clientFactory = clientFactory;
}
```

### 네이밍
- 클래스: PascalCase
- 메서드/변수: camelCase
- 상수: UPPER_SNAKE_CASE

## 테스트 (Spring Boot 4.0)

```java
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
```

## Git

- 모든 작업은 `git add`까지만
- commit은 명시적 요청 시에만

## 빌드

```bash
./gradlew build
./gradlew :macro:run --args="https://example.com 5 3"
```
