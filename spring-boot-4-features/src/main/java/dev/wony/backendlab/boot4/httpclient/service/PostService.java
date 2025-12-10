package dev.wony.backendlab.boot4.httpclient.service;

import dev.wony.backendlab.boot4.httpclient.dto.Comment;
import dev.wony.backendlab.boot4.httpclient.dto.Post;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;

/**
 * JSONPlaceholder Posts API를 위한 Declarative HTTP Interface
 *
 * <p>Spring Boot 4 / Spring Framework 7에서 도입된 {@code @HttpExchange} 애노테이션을 사용하여
 * Feign과 유사한 선언적 HTTP 클라이언트를 정의합니다.</p>
 *
 * <h2>주요 특징</h2>
 * <ul>
 *   <li>인터페이스 기반의 선언적 API 정의</li>
 *   <li>Spring의 RestClient/WebClient와 네이티브 통합</li>
 *   <li>타입 안전한 HTTP 요청/응답 처리</li>
 *   <li>외부 라이브러리 의존성 없음 (OpenFeign 불필요)</li>
 * </ul>
 *
 * <h2>Feign vs @HttpExchange 비교</h2>
 * <table border="1">
 *   <tr><th>기능</th><th>Feign</th><th>@HttpExchange</th></tr>
 *   <tr><td>의존성</td><td>spring-cloud-starter-openfeign</td><td>spring-boot-starter-web</td></tr>
 *   <tr><td>애노테이션</td><td>@FeignClient, @GetMapping</td><td>@HttpExchange, @GetExchange</td></tr>
 *   <tr><td>클라이언트</td><td>Feign 전용</td><td>RestClient / WebClient</td></tr>
 *   <tr><td>Spring 버전</td><td>Spring Cloud 의존</td><td>Spring Boot 4+ 네이티브</td></tr>
 * </table>
 *
 * @see <a href="https://docs.spring.io/spring-framework/reference/integration/rest-clients.html">Spring REST Clients</a>
 */
@HttpExchange("/posts")
public interface PostService {

    /**
     * 모든 게시물 조회
     *
     * @return 게시물 목록
     */
    @GetExchange
    List<Post> getAllPosts();

    /**
     * 특정 게시물 조회
     *
     * @param id 게시물 ID
     * @return 게시물 정보
     */
    @GetExchange("/{id}")
    Post getPostById(@PathVariable Long id);

    /**
     * 특정 사용자의 게시물 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 게시물 목록
     */
    @GetExchange
    List<Post> getPostsByUserId(@RequestParam Long userId);

    /**
     * 특정 게시물의 댓글 조회
     *
     * @param postId 게시물 ID
     * @return 댓글 목록
     */
    @GetExchange("/{postId}/comments")
    List<Comment> getCommentsByPostId(@PathVariable Long postId);

    /**
     * 새 게시물 생성
     *
     * @param post 생성할 게시물 정보
     * @return 생성된 게시물 (ID 포함)
     */
    @PostExchange
    Post createPost(@RequestBody Post post);

    /**
     * 게시물 전체 수정
     *
     * @param id   수정할 게시물 ID
     * @param post 수정할 내용
     * @return 수정된 게시물
     */
    @PutExchange("/{id}")
    Post updatePost(@PathVariable Long id, @RequestBody Post post);

    /**
     * 게시물 삭제
     *
     * @param id 삭제할 게시물 ID
     */
    @DeleteExchange("/{id}")
    void deletePost(@PathVariable Long id);
}
