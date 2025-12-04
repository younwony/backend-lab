package dev.wony.backendlab.patterns.ddd.product.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Quantity Value Object 테스트")
class QuantityTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 수량으로 생성")
        void create_validValue_success() {
            Quantity quantity = Quantity.of(10);
            assertEquals(10, quantity.getValue());
        }

        @Test
        @DisplayName("0으로 생성 가능")
        void create_zero_success() {
            Quantity quantity = Quantity.of(0);
            assertTrue(quantity.isZero());
        }

        @Test
        @DisplayName("음수로 생성시 예외")
        void create_negative_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> Quantity.of(-1));
        }
    }

    @Nested
    @DisplayName("연산 테스트")
    class OperationTest {

        @Test
        @DisplayName("수량 더하기")
        void add_success() {
            Quantity q1 = Quantity.of(10);
            Quantity q2 = Quantity.of(5);

            Quantity result = q1.add(q2);

            assertEquals(Quantity.of(15), result);
        }

        @Test
        @DisplayName("수량 빼기")
        void subtract_success() {
            Quantity q1 = Quantity.of(10);
            Quantity q2 = Quantity.of(3);

            Quantity result = q1.subtract(q2);

            assertEquals(Quantity.of(7), result);
        }

        @Test
        @DisplayName("재고 부족시 예외")
        void subtract_insufficient_throwsException() {
            Quantity q1 = Quantity.of(5);
            Quantity q2 = Quantity.of(10);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> q1.subtract(q2));

            assertTrue(exception.getMessage().contains("재고가 부족합니다"));
        }
    }

    @Nested
    @DisplayName("비교 테스트")
    class ComparisonTest {

        @Test
        @DisplayName("isGreaterThanOrEqual - 크거나 같으면 true")
        void isGreaterThanOrEqual_greaterOrEqual_returnsTrue() {
            Quantity q1 = Quantity.of(10);
            Quantity q2 = Quantity.of(5);
            Quantity q3 = Quantity.of(10);

            assertTrue(q1.isGreaterThanOrEqual(q2));
            assertTrue(q1.isGreaterThanOrEqual(q3));
        }

        @Test
        @DisplayName("isPositive - 0보다 크면 true")
        void isPositive_positive_returnsTrue() {
            assertTrue(Quantity.of(1).isPositive());
            assertFalse(Quantity.of(0).isPositive());
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 수량은 동등")
        void equals_sameValue_returnsTrue() {
            Quantity q1 = Quantity.of(10);
            Quantity q2 = Quantity.of(10);

            assertEquals(q1, q2);
            assertEquals(q1.hashCode(), q2.hashCode());
        }

        @Test
        @DisplayName("ZERO 상수와 비교")
        void equals_zeroConstant_returnsTrue() {
            assertEquals(Quantity.ZERO, Quantity.of(0));
        }
    }
}
