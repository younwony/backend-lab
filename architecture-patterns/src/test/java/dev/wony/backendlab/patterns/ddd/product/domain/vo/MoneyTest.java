package dev.wony.backendlab.patterns.ddd.product.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Value Object 테스트")
class MoneyTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 금액으로 생성")
        void create_validAmount_success() {
            Money money = Money.of(1000);
            assertEquals(BigDecimal.valueOf(1000), money.getAmount());
        }

        @Test
        @DisplayName("BigDecimal로 생성")
        void create_withBigDecimal_success() {
            Money money = Money.of(BigDecimal.valueOf(1500));
            assertEquals(BigDecimal.valueOf(1500), money.getAmount());
        }

        @Test
        @DisplayName("0원 생성 가능")
        void create_zero_success() {
            Money money = Money.of(0);
            assertTrue(money.isZero());
        }

        @Test
        @DisplayName("null로 생성시 예외")
        void create_null_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> Money.of((BigDecimal) null));
        }

        @Test
        @DisplayName("음수로 생성시 예외")
        void create_negative_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> Money.of(-1000));
        }
    }

    @Nested
    @DisplayName("연산 테스트")
    class OperationTest {

        @Test
        @DisplayName("금액 더하기")
        void add_success() {
            Money money1 = Money.of(1000);
            Money money2 = Money.of(500);

            Money result = money1.add(money2);

            assertEquals(Money.of(1500), result);
        }

        @Test
        @DisplayName("금액 빼기")
        void subtract_success() {
            Money money1 = Money.of(1000);
            Money money2 = Money.of(300);

            Money result = money1.subtract(money2);

            assertEquals(Money.of(700), result);
        }

        @Test
        @DisplayName("결과가 음수면 예외")
        void subtract_resultNegative_throwsException() {
            Money money1 = Money.of(100);
            Money money2 = Money.of(500);

            assertThrows(IllegalArgumentException.class,
                    () -> money1.subtract(money2));
        }

        @Test
        @DisplayName("금액 곱하기")
        void multiply_success() {
            Money money = Money.of(1000);

            Money result = money.multiply(3);

            assertEquals(Money.of(3000), result);
        }

        @Test
        @DisplayName("음수 곱하기 예외")
        void multiply_negative_throwsException() {
            Money money = Money.of(1000);

            assertThrows(IllegalArgumentException.class,
                    () -> money.multiply(-1));
        }
    }

    @Nested
    @DisplayName("비교 테스트")
    class ComparisonTest {

        @Test
        @DisplayName("isGreaterThan - 크면 true")
        void isGreaterThan_greater_returnsTrue() {
            Money money1 = Money.of(1000);
            Money money2 = Money.of(500);

            assertTrue(money1.isGreaterThan(money2));
        }

        @Test
        @DisplayName("isGreaterThan - 같으면 false")
        void isGreaterThan_equal_returnsFalse() {
            Money money1 = Money.of(1000);
            Money money2 = Money.of(1000);

            assertFalse(money1.isGreaterThan(money2));
        }

        @Test
        @DisplayName("isLessThanOrEqual - 작거나 같으면 true")
        void isLessThanOrEqual_lessOrEqual_returnsTrue() {
            Money money1 = Money.of(500);
            Money money2 = Money.of(1000);

            assertTrue(money1.isLessThanOrEqual(money2));
            assertTrue(Money.of(1000).isLessThanOrEqual(money2));
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 금액은 동등")
        void equals_sameAmount_returnsTrue() {
            Money money1 = Money.of(1000);
            Money money2 = Money.of(1000);

            assertEquals(money1, money2);
            assertEquals(money1.hashCode(), money2.hashCode());
        }

        @Test
        @DisplayName("다른 금액은 동등하지 않음")
        void equals_differentAmount_returnsFalse() {
            Money money1 = Money.of(1000);
            Money money2 = Money.of(2000);

            assertNotEquals(money1, money2);
        }
    }
}
