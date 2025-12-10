# Backend Lab Architecture

이 문서는 Backend Lab 프로젝트의 전체 아키텍처와 각 모듈의 구조를 설명합니다.

## 프로젝트 구조

```
backend-lab/
├── common/                    # 공통 유틸리티 모듈
├── api-test/                  # 외부 API 호출, REST Client 테스트
├── library-sandbox/           # 새로운 라이브러리 검증 및 PoC
├── architecture-patterns/     # 디자인 패턴, 아키텍처 패턴 구현
├── macro/                     # 웹 자동화 매크로
├── board/                     # Spring Boot 기반 게시판
├── claude-code/               # 계절별 테마 웹 애플리케이션
├── antigravity/               # 정적 웹사이트
└── spring-boot-4-features/    # Spring Boot 4 신규 기능 테스트
```

---

## Spring Boot 4 Features 모듈

### 개요

Spring Boot 4 (Spring Framework 7) 에서 도입된 새로운 기능들을 테스트하고 학습하기 위한 모듈입니다.

### 주요 기능

1. **Declarative HTTP Interface (`@HttpExchange`)** - OpenFeign 대체
2. **RestClient** - RestTemplate 대체
3. **HTTP Service Groups** - 대규모 마이크로서비스 환경 지원 (예정)

---

## HTTP Client 아키텍처

### 기존 방식 vs 신규 방식 비교

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         HTTP Client Evolution                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  [기존 - OpenFeign]                    [신규 - @HttpExchange]                │
│                                                                              │
│  ┌──────────────────┐                  ┌──────────────────┐                  │
│  │  @FeignClient    │                  │  @HttpExchange   │                  │
│  │  interface       │                  │  interface       │                  │
│  └────────┬─────────┘                  └────────┬─────────┘                  │
│           │                                     │                            │
│           ▼                                     ▼                            │
│  ┌──────────────────┐                  ┌──────────────────┐                  │
│  │  Feign Client    │                  │  RestClient /    │                  │
│  │  (외부 의존성)    │                  │  WebClient       │                  │
│  └────────┬─────────┘                  │  (Spring Native) │                  │
│           │                            └────────┬─────────┘                  │
│           ▼                                     │                            │
│  ┌──────────────────┐                           │                            │
│  │  HTTP Call       │◄──────────────────────────┘                            │
│  └──────────────────┘                                                        │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### RestTemplate 지원 중단 일정

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    RestTemplate Deprecation Timeline                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  2025-11                2026-11                2028+                         │
│     │                      │                     │                           │
│     ▼                      ▼                     ▼                           │
│  ┌─────────┐           ┌─────────┐           ┌─────────┐                    │
│  │ Spring  │           │ Spring  │           │ Spring  │                    │
│  │ FW 7.0  │    ───►   │ FW 7.1  │    ───►   │ FW 8.0  │                    │
│  │         │           │         │           │         │                    │
│  │ 지원중단 │           │@Deprecated│           │ 완전제거 │                    │
│  │ 의도공지 │           │ 표시     │           │         │                    │
│  └─────────┘           └─────────┘           └─────────┘                    │
│                                                                              │
│  OSS 지원: 2029년까지 유지                                                    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 모듈 구조

### spring-boot-4-features 모듈

```
spring-boot-4-features/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/dev/wony/backendlab/boot4/
    │   │   ├── SpringBoot4FeaturesApplication.java
    │   │   ├── httpclient/
    │   │   │   ├── config/
    │   │   │   │   ├── HttpClientConfig.java          # RestClient 설정
    │   │   │   │   └── HttpServiceGroupConfig.java    # Groups 문서 (예정)
    │   │   │   ├── dto/
    │   │   │   │   ├── Post.java
    │   │   │   │   ├── User.java
    │   │   │   │   └── Comment.java
    │   │   │   └── service/
    │   │   │       ├── PostService.java               # @HttpExchange 인터페이스
    │   │   │       └── UserService.java
    │   │   └── restclient/
    │   │       └── RestClientExample.java             # RestClient 직접 사용 예제
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/dev/wony/backendlab/boot4/
            ├── httpclient/
            │   ├── PostServiceIntegrationTest.java    # 통합 테스트
            │   └── service/
            │       ├── PostServiceTest.java           # 단위 테스트
            │       └── UserServiceTest.java
            └── restclient/
                └── RestClientExampleTest.java
```

---

## 데이터 흐름

