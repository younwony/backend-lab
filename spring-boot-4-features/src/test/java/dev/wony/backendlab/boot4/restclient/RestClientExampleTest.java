package dev.wony.backendlab.boot4.restclient;

import dev.wony.backendlab.boot4.httpclient.dto.Post;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * RestClient 사용 예제 테스트
 *
 * <p>RestClient의 다양한 기능을 테스트합니다.</p>
 */
class RestClientExampleTest {

    private MockWebServer mockWebServer;
    private RestClientExample restClientExample;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        restClientExample = new RestClientExample(restClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("GET 요청")
    class GetRequests {

        @Test
        @DisplayName("단일 게시물을 조회한다")
        void shouldGetPost() throws InterruptedException {
            // given
            String responseBody = """
                    {"id": 1, "userId": 1, "title": "Test Title", "body": "Test Body"}
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(responseBody));

            // when
            Post post = restClientExample.getPost(1L);

            // then
            assertThat(post.getId()).isEqualTo(1L);
            assertThat(post.getTitle()).isEqualTo("Test Title");

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/posts/1");
        }

        @Test
        @DisplayName("ResponseEntity로 게시물을 조회한다")
        void shouldGetPostWithEntity() throws InterruptedException {
            // given
            String responseBody = """
                    {"id": 1, "userId": 1, "title": "Test Title", "body": "Test Body"}
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setHeader("X-Custom-Header", "custom-value")
                    .setBody(responseBody));

            // when
            ResponseEntity<Post> response = restClientExample.getPostWithEntity(1L);

            // then
            assertThat(response.getStatusCode().value()).isEqualTo(200);
            assertThat(response.getHeaders().getFirst("X-Custom-Header")).isEqualTo("custom-value");
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Test Title");
        }

        @Test
        @DisplayName("모든 게시물을 조회한다")
        void shouldGetAllPosts() {
            // given
            String responseBody = """
                    [
                        {"id": 1, "userId": 1, "title": "Title 1", "body": "Body 1"},
                        {"id": 2, "userId": 1, "title": "Title 2", "body": "Body 2"},
                        {"id": 3, "userId": 2, "title": "Title 3", "body": "Body 3"}
                    ]
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(responseBody));

            // when
            List<Post> posts = restClientExample.getAllPosts();

            // then
            assertThat(posts).hasSize(3);
            assertThat(posts).extracting(Post::getTitle)
                    .containsExactly("Title 1", "Title 2", "Title 3");
        }
    }

    @Nested
    @DisplayName("POST 요청")
    class PostRequests {

        @Test
        @DisplayName("새 게시물을 생성한다")
        void shouldCreatePost() throws InterruptedException {
            // given
            String responseBody = """
                    {"id": 101, "userId": 1, "title": "New Post", "body": "New Body"}
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(201)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(responseBody));

            Post newPost = Post.builder()
                    .userId(1L)
                    .title("New Post")
                    .body("New Body")
                    .build();

            // when
            Post createdPost = restClientExample.createPost(newPost);

            // then
            assertThat(createdPost.getId()).isEqualTo(101L);
            assertThat(createdPost.getTitle()).isEqualTo("New Post");

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getPath()).isEqualTo("/posts");
            assertThat(request.getHeader(HttpHeaders.CONTENT_TYPE))
                    .contains(MediaType.APPLICATION_JSON_VALUE);
        }
    }

    @Nested
    @DisplayName("PUT 요청")
    class PutRequests {

        @Test
        @DisplayName("게시물을 수정한다")
        void shouldUpdatePost() throws InterruptedException {
            // given
            String responseBody = """
                    {"id": 1, "userId": 1, "title": "Updated Title", "body": "Updated Body"}
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(responseBody));

            Post updatePost = Post.builder()
                    .id(1L)
                    .userId(1L)
                    .title("Updated Title")
                    .body("Updated Body")
                    .build();

            // when
            Post updatedPost = restClientExample.updatePost(1L, updatePost);

            // then
            assertThat(updatedPost.getTitle()).isEqualTo("Updated Title");

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("PUT");
            assertThat(request.getPath()).isEqualTo("/posts/1");
        }
    }

    @Nested
    @DisplayName("DELETE 요청")
    class DeleteRequests {

        @Test
        @DisplayName("게시물을 삭제한다")
        void shouldDeletePost() throws InterruptedException {
            // given
            mockWebServer.enqueue(new MockResponse().setResponseCode(200));

            // when
            restClientExample.deletePost(1L);

            // then
            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("DELETE");
            assertThat(request.getPath()).isEqualTo("/posts/1");
        }
    }

    @Nested
    @DisplayName("에러 처리")
    class ErrorHandling {

        @Test
        @DisplayName("404 에러 시 예외를 던진다")
        void shouldThrowExceptionOn404() {
            // given
            mockWebServer.enqueue(new MockResponse().setResponseCode(404));

            // when & then
            assertThatThrownBy(() -> restClientExample.getPostWithErrorHandling(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Post not found");
        }

        @Test
        @DisplayName("500 에러 시 예외를 던진다")
        void shouldThrowExceptionOn500() {
            // given
            mockWebServer.enqueue(new MockResponse().setResponseCode(500));

            // when & then
            assertThatThrownBy(() -> restClientExample.getPostWithErrorHandling(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Server error");
        }
    }

    @Nested
    @DisplayName("Exchange 메서드")
    class ExchangeMethod {

        @Test
        @DisplayName("정상 응답 시 게시물을 반환한다")
        void shouldReturnPostOnSuccess() {
            // given
            String responseBody = """
                    {"id": 1, "userId": 1, "title": "Test Title", "body": "Test Body"}
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(responseBody));

            // when
            Post post = restClientExample.getPostWithExchange(1L);

            // then
            assertThat(post).isNotNull();
            assertThat(post.getTitle()).isEqualTo("Test Title");
        }

        @Test
        @DisplayName("4xx 에러 시 null을 반환한다")
        void shouldReturnNullOn4xxError() {
            // given
            mockWebServer.enqueue(new MockResponse().setResponseCode(404));

            // when
            Post post = restClientExample.getPostWithExchange(999L);

            // then
            assertThat(post).isNull();
        }

        @Test
        @DisplayName("5xx 에러 시 예외를 던진다")
        void shouldThrowExceptionOn5xxError() {
            // given
            mockWebServer.enqueue(new MockResponse().setResponseCode(500));

            // when & then
            assertThatThrownBy(() -> restClientExample.getPostWithExchange(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Server error");
        }
    }
}
