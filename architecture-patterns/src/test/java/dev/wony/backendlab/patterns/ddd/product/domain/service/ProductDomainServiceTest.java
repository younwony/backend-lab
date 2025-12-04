package dev.wony.backendlab.patterns.ddd.product.domain.service;

import dev.wony.backendlab.patterns.ddd.product.domain.entity.Product;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductDomainService 테스트")
class ProductDomainServiceTest {

    private ProductDomainService service;
    private Product product;

    @BeforeEach
    void setUp() {
        service = new ProductDomainService();
        product = Product.create(
                ProductName.of("테스트 상품"),
                "설명",
                Money.of(10000),
                Quantity.of(100),
                CategoryId.generate()
        );
        product.startSelling();
    }

    @Nested
    @DisplayName("총 가격 계산 테스트")
    class CalculateTotalPriceTest {

        @Test
        @DisplayName("단일 상품 총 가격 계산")
        void calculateTotalPrice_singleProduct_success() {
            Money total = service.calculateTotalPrice(product, Quantity.of(3));

            assertEquals(Money.of(30000), total);
        }

        @Test
        @DisplayName("여러 상품 총 가격 계산")
        void calculateTotalPrice_multipleProducts_success() {
            Product product2 = Product.create(
                    ProductName.of("상품2"),
                    "설명",
                    Money.of(5000),
                    Quantity.of(50),
                    CategoryId.generate()
            );

            List<ProductDomainService.OrderItem> items = List.of(
                    new ProductDomainService.OrderItem(product, Quantity.of(2)),
                    new ProductDomainService.OrderItem(product2, Quantity.of(3))
            );

            Money total = service.calculateTotalPrice(items);

            assertEquals(Money.of(35000), total); // 20000 + 15000
        }

        @Test
        @DisplayName("빈 목록이면 0원")
        void calculateTotalPrice_emptyList_returnsZero() {
            Money total = service.calculateTotalPrice(List.of());

            assertEquals(Money.ZERO, total);
        }
    }

    @Nested
    @DisplayName("할인 적용 테스트")
    class ApplyDiscountTest {

        @Test
        @DisplayName("10% 할인 적용")
        void applyDiscount_10Percent_success() {
            Money original = Money.of(10000);

            Money discounted = service.applyDiscount(original, 10);

            assertEquals(Money.of(9000), discounted);
        }

        @Test
        @DisplayName("0% 할인은 원래 가격")
        void applyDiscount_zeroPercent_returnsOriginal() {
            Money original = Money.of(10000);

            Money discounted = service.applyDiscount(original, 0);

            assertEquals(original, discounted);
        }

        @Test
        @DisplayName("100% 할인은 0원")
        void applyDiscount_100Percent_returnsZero() {
            Money original = Money.of(10000);

            Money discounted = service.applyDiscount(original, 100);

            assertEquals(Money.ZERO, discounted);
        }

        @Test
        @DisplayName("유효하지 않은 할인율 예외")
        void applyDiscount_invalidPercent_throwsException() {
            Money original = Money.of(10000);

            assertThrows(IllegalArgumentException.class,
                    () -> service.applyDiscount(original, -10));
            assertThrows(IllegalArgumentException.class,
                    () -> service.applyDiscount(original, 150));
        }
    }

    @Nested
    @DisplayName("대량 주문 가능 여부 테스트")
    class CanOrderAllTest {

        @Test
        @DisplayName("모든 상품 주문 가능")
        void canOrderAll_allAvailable_returnsTrue() {
            List<ProductDomainService.OrderItem> items = List.of(
                    new ProductDomainService.OrderItem(product, Quantity.of(10))
            );

            assertTrue(service.canOrderAll(items));
        }

        @Test
        @DisplayName("일부 재고 부족시 false")
        void canOrderAll_someInsufficient_returnsFalse() {
            List<ProductDomainService.OrderItem> items = List.of(
                    new ProductDomainService.OrderItem(product, Quantity.of(200)) // 재고 100개
            );

            assertFalse(service.canOrderAll(items));
        }
    }
}
