package dev.wony.backendlab.boot4.httpclient.service;

import dev.wony.backendlab.boot4.httpclient.dto.Comment;
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
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PostService HTTP Interface 단위 테스트
 *
 * <p>MockWebServer를 사용하여 실제 HTTP 호출 없이 테스트합니다.</p>
 */
class PostServiceTest {

    private MockWebServer mockWebServer;
    private PostService postService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        postService = factory.createClient(PostService.class);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("GET /posts")
    class GetAllPosts {

        @Test
        @DisplayName("모든 게시물을 조회한다")
        void shouldReturnAllPosts() throws InterruptedException {
            // given
            String responseBody = """
                    [
                        {"id": 1, "userId": 1, "title": "Title 1", "body": "Body 1"},
                        {"id": 2, "userId": 1, "title": "Title 2", "body": "Body 2"}
                    ]
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(responseBody));

            // when
            List<Post> posts = postService.getAllPosts();

            // then
            assertThat(posts).hasSize(2);
            assertThat(posts.get(0).getId()).isEqualTo(1L);
            assertThat(posts.get(0).getTitle()).isEqualTo("Title 1");

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/posts");
        }
    }

    @Nested
    @DisplayName("GET /posts/{id}")
    class GetPostById {

        @Test
        @DisplayName("특정 게시물을 조회한다")
        void shouldReturnPostById() throws InterruptedException {
            // given
            String responseBody = """
                    {"id": 1, "userId": 1, "title": "Test Title", "body": "Test Body"}
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(responseBody));

            // when
            Post post = postService.getPostById(1L);

            // then
            assertThat(post.getId()).isEqualTo(1L);
            assertThat(post.getUserId()).isEqualTo(1L);
            assertThat(post.getTitle()).isEqualTo("Test Title");
            assertThat(post.getBody()).isEqualTo("Test Body");

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/posts/1");
        }
    }

    @Nested
    @DisplayName("GET /posts?userId={userId}")
    class GetPostsByUserId {

        @Test
        @DisplayName("특정 사용자의 게시물을 조회한다")
        void shouldReturnPostsByUserId() throws InterruptedException {
            // given
            String responseBody = """
                    [
                        {"id": 1, "userId": 1, "title": "User 1 Post 1", "body": "Body 1"},
                        {"id": 2, "userId": 1, "title": "User 1 Post 2", "body": "Body 2"}
                    ]
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(responseBody));

            // when
            List<Post> posts = postService.getPostsByUserId(1L);

            // then
            assertThat(posts).hasSize(2);
            assertThat(posts).allMatch(post -> post.getUserId().equals(1L));

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/posts?userId=1");
        }
    }

    @Nested
    @DisplayName("GET /posts/{postId}/comments")
    class GetCommentsByPostId {

        @Test
        @DisplayName("특정 게시물의 댓글을 조회한다")
        void shouldReturnCommentsByPostId() throws InterruptedException {
            // given
            String responseBody = """
                    [
                        {"id": 1, "postId": 1, "name": "Commenter 1", "email": "test1@test.com", "body": "Comment 1"},
                        {"id": 2, "postId": 1, "name": "Commenter 2", "email": "test2@test.com", "body": "Comment 2"}
                    ]
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(responseBody));

            // when
            List<Comment> comments = postService.getCommentsByPostId(1L);

            // then
            assertThat(comments).hasSize(2);
            assertThat(comments).allMatch(comment -> comment.getPostId().equals(1L));

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/posts/1/comments");
        }
    }

    @Nested
    @DisplayName("POST /posts")
    class CreatePost {

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
            Post createdPost = postService.createPost(newPost);

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
    @DisplayName("PUT /posts/{id}")
    class UpdatePost {

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
            Post updatedPost = postService.updatePost(1L, updatePost);

            // then
            assertThat(updatedPost.getTitle()).isEqualTo("Updated Title");
            assertThat(updatedPost.getBody()).isEqualTo("Updated Body");

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("PUT");
            assertThat(request.getPath()).isEqualTo("/posts/1");
        }
    }

    @Nested
    @DisplayName("DELETE /posts/{id}")
    class DeletePost {

        @Test
        @DisplayName("게시물을 삭제한다")
        void shouldDeletePost() throws InterruptedException {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200));

            // when
            postService.deletePost(1L);

            // then
            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("DELETE");
            assertThat(request.getPath()).isEqualTo("/posts/1");
        }
    }
}
