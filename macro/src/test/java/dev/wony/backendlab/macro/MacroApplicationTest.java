package dev.wony.backendlab.macro;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MacroApplication 테스트
 */
@DisplayName("MacroApplication 테스트")
class MacroApplicationTest {

    @Test
    @DisplayName("main 메서드가 존재한다")
    void mainMethodExists() throws Exception {
        // Given
        Class<?> clazz = MacroApplication.class;

        // When
        Method mainMethod = clazz.getMethod("main", String[].class);

        // Then
        assertNotNull(mainMethod);
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
    }

    @Test
    @DisplayName("인자 없이 main 실행 시 예외 없이 종료된다")
    void main_WithoutArgs_ExitsGracefully() {
        // Given & When & Then
        assertDoesNotThrow(() -> MacroApplication.main(new String[]{}));
    }
}
