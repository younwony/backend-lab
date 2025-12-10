package dev.wony.backendlab.boot4.httpclient.config;

import dev.wony.backendlab.boot4.httpclient.service.PostService;
import dev.wony.backendlab.boot4.httpclient.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * HTTP Interface Client 설정
 *
 * <p>Spring Boot 4에서 {@code @HttpExchange} 인터페이스를 사용하기 위한 설정입니다.</p>
 *
 * <h2>기존 방식 (수동 설정)</h2>
 * <p>이 설정 클래스는 {@code HttpServiceProxyFactory}를 사용하여 HTTP 인터페이스의 프록시를 수동으로 생성합니다.
 * 이는 기존 방식이지만, Spring Boot 4에서는 {@code @ImportHttpServices}를 통한 자동 등록도 지원합니다.</p>
 *
 * <h2>RestClient vs WebClient</h2>
 * <ul>
 *   <li><strong>RestClient</strong>: 동기 방식, 새로운 프로젝트에 권장</li>
 *   <li><strong>WebClient</strong>: 비동기/반응형 방식, 고동시성 필요 시</li>
 * </ul>
 *
 * @see HttpServiceGroupConfig 자동 등록 방식 (@ImportHttpServices)
 */
@Configuration
public class HttpClientConfig {

    @Value("${http.client.jsonplaceholder.base-url:https://jsonplaceholder.typicode.com}")
    private String baseUrl;

    /**
     * RestClient 빈 생성
     *
     * <p>RestTemplate을 대체하는 새로운 동기 HTTP 클라이언트입니다.</p>
     *
     * <h3>주요 특징</h3>
     * <ul>
     *   <li>Fluent API로 직관적인 사용</li>
     *   <li>RestTemplate 대비 확장성 향상</li>
     *   <li>Virtual Threads 지원</li>
     * </ul>
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                // Apache HttpClient5 사용 (커넥션 풀, 타임아웃 등 세부 설정 가능)
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * HttpServiceProxyFactory 빈 생성
     *
     * <p>{@code @HttpExchange} 인터페이스의 프록시를 생성하는 팩토리입니다.</p>
     */
    @Bean
    public HttpServiceProxyFactory httpServiceProxyFactory(RestClient restClient) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    /**
     * PostService 프록시 빈 생성
     *
     * <p>{@code @HttpExchange} 인터페이스를 구현하는 프록시 객체를 생성합니다.
     * 실제 HTTP 호출은 RestClient를 통해 이루어집니다.</p>
     */
    @Bean
    public PostService postService(HttpServiceProxyFactory factory) {
        return factory.createClient(PostService.class);
    }

    /**
     * UserService 프록시 빈 생성
     */
    @Bean
    public UserService userService(HttpServiceProxyFactory factory) {
        return factory.createClient(UserService.class);
    }
}
