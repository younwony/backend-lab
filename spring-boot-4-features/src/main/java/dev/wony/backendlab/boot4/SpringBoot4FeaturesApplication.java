package dev.wony.backendlab.boot4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 4 신규 기능 테스트 애플리케이션
 *
 * <p>이 모듈은 Spring Boot 4 (Spring Framework 7) 에서 도입된 새로운 기능들을 테스트합니다.</p>
 *
 * <h2>주요 기능</h2>
 * <ul>
 *   <li>Declarative HTTP Interface (@HttpExchange)</li>
 *   <li>HTTP Service Groups (@ImportHttpServices)</li>
 *   <li>RestClient (RestTemplate 대체)</li>
 * </ul>
 *
 * @see <a href="https://spring.io/blog/2025/09/23/http-service-client-enhancements/">HTTP Service Client Enhancements</a>
 * @see <a href="https://spring.io/blog/2025/09/30/the-state-of-http-clients-in-spring/">The State of HTTP Clients in Spring</a>
 */
@SpringBootApplication
public class SpringBoot4FeaturesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot4FeaturesApplication.class, args);
    }
}
