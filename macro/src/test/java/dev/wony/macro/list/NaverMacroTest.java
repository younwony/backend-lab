package dev.wony.macro.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * NaverMacro 클래스의 단위 테스트
 * <p>
 * Mock WebDriver를 사용하여 실제 브라우저 없이 테스트합니다.
 * </p>
 */
@DisplayName("NaverMacro 테스트")
@ExtendWith(MockitoExtension.class)
class NaverMacroTest {

    private static final String TEST_ID = "testId";
    private static final String TEST_PW = "testPw";
    private static final String TEST_URL = "https://example.com/product";

    @Mock
    private WebDriver mockDriver;

    @Mock
    private WebElement mockElement;

    private NaverMacro naverMacro;

    @BeforeEach
    void setUp() {
        naverMacro = new NaverMacro(TEST_ID, TEST_PW, TEST_URL, mockDriver);
    }

    @Test
    @DisplayName("외부 WebDriver를 주입하여 NaverMacro 객체가 정상적으로 생성된다")
    void constructor_WithInjectedDriver_CreatesInstance() {
        // Given & When
        NaverMacro macro = new NaverMacro(TEST_ID, TEST_PW, TEST_URL, mockDriver);

        // Then
        assertNotNull(macro);
    }

    @Test
    @DisplayName("close 호출 시 WebDriver의 quit 메서드가 호출된다")
    void close_WhenCalled_QuitsDriver() {
        // Given & When
        naverMacro.close();

        // Then
        verify(mockDriver, times(1)).quit();
    }
}
