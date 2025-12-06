package dev.wony.backendlab.macro;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * HttpMacro 테스트
 */
@DisplayName("HttpMacro 테스트")
class HttpMacroTest {

    @Mock
    private CloseableHttpClient mockClient;

    private HttpMacro httpMacro;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        httpMacro = new HttpMacro(() -> mockClient);
    }

    @Test
    @DisplayName("유효한 URL과 인자로 execute 실행 시 성공한다")
    void execute_WithValidArgs_Succeeds() throws IOException {
        // Given
        String url = "https://example.com";
        when(mockClient.execute(any(ClassicHttpRequest.class), any(HttpClientResponseHandler.class)))
                .thenReturn(200);

        // When & Then
        assertDoesNotThrow(() -> httpMacro.execute(url, 1, 1));
        verify(mockClient, times(1)).execute(any(ClassicHttpRequest.class), any(HttpClientResponseHandler.class));
    }

    @Test
    @DisplayName("URL이 null이면 IllegalArgumentException이 발생한다")
    void execute_WithNullUrl_ThrowsException() {
        // Given & When & Then
        assertThrows(IllegalArgumentException.class,
                () -> httpMacro.execute(null, 1, 1));
    }

    @Test
    @DisplayName("URL이 빈 문자열이면 IllegalArgumentException이 발생한다")
    void execute_WithEmptyUrl_ThrowsException() {
        // Given & When & Then
        assertThrows(IllegalArgumentException.class,
                () -> httpMacro.execute("", 1, 1));
    }

    @Test
    @DisplayName("인터벌이 0이면 IllegalArgumentException이 발생한다")
    void execute_WithZeroInterval_ThrowsException() {
        // Given & When & Then
        assertThrows(IllegalArgumentException.class,
                () -> httpMacro.execute("https://example.com", 0, 1));
    }

    @Test
    @DisplayName("반복 횟수가 0이면 IllegalArgumentException이 발생한다")
    void execute_WithZeroRepeatCount_ThrowsException() {
        // Given & When & Then
        assertThrows(IllegalArgumentException.class,
                () -> httpMacro.execute("https://example.com", 1, 0));
    }

    @Test
    @DisplayName("HTTP 요청 실패 시 예외 없이 계속 진행한다")
    void execute_WhenRequestFails_ContinuesGracefully() throws IOException {
        // Given
        String url = "https://example.com";
        when(mockClient.execute(any(ClassicHttpRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(new IOException("Connection failed"));

        // When & Then
        assertDoesNotThrow(() -> httpMacro.execute(url, 1, 2));
        verify(mockClient, times(2)).execute(any(ClassicHttpRequest.class), any(HttpClientResponseHandler.class));
    }

    @Test
    @DisplayName("반복 횟수만큼 HTTP 요청을 수행한다")
    void execute_WithRepeatCount_ExecutesMultipleTimes() throws IOException {
        // Given
        String url = "https://example.com";
        int repeatCount = 3;
        when(mockClient.execute(any(ClassicHttpRequest.class), any(HttpClientResponseHandler.class)))
                .thenReturn(200);

        // When
        httpMacro.execute(url, 1, repeatCount);

        // Then
        verify(mockClient, times(repeatCount)).execute(any(ClassicHttpRequest.class), any(HttpClientResponseHandler.class));
    }
}
