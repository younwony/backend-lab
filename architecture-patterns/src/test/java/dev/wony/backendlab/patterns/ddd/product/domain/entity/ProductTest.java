package dev.wony.backendlab.patterns.ddd.product.domain.entity;

import dev.wony.backendlab.patterns.ddd.product.domain.event.DomainEvent;
import dev.wony.backendlab.patterns.ddd.product.domain.event.ProductCreatedEvent;
import dev.wony.backendlab.patterns.ddd.product.domain.event.ProductPriceChangedEvent;
import dev.wony.backendlab.patterns.ddd.product.domain.event.ProductStockChangedEvent;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Aggregate Root 테스트")
class ProductTest {

    private ProductName name;
    private Money price;
    private Quantity stockQuantity;
    private CategoryId categoryId;

    @BeforeEach
    void setUp() {
        name = ProductName.of("테스트 상품");
        price = Money.of(10000);
        stockQuantity = Quantity.of(100);
        categoryId = CategoryId.generate();
    }

    @Nested
    @DisplayName("상품 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 정보로 상품 생성")
        void create_validInfo_success() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);

            assertNotNull(product.getId());
            assertEquals(name, product.getName());
            assertEquals(price, product.getPrice());
            assertEquals(stockQuantity, product.getStockQuantity());
            assertEquals(ProductStatus.PENDING, product.getStatus());
        }

        @Test
        @DisplayName("생성시 ProductCreatedEvent 발행")
        void create_emitsCreatedEvent() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);

            List<DomainEvent> events = product.pullDomainEvents();

            assertEquals(1, events.size());
            assertInstanceOf(ProductCreatedEvent.class, events.get(0));
        }

        @Test
        @DisplayName("필수 값 누락시 예외")
        void create_missingRequired_throwsException() {
            assertThrows(NullPointerException.class,
                    () -> Product.create(null, "설명", price, stockQuantity, categoryId));
            assertThrows(NullPointerException.class,
                    () -> Product.create(name, "설명", null, stockQuantity, categoryId));
        }
    }

    @Nested
    @DisplayName("상태 변경 테스트")
    class StatusChangeTest {

        @Test
        @DisplayName("판매 시작")
        void startSelling_fromPending_success() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);

            product.startSelling();

            assertEquals(ProductStatus.ON_SALE, product.getStatus());
        }

        @Test
        @DisplayName("판매대기 상태가 아니면 판매 시작 불가")
        void startSelling_notPending_throwsException() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);
            product.startSelling();

            assertThrows(IllegalStateException.class, product::startSelling);
        }

        @Test
        @DisplayName("재고 없으면 판매 시작 불가")
        void startSelling_noStock_throwsException() {
            Product product = Product.create(name, "설명", price, Quantity.ZERO, categoryId);

            assertThrows(IllegalStateException.class, product::startSelling);
        }

        @Test
        @DisplayName("판매 중지")
        void discontinue_success() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);
            product.startSelling();

            product.discontinue();

            assertEquals(ProductStatus.DISCONTINUED, product.getStatus());
        }
    }

    @Nested
    @DisplayName("가격 변경 테스트")
    class PriceChangeTest {

        @Test
        @DisplayName("가격 변경 성공")
        void changePrice_success() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);
            product.pullDomainEvents(); // 생성 이벤트 제거

            Money newPrice = Money.of(15000);
            product.changePrice(newPrice);

            assertEquals(newPrice, product.getPrice());
        }

        @Test
        @DisplayName("가격 변경시 이벤트 발행")
        void changePrice_emitsPriceChangedEvent() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);
            product.pullDomainEvents();

            Money newPrice = Money.of(15000);
            product.changePrice(newPrice);

            List<DomainEvent> events = product.pullDomainEvents();
            assertEquals(1, events.size());
            assertInstanceOf(ProductPriceChangedEvent.class, events.get(0));

            ProductPriceChangedEvent event = (ProductPriceChangedEvent) events.get(0);
            assertEquals(price, event.getOldPrice());
            assertEquals(newPrice, event.getNewPrice());
        }

        @Test
        @DisplayName("같은 가격으로 변경시 이벤트 미발행")
        void changePrice_samePrice_noEvent() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);
            product.pullDomainEvents();

            product.changePrice(Money.of(10000));

            assertTrue(product.pullDomainEvents().isEmpty());
        }
    }

    @Nested
    @DisplayName("재고 관리 테스트")
    class StockManagementTest {

        @Test
        @DisplayName("재고 추가")
        void addStock_success() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);
            product.pullDomainEvents();

            product.addStock(Quantity.of(50));

            assertEquals(Quantity.of(150), product.getStockQuantity());
        }

        @Test
        @DisplayName("재고 추가시 이벤트 발행")
        void addStock_emitsStockChangedEvent() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);
            product.pullDomainEvents();

            product.addStock(Quantity.of(50));

            List<DomainEvent> events = product.pullDomainEvents();
            assertEquals(1, events.size());
            assertInstanceOf(ProductStockChangedEvent.class, events.get(0));
        }

        @Test
        @DisplayName("품절 상태에서 재고 추가시 판매중으로 변경")
        void addStock_fromOutOfStock_becomesOnSale() {
            Product product = Product.create(name, "설명", price, Quantity.of(10), categoryId);
            product.startSelling();
            product.decreaseStock(Quantity.of(10)); // 품절

            assertEquals(ProductStatus.OUT_OF_STOCK, product.getStatus());

            product.addStock(Quantity.of(5));

            assertEquals(ProductStatus.ON_SALE, product.getStatus());
        }

        @Test
        @DisplayName("재고 감소")
        void decreaseStock_success() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);
            product.startSelling();
            product.pullDomainEvents();

            product.decreaseStock(Quantity.of(30));

            assertEquals(Quantity.of(70), product.getStockQuantity());
        }

        @Test
        @DisplayName("재고 0이 되면 품절 처리")
        void decreaseStock_becomesZero_outOfStock() {
            Product product = Product.create(name, "설명", price, Quantity.of(10), categoryId);
            product.startSelling();

            product.decreaseStock(Quantity.of(10));

            assertEquals(ProductStatus.OUT_OF_STOCK, product.getStatus());
        }

        @Test
        @DisplayName("판매 불가 상태에서 재고 감소 불가")
        void decreaseStock_notSellable_throwsException() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);
            // PENDING 상태

            assertThrows(IllegalStateException.class,
                    () -> product.decreaseStock(Quantity.of(10)));
        }

        @Test
        @DisplayName("재고 부족시 예외")
        void decreaseStock_insufficient_throwsException() {
            Product product = Product.create(name, "설명", price, Quantity.of(5), categoryId);
            product.startSelling();

            assertThrows(IllegalArgumentException.class,
                    () -> product.decreaseStock(Quantity.of(10)));
        }
    }

    @Nested
    @DisplayName("주문 가능 여부 테스트")
    class CanOrderTest {

        @Test
        @DisplayName("판매중이고 재고 충분하면 주문 가능")
        void canOrder_sellableAndSufficientStock_returnsTrue() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);
            product.startSelling();

            assertTrue(product.canOrder(Quantity.of(50)));
        }

        @Test
        @DisplayName("판매중이 아니면 주문 불가")
        void canOrder_notSellable_returnsFalse() {
            Product product = Product.create(name, "설명", price, stockQuantity, categoryId);

            assertFalse(product.canOrder(Quantity.of(10)));
        }

        @Test
        @DisplayName("재고 부족하면 주문 불가")
        void canOrder_insufficientStock_returnsFalse() {
            Product product = Product.create(name, "설명", price, Quantity.of(5), categoryId);
            product.startSelling();

            assertFalse(product.canOrder(Quantity.of(10)));
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 ID면 동등")
        void equals_sameId_returnsTrue() {
            Product product1 = Product.create(name, "설명1", price, stockQuantity, categoryId);
            // 실제로는 ID가 다르므로 동등하지 않음
            Product product2 = Product.create(name, "설명2", price, stockQuantity, categoryId);

            assertNotEquals(product1, product2);
            assertEquals(product1, product1);
        }
    }
}