### @HttpExchange 기반 HTTP 호출

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                           Request Flow                                        │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                               │
│  ┌─────────────┐    ┌─────────────────┐    ┌─────────────┐    ┌───────────┐  │
│  │ Application │───►│ PostService     │───►│ RestClient  │───►│ External  │  │
│  │ Code        │    │ (@HttpExchange) │    │ Adapter     │    │ API       │  │
│  └─────────────┘    └─────────────────┘    └─────────────┘    └───────────┘  │
│                              │                    │                           │
│                              │                    │                           │
│                              ▼                    ▼                           │
│                     ┌─────────────────┐    ┌─────────────┐                   │
│                     │ HttpService     │    │ HTTP Client │                   │
│                     │ ProxyFactory    │    │ (Apache     │                   │
│                     │                 │    │  HttpClient5)│                   │
│                     └─────────────────┘    └─────────────┘                   │
│                                                                               │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 설정 흐름

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                         Configuration Flow                                    │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                               │
│  1. application.yml                                                           │
│     │                                                                         │
│     ▼                                                                         │
│  2. HttpClientConfig.java                                                     │
│     │  - RestClient 생성 (baseUrl, headers, requestFactory)                   │
│     │  - HttpServiceProxyFactory 생성                                         │
│     │                                                                         │
│     ▼                                                                         │
│  3. @HttpExchange Interface Proxy 생성                                        │
│     │  - PostService 프록시 빈                                                │
│     │  - UserService 프록시 빈                                                │
│     │                                                                         │
│     ▼                                                                         │
│  4. Spring Bean으로 자동 주입                                                  │
│                                                                               │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## 핵심 코드 설명

### 1. @HttpExchange 인터페이스

```java
@HttpExchange("/posts")
public interface PostService {

    @GetExchange
    List<Post> getAllPosts();

    @GetExchange("/{id}")
    Post getPostById(@PathVariable Long id);

    @PostExchange
    Post createPost(@RequestBody Post post);

    @PutExchange("/{id}")
    Post updatePost(@PathVariable Long id, @RequestBody Post post);

    @DeleteExchange("/{id}")
    void deletePost(@PathVariable Long id);
}
```

**주요 애노테이션:**
- `@HttpExchange`: 타입 레벨에서 공통 경로 설정
- `@GetExchange`, `@PostExchange`, `@PutExchange`, `@DeleteExchange`: HTTP 메서드별 매핑
- `@PathVariable`, `@RequestParam`, `@RequestBody`: 파라미터 바인딩

### 2. RestClient 설정

```java
@Bean
public RestClient restClient() {
    return RestClient.builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .requestFactory(new HttpComponentsClientHttpRequestFactory())
            .defaultHeader("Accept", "application/json")
            .build();
}

@Bean
public HttpServiceProxyFactory httpServiceProxyFactory(RestClient restClient) {
    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    return HttpServiceProxyFactory.builderFor(adapter).build();
}

@Bean
public PostService postService(HttpServiceProxyFactory factory) {
    return factory.createClient(PostService.class);
}
```

### 3. RestClient 직접 사용

```java
// GET 요청
Post post = restClient.get()
    .uri("/posts/{id}", id)
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .body(Post.class);

// POST 요청
Post createdPost = restClient.post()
    .uri("/posts")
    .contentType(MediaType.APPLICATION_JSON)
    .body(newPost)
    .retrieve()
    .body(Post.class);

// 에러 처리
Post post = restClient.get()
    .uri("/posts/{id}", id)
    .retrieve()
    .onStatus(status -> status.value() == 404, (req, res) -> {
        throw new NotFoundException("Post not found");
    })
    .body(Post.class);
```

---

## Feign vs @HttpExchange 비교표

| 기능 | OpenFeign | @HttpExchange |
|------|-----------|---------------|
| **의존성** | spring-cloud-starter-openfeign | spring-boot-starter-web |
| **애노테이션** | @FeignClient, @GetMapping | @HttpExchange, @GetExchange |
| **HTTP 클라이언트** | Feign 전용 | RestClient / WebClient |
| **Spring 버전** | Spring Cloud 의존 | Spring Boot 4+ Native |
| **로드 밸런싱** | @LoadBalanced 필요 | Spring Cloud 2025.1+ 자동 지원 |
| **서킷 브레이커** | Resilience4j 별도 설정 | Spring Cloud 자동 통합 |
| **OAuth 지원** | 별도 설정 | @ClientRegistrationId (Spring Security 7.0) |

---

## 향후 기능 (Spring Framework 7.0.1+)

### HTTP Service Groups (@ImportHttpServices)

```java
@Configuration
@ImportHttpServices(
    group = "github",
    types = {MilestoneService.class, ReleaseService.class}
)
@ImportHttpServices(
    group = "jsonplaceholder",
    basePackages = "dev.wony.backendlab.boot4.httpclient.service"
)
public class HttpServiceGroupConfig {

    @Bean
    public RestClientHttpServiceGroupConfigurer groupConfigurer() {
        return groups -> {
            groups.filterByName("github")
                .forEachClient((_, builder) ->
                    builder.baseUrl("https://api.github.com"));

            groups.filterByName("jsonplaceholder")
                .forEachClient((_, builder) ->
                    builder.baseUrl("https://jsonplaceholder.typicode.com"));
        };
    }
}
```

---

## 참고 자료

- [HTTP Service Client Enhancements - Spring](https://spring.io/blog/2025/09/23/http-service-client-enhancements/)
- [The State of HTTP Clients in Spring](https://spring.io/blog/2025/09/30/the-state-of-http-clients-in-spring/)
- [REST Clients :: Spring Framework](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html)
- [HTTP Interface in Spring | Baeldung](https://www.baeldung.com/spring-6-http-interface)

---

*Last Updated: 2025-12-10*
