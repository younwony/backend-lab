package dev.wony.backendlab.boot4.restclient;

import dev.wony.backendlab.boot4.httpclient.dto.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * RestClient 사용 예제
 *
 * <p>Spring Framework 6.1에서 도입된 RestClient는 RestTemplate을 대체하는 새로운 동기 HTTP 클라이언트입니다.
 * Spring Framework 7.0 (Spring Boot 4)에서는 RestTemplate이 공식적으로 지원 중단될 예정입니다.</p>
 *
 * <h2>RestTemplate 지원 중단 일정</h2>
 * <ul>
 *   <li>Spring Framework 7.0 (2025년 11월): 지원 중단 의도 공지</li>
 *   <li>Spring Framework 7.1 (2026년 11월 예정): {@code @Deprecated} 표시</li>
 *   <li>Spring Framework 8.0: 완전 제거</li>
 * </ul>
 *
 * <h2>RestClient vs RestTemplate 비교</h2>
 * <pre>
 * // RestTemplate (기존)
 * String result = restTemplate.getForObject(url, String.class);
 * ResponseEntity&lt;String&gt; entity = restTemplate.getForEntity(url, String.class);
 *
 * // RestClient (신규)
 * String result = restClient.get().uri(url).retrieve().body(String.class);
 * ResponseEntity&lt;String&gt; entity = restClient.get().uri(url).retrieve().toEntity(String.class);
 * </pre>
 *
 * <h2>주요 특징</h2>
 * <ul>
 *   <li>Fluent API: 체이닝 방식의 직관적인 API</li>
 *   <li>Virtual Threads 지원: 높은 동시성 처리</li>
 *   <li>API 버전 관리 지원 (Spring 7.0)</li>
 *   <li>RestTemplate에서 쉬운 마이그레이션</li>
 * </ul>
 *
 * @see <a href="https://spring.io/blog/2025/09/30/the-state-of-http-clients-in-spring/">The State of HTTP Clients in Spring</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestClientExample {

    private final RestClient restClient;

    /**
     * GET 요청 - 단일 객체 조회
     *
     * <p>RestClient의 fluent API를 사용한 기본적인 GET 요청입니다.</p>
     */
    public Post getPost(Long id) {
        return restClient.get()
                .uri("/posts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Post.class);
    }

    /**
     * GET 요청 - ResponseEntity로 받기
     *
     * <p>상태 코드, 헤더 등 전체 응답 정보가 필요할 때 사용합니다.</p>
     */
    public ResponseEntity<Post> getPostWithEntity(Long id) {
        return restClient.get()
                .uri("/posts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Post.class);
    }

    /**
     * GET 요청 - 목록 조회
     *
     * <p>ParameterizedTypeReference를 사용하여 제네릭 타입을 처리합니다.</p>
     */
    public List<Post> getAllPosts() {
        return restClient.get()
                .uri("/posts")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<>() {});
    }

    /**
     * POST 요청 - 객체 생성
     */
    public Post createPost(Post post) {
        return restClient.post()
                .uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(post)
                .retrieve()
                .body(Post.class);
    }

    /**
     * PUT 요청 - 객체 수정
     */
    public Post updatePost(Long id, Post post) {
        return restClient.put()
                .uri("/posts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(post)
                .retrieve()
                .body(Post.class);
    }

    /**
     * DELETE 요청 - 객체 삭제
     */
    public void deletePost(Long id) {
        restClient.delete()
                .uri("/posts/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * 에러 처리 예제
     *
     * <p>{@code onStatus}를 사용하여 HTTP 상태 코드별 에러 처리를 커스터마이징할 수 있습니다.</p>
     */
    public Post getPostWithErrorHandling(Long id) {
        return restClient.get()
                .uri("/posts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    log.warn("Post not found: {}", id);
                    throw new RuntimeException("Post not found: " + id);
                })
                .onStatus(status -> status.is4xxClientError(), (request, response) -> {
                    log.error("Client error: {}", response.getStatusCode());
                    throw new RuntimeException("Client error: " + response.getStatusCode());
                })
                .onStatus(status -> status.is5xxServerError(), (request, response) -> {
                    log.error("Server error: {}", response.getStatusCode());
                    throw new RuntimeException("Server error: " + response.getStatusCode());
                })
                .body(Post.class);
    }

    /**
     * exchange() 메서드를 사용한 저수준 접근
     *
     * <p>응답을 더 세밀하게 제어해야 할 때 {@code exchange}를 사용합니다.</p>
     */
    public Post getPostWithExchange(Long id) {
        return restClient.get()
                .uri("/posts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    if (response.getStatusCode().is4xxClientError()) {
                        log.warn("Client error for post {}: {}", id, response.getStatusCode());
                        return null;
                    }
                    if (response.getStatusCode().is5xxServerError()) {
                        throw new RuntimeException("Server error: " + response.getStatusCode());
                    }
                    return response.bodyTo(Post.class);
                });
    }
}
