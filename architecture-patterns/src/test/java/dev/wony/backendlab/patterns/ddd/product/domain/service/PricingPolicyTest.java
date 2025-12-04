package dev.wony.backendlab.patterns.ddd.product.domain.service;

import dev.wony.backendlab.patterns.ddd.product.domain.entity.Product;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PricingPolicy 테스트")
class PricingPolicyTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.create(
                ProductName.of("테스트 상품"),
                "설명",
                Money.of(10000),
                Quantity.of(100),
                CategoryId.generate()
        );
    }

    @Nested
    @DisplayName("기본 정책 테스트")
    class StandardPolicyTest {

        @Test
        @DisplayName("기본 정책 - 할인 없음")
        void standard_noDiscount() {
            PricingPolicy policy = PricingPolicy.standard();

            Money price = policy.calculatePrice(product, Quantity.of(3));

            assertEquals(Money.of(30000), price);
        }
    }

    @Nested
    @DisplayName("정률 할인 정책 테스트")
    class PercentDiscountPolicyTest {

        @Test
        @DisplayName("10% 할인")
        void percentDiscount_10Percent() {
            PricingPolicy policy = PricingPolicy.percentDiscount(10);

            Money price = policy.calculatePrice(product, Quantity.of(2));

            assertEquals(Money.of(18000), price); // 20000 * 0.9
        }

        @Test
        @DisplayName("50% 할인")
        void percentDiscount_50Percent() {
            PricingPolicy policy = PricingPolicy.percentDiscount(50);

            Money price = policy.calculatePrice(product, Quantity.of(1));

            assertEquals(Money.of(5000), price);
        }

        @Test
        @DisplayName("유효하지 않은 할인율 예외")
        void percentDiscount_invalidPercent_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> PricingPolicy.percentDiscount(-10));
            assertThrows(IllegalArgumentException.class,
                    () -> PricingPolicy.percentDiscount(150));
        }
    }

    @Nested
    @DisplayName("정액 할인 정책 테스트")
    class FixedDiscountPolicyTest {

        @Test
        @DisplayName("3000원 할인")
        void fixedDiscount_3000Won() {
            PricingPolicy policy = PricingPolicy.fixedDiscount(Money.of(3000));

            Money price = policy.calculatePrice(product, Quantity.of(2));

            assertEquals(Money.of(17000), price); // 20000 - 3000
        }

        @Test
        @DisplayName("할인액이 총액보다 크면 0원")
        void fixedDiscount_exceedsTotal_returnsZero() {
            PricingPolicy policy = PricingPolicy.fixedDiscount(Money.of(50000));

            Money price = policy.calculatePrice(product, Quantity.of(1));

            assertEquals(Money.ZERO, price);
        }
    }

    @Nested
    @DisplayName("대량 구매 할인 정책 테스트")
    class BulkDiscountPolicyTest {

        @Test
        @DisplayName("기준 수량 이상이면 할인 적용")
        void bulkDiscount_meetsThreshold_appliesDiscount() {
            PricingPolicy policy = PricingPolicy.bulkDiscount(5, 20);

            Money price = policy.calculatePrice(product, Quantity.of(10));

            assertEquals(Money.of(80000), price); // 100000 * 0.8
        }

        @Test
        @DisplayName("기준 수량 미만이면 할인 미적용")
        void bulkDiscount_belowThreshold_noDiscount() {
            PricingPolicy policy = PricingPolicy.bulkDiscount(5, 20);

            Money price = policy.calculatePrice(product, Quantity.of(3));

            assertEquals(Money.of(30000), price); // 할인 없음
        }

        @Test
        @DisplayName("정확히 기준 수량이면 할인 적용")
        void bulkDiscount_exactThreshold_appliesDiscount() {
            PricingPolicy policy = PricingPolicy.bulkDiscount(5, 10);

            Money price = policy.calculatePrice(product, Quantity.of(5));

            assertEquals(Money.of(45000), price); // 50000 * 0.9
        }
    }
}
