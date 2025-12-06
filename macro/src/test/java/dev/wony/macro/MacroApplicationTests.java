package dev.wony.macro;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * MacroApplication 테스트
 */
@DisplayName("MacroApplication 테스트")
class MacroApplicationTests {

    @Test
    @DisplayName("MacroApplication 클래스가 정상적으로 로드된다")
    void classLoads() {
        // Given & When & Then
        assertDoesNotThrow(() -> Class.forName("dev.wony.macro.MacroApplication"));
    }
}
