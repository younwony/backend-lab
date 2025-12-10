package dev.wony.backendlab.boot4.httpclient.config;

/**
 * HTTP Service Groups 설정 (Spring Framework 7.0.1+ / Spring Boot 4.0.1+ 예정)
 *
 * <p><strong>참고:</strong> 이 기능은 Spring Framework 7.0.1 이상에서 사용 가능합니다.
 * 현재 Spring Boot 4.0.0에서는 아직 {@code @ImportHttpServices} 애노테이션이 포함되지 않았습니다.
 * 향후 버전에서 사용 가능해지면 아래 코드를 활성화할 수 있습니다.</p>
 *
 * <h2>HTTP Service Groups 개념</h2>
 * <p>HTTP Service Group은 동일한 클라이언트 설정과 {@code HttpServiceProxyFactory} 인스턴스를
 * 공유하는 인터페이스 집합입니다. 일반적으로 하나의 호스트당 하나의 그룹을 사용합니다.</p>
 *
 * <h2>@ImportHttpServices 방식의 장점</h2>
 * <ul>
 *   <li>복잡한 프록시 팩토리 설정을 한 줄로 대체</li>
 *   <li>선언적이고 유지보수하기 쉬운 REST 클라이언트</li>
 *   <li>Spring Cloud (로드 밸런싱, 서킷 브레이커) 투명한 지원</li>
 *   <li>Spring Security OAuth 지원 (@ClientRegistrationId)</li>
 * </ul>
 *
 * <h2>Feign과의 비교</h2>
 * <pre>
 * // Feign (기존)
 * {@literal @}FeignClient(name = "jsonplaceholder", url = "https://jsonplaceholder.typicode.com")
 * public interface PostClient {
 *     {@literal @}GetMapping("/posts")
 *     List&lt;Post&gt; getAllPosts();
 * }
 *
 * // Spring Boot 4.0.1+ (@ImportHttpServices 예정)
 * {@literal @}ImportHttpServices(group = "jsonplaceholder", types = {PostService.class})
 * {@literal @}Configuration
 * public class HttpServiceGroupConfig { }
 * </pre>
 *
 * <h2>향후 사용 예시 (Spring Framework 7.0.1+)</h2>
 * <pre>
 * {@literal @}Configuration
 * {@literal @}Profile("http-service-groups")
 * {@literal @}ImportHttpServices(
 *         group = "jsonplaceholder",
 *         basePackages = "dev.wony.backendlab.boot4.httpclient.service"
 * )
 * public class HttpServiceGroupConfig {
 *
 *     {@literal @}Bean
 *     public RestClientHttpServiceGroupConfigurer httpServiceGroupConfigurer() {
 *         return groups -&gt; groups
 *                 .filterByName("jsonplaceholder")
 *                 .forEachClient((group, builder) -&gt; builder
 *                         .baseUrl("https://jsonplaceholder.typicode.com")
 *                         .requestFactory(new HttpComponentsClientHttpRequestFactory())
 *                         .defaultHeader("Accept", "application/json")
 *                 );
 *     }
 * }
 * </pre>
 *
 * @see HttpClientConfig 현재 사용 가능한 수동 설정 방식
 * @see <a href="https://spring.io/blog/2025/09/23/http-service-client-enhancements/">HTTP Service Client Enhancements</a>
 */
public final class HttpServiceGroupConfig {

    private HttpServiceGroupConfig() {
        // 문서용 클래스 - 인스턴스 생성 불가
    }
}
