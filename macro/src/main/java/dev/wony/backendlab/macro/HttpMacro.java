package dev.wony.backendlab.macro;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * HTTP 요청 기반 간단한 매크로
 */
@Slf4j
public class HttpMacro {

    private static final String INVALID_URL_MESSAGE = "URL은 필수입니다.";
    private static final String INVALID_INTERVAL_MESSAGE = "인터벌은 1초 이상이어야 합니다.";
    private static final String INVALID_COUNT_MESSAGE = "반복 횟수는 1회 이상이어야 합니다.";

    private final HttpClientFactory clientFactory;

    /**
     * 기본 생성자
     */
    public HttpMacro() {
        this(HttpClients::createDefault);
    }

    /**
     * 테스트용 생성자 - HttpClient 팩토리 주입
     *
     * @param clientFactory HttpClient 생성 팩토리
     */
    public HttpMacro(HttpClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    /**
     * 지정된 URL에 반복적으로 HTTP GET 요청을 수행합니다.
     *
     * @param url             요청할 URL
     * @param intervalSeconds 요청 간 대기 시간(초)
     * @param repeatCount     반복 횟수
     */
    public void execute(String url, int intervalSeconds, int repeatCount) {
        checkArgument(isNotBlank(url), INVALID_URL_MESSAGE);
        checkArgument(intervalSeconds >= 1, INVALID_INTERVAL_MESSAGE);
        checkArgument(repeatCount >= 1, INVALID_COUNT_MESSAGE);

        log.info("매크로 실행 시작 - URL: {}, 인터벌: {}초, 반복: {}회", url, intervalSeconds, repeatCount);

        for (int i = 1; i <= repeatCount; i++) {
            executeRequest(url, i, repeatCount);

            if (i < repeatCount) {
                sleep(intervalSeconds);
            }
        }

        log.info("매크로 실행 완료");
    }

    private void executeRequest(String url, int currentAttempt, int totalAttempts) {
        log.info("[{}/{}] 요청 시작: {}", currentAttempt, totalAttempts, url);

        try (CloseableHttpClient client = clientFactory.create()) {
            HttpGet request = new HttpGet(url);

            int statusCode = client.execute(request, this::handleResponse);
            log.info("[{}/{}] 응답 상태 코드: {}", currentAttempt, totalAttempts, statusCode);

        } catch (IOException e) {
            log.error("[{}/{}] 요청 실패: {}", currentAttempt, totalAttempts, e.getMessage());
        }
    }

    private int handleResponse(ClassicHttpResponse response) throws IOException {
        int statusCode = response.getCode();
        EntityUtils.consume(response.getEntity());
        return statusCode;
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("대기 중 인터럽트 발생");
        }
    }

    /**
     * HttpClient 생성 팩토리 인터페이스 (테스트용)
     */
    @FunctionalInterface
    public interface HttpClientFactory {
        CloseableHttpClient create();
    }
}
