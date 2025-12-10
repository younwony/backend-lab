package dev.wony.backendlab.boot4.httpclient.service;

import dev.wony.backendlab.boot4.httpclient.dto.Post;
import dev.wony.backendlab.boot4.httpclient.dto.User;
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
 * UserService HTTP Interface 단위 테스트
 */
class UserServiceTest {

    private MockWebServer mockWebServer;
    private UserService userService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        userService = factory.createClient(UserService.class);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("GET /users")
    class GetAllUsers {

        @Test
        @DisplayName("모든 사용자를 조회한다")
        void shouldReturnAllUsers() throws InterruptedException {
            // given
            String responseBody = """
                    [
                        {
                            "id": 1,
                            "name": "Leanne Graham",
                            "username": "Bret",
                            "email": "Sincere@april.biz",
                            "phone": "1-770-736-8031 x56442",
                            "website": "hildegard.org",
                            "address": {
                                "street": "Kulas Light",
                                "suite": "Apt. 556",
                                "city": "Gwenborough",
                                "zipcode": "92998-3874",
                                "geo": {"lat": "-37.3159", "lng": "81.1496"}
                            },
                            "company": {
                                "name": "Romaguera-Crona",
                                "catchPhrase": "Multi-layered client-server neural-net",
                                "bs": "harness real-time e-markets"
                            }
                        }
                    ]
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(responseBody));

            // when
            List<User> users = userService.getAllUsers();

            // then
            assertThat(users).hasSize(1);
            assertThat(users.get(0).getName()).isEqualTo("Leanne Graham");
            assertThat(users.get(0).getAddress().getCity()).isEqualTo("Gwenborough");
            assertThat(users.get(0).getCompany().getName()).isEqualTo("Romaguera-Crona");

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/users");
        }
    }

    @Nested
    @DisplayName("GET /users/{id}")
    class GetUserById {

        @Test
        @DisplayName("특정 사용자를 조회한다")
        void shouldReturnUserById() throws InterruptedException {
            // given
            String responseBody = """
                    {
                        "id": 1,
                        "name": "Leanne Graham",
                        "username": "Bret",
                        "email": "Sincere@april.biz",
                        "phone": "1-770-736-8031 x56442",
                        "website": "hildegard.org"
                    }
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(responseBody));

            // when
            User user = userService.getUserById(1L);

            // then
            assertThat(user.getId()).isEqualTo(1L);
            assertThat(user.getName()).isEqualTo("Leanne Graham");
            assertThat(user.getUsername()).isEqualTo("Bret");
            assertThat(user.getEmail()).isEqualTo("Sincere@april.biz");

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/users/1");
        }
    }

    @Nested
    @DisplayName("GET /users/{userId}/posts")
    class GetPostsByUserId {

        @Test
        @DisplayName("특정 사용자의 게시물을 조회한다")
        void shouldReturnPostsByUserId() throws InterruptedException {
            // given
            String responseBody = """
                    [
                        {"id": 1, "userId": 1, "title": "Post 1", "body": "Body 1"},
                        {"id": 2, "userId": 1, "title": "Post 2", "body": "Body 2"}
                    ]
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(responseBody));

            // when
            List<Post> posts = userService.getPostsByUserId(1L);

            // then
            assertThat(posts).hasSize(2);
            assertThat(posts).allMatch(post -> post.getUserId().equals(1L));

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/users/1/posts");
        }
    }
}
