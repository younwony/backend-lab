package dev.wony.backendlab.boot4.httpclient;

import dev.wony.backendlab.boot4.SpringBoot4FeaturesApplication;
import dev.wony.backendlab.boot4.httpclient.dto.Comment;
import dev.wony.backendlab.boot4.httpclient.dto.Post;
import dev.wony.backendlab.boot4.httpclient.dto.User;
import dev.wony.backendlab.boot4.httpclient.service.PostService;
import dev.wony.backendlab.boot4.httpclient.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HTTP Interface 통합 테스트
 *
 * <p>실제 JSONPlaceholder API를 호출하여 테스트합니다.
 * 이 테스트는 네트워크 연결이 필요하며, 외부 API 상태에 따라 실패할 수 있습니다.</p>
 *
 * <p>실행 방법: {@code ./gradlew :spring-boot-4-features:test --tests "*IntegrationTest" -Dtest.tags="integration"}</p>
 */
@Tag("integration")
@SpringBootTest(classes = SpringBoot4FeaturesApplication.class)
class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Nested
    @DisplayName("PostService 통합 테스트")
    class PostServiceTests {

        @Test
        @DisplayName("모든 게시물을 조회한다")
        void shouldGetAllPosts() {
            // when
            List<Post> posts = postService.getAllPosts();

            // then
            assertThat(posts).isNotEmpty();
            assertThat(posts.size()).isGreaterThanOrEqualTo(100);
            assertThat(posts.get(0).getId()).isNotNull();
            assertThat(posts.get(0).getTitle()).isNotBlank();
        }

        @Test
        @DisplayName("특정 게시물을 조회한다")
        void shouldGetPostById() {
            // when
            Post post = postService.getPostById(1L);

            // then
            assertThat(post.getId()).isEqualTo(1L);
            assertThat(post.getUserId()).isEqualTo(1L);
            assertThat(post.getTitle()).isNotBlank();
            assertThat(post.getBody()).isNotBlank();
        }

        @Test
        @DisplayName("특정 사용자의 게시물을 조회한다")
        void shouldGetPostsByUserId() {
            // when
            List<Post> posts = postService.getPostsByUserId(1L);

            // then
            assertThat(posts).isNotEmpty();
            assertThat(posts).allMatch(post -> post.getUserId().equals(1L));
        }

        @Test
        @DisplayName("특정 게시물의 댓글을 조회한다")
        void shouldGetCommentsByPostId() {
            // when
            List<Comment> comments = postService.getCommentsByPostId(1L);

            // then
            assertThat(comments).isNotEmpty();
            assertThat(comments).allMatch(comment -> comment.getPostId().equals(1L));
            assertThat(comments.get(0).getEmail()).isNotBlank();
        }

        @Test
        @DisplayName("새 게시물을 생성한다 (Fake API)")
        void shouldCreatePost() {
            // given
            Post newPost = Post.builder()
                    .userId(1L)
                    .title("Spring Boot 4 HTTP Interface Test")
                    .body("Testing @HttpExchange annotation")
                    .build();

            // when
            Post createdPost = postService.createPost(newPost);

            // then
            assertThat(createdPost.getId()).isNotNull();
            assertThat(createdPost.getTitle()).isEqualTo(newPost.getTitle());
            assertThat(createdPost.getBody()).isEqualTo(newPost.getBody());
        }

        @Test
        @DisplayName("게시물을 수정한다 (Fake API)")
        void shouldUpdatePost() {
            // given
            Post updatePost = Post.builder()
                    .id(1L)
                    .userId(1L)
                    .title("Updated Title")
                    .body("Updated Body")
                    .build();

            // when
            Post updatedPost = postService.updatePost(1L, updatePost);

            // then
            assertThat(updatedPost.getId()).isEqualTo(1L);
            assertThat(updatedPost.getTitle()).isEqualTo("Updated Title");
        }

        @Test
        @DisplayName("게시물을 삭제한다 (Fake API)")
        void shouldDeletePost() {
            // when & then (no exception)
            postService.deletePost(1L);
        }
    }

    @Nested
    @DisplayName("UserService 통합 테스트")
    class UserServiceTests {

        @Test
        @DisplayName("모든 사용자를 조회한다")
        void shouldGetAllUsers() {
            // when
            List<User> users = userService.getAllUsers();

            // then
            assertThat(users).isNotEmpty();
            assertThat(users.size()).isEqualTo(10);
            assertThat(users.get(0).getName()).isNotBlank();
        }

        @Test
        @DisplayName("특정 사용자를 조회한다")
        void shouldGetUserById() {
            // when
            User user = userService.getUserById(1L);

            // then
            assertThat(user.getId()).isEqualTo(1L);
            assertThat(user.getName()).isEqualTo("Leanne Graham");
            assertThat(user.getUsername()).isEqualTo("Bret");
            assertThat(user.getEmail()).isNotBlank();
        }

        @Test
        @DisplayName("특정 사용자의 게시물을 조회한다")
        void shouldGetPostsByUserId() {
            // when
            List<Post> posts = userService.getPostsByUserId(1L);

            // then
            assertThat(posts).isNotEmpty();
            assertThat(posts).allMatch(post -> post.getUserId().equals(1L));
        }
    }
}
