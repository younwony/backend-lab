# lab-api-test

> 외부 API 호출, REST Client 테스트, HTTP 통신 패턴 실험 모듈

## 목적

외부 API와의 통신 패턴을 실험하고, 다양한 HTTP 클라이언트의 사용법과 성능을 비교합니다.

## 주요 실험 주제

### HTTP 클라이언트 비교
- **RestTemplate** (동기, Spring 전통 방식)
- **WebClient** (비동기/리액티브, Spring WebFlux)
- **RestClient** (Spring 6.1+, 새로운 동기 클라이언트)
- **Feign Client** (선언적 HTTP 클라이언트)

### API 호출 패턴
- 동기 vs 비동기 호출
- 타임아웃 설정 및 재시도 전략
- 서킷 브레이커 패턴 (Resilience4j)
- 벌크헤드 패턴

### 테스트 기법
- MockWebServer를 이용한 단위 테스트
- WireMock을 이용한 통합 테스트
- Testcontainers를 이용한 실제 환경 테스트

## 의존성

```groovy
dependencies {
    implementation project(':lab-common')

    // Spring Web
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // WebClient (Reactive)
    implementation 'org.springframework.boot:spring-boot-starter-webflux'

    // Mock Server for Testing
    testImplementation 'com.squareup.okhttp3:mockwebserver'
}
```

## 패키지 구조

```
src/main/java/dev/wony/backendlab/apitest/
├── client/              # HTTP 클라이언트 구현체
│   ├── resttemplate/
│   ├── webclient/
│   └── restclient/
├── config/              # 클라이언트 설정
├── dto/                 # 요청/응답 DTO
└── resilience/          # 장애 대응 패턴

src/test/java/dev/wony/backendlab/apitest/
├── unit/                # 단위 테스트
└── integration/         # 통합 테스트
```

## 실험 예시

### RestTemplate vs WebClient 성능 비교

```java
// RestTemplate (Blocking)
ResponseEntity<User> response = restTemplate.getForEntity(url, User.class);

// WebClient (Non-blocking)
Mono<User> userMono = webClient.get()
    .uri(url)
    .retrieve()
    .bodyToMono(User.class);
```

### 재시도 전략

```java
// Resilience4j Retry
@Retry(name = "apiRetry", fallbackMethod = "fallback")
public User getUser(Long id) {
    return restTemplate.getForObject("/users/{id}", User.class, id);
}
```

## 실행 방법

```bash
# 모듈 빌드
./gradlew :lab-api-test:build

# 테스트 실행
./gradlew :lab-api-test:test

# 애플리케이션 실행
./gradlew :lab-api-test:bootRun
```
