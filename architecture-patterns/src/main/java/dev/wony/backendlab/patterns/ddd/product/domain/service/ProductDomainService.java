package dev.wony.backendlab.patterns.ddd.product.domain.service;

import dev.wony.backendlab.patterns.ddd.product.domain.entity.Product;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.Money;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.Quantity;

import java.util.List;
import java.util.Objects;

/**
 * 상품 도메인 서비스.
 * <p>
 * DDD에서 Domain Service는 특정 Entity나 Value Object에 속하지 않는
 * 도메인 로직을 담당합니다. 주로 여러 Aggregate에 걸친 비즈니스 로직을 처리합니다.
 */
public class ProductDomainService {

    /**
     * 총 주문 금액 계산
     * <p>
     * 여러 상품의 총 금액을 계산합니다. 이 로직은 특정 Entity에 속하지 않으므로
     * 도메인 서비스에서 처리합니다.
     *
     * @param product  상품
     * @param quantity 수량
     * @return 총 금액
     */
    public Money calculateTotalPrice(Product product, Quantity quantity) {
        Objects.requireNonNull(product, "상품은 null일 수 없습니다");
        Objects.requireNonNull(quantity, "수량은 null일 수 없습니다");

        return product.getPrice().multiply(quantity.getValue());
    }

    /**
     * 여러 상품의 총 금액 계산
     *
     * @param items 상품-수량 쌍 목록
     * @return 총 금액
     */
    public Money calculateTotalPrice(List<OrderItem> items) {
        Objects.requireNonNull(items, "주문 항목은 null일 수 없습니다");

        return items.stream()
                .map(item -> calculateTotalPrice(item.product(), item.quantity()))
                .reduce(Money.ZERO, Money::add);
    }

    /**
     * 할인 적용
     *
     * @param originalPrice    원래 가격
     * @param discountPercent  할인율 (0-100)
     * @return 할인된 가격
     */
    public Money applyDiscount(Money originalPrice, int discountPercent) {
        Objects.requireNonNull(originalPrice, "가격은 null일 수 없습니다");

        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("할인율은 0-100 사이여야 합니다: " + discountPercent);
        }

        if (discountPercent == 0) {
            return originalPrice;
        }

        if (discountPercent == 100) {
            return Money.ZERO;
        }

        long discountedAmount = originalPrice.getAmount()
                .multiply(java.math.BigDecimal.valueOf(100 - discountPercent))
                .divide(java.math.BigDecimal.valueOf(100), java.math.RoundingMode.HALF_UP)
                .longValue();

        return Money.of(discountedAmount);
    }

    /**
     * 대량 주문 가능 여부 확인
     * <p>
     * 여러 상품의 재고를 한번에 확인합니다.
     *
     * @param items 상품-수량 쌍 목록
     * @return 모두 주문 가능하면 true
     */
    public boolean canOrderAll(List<OrderItem> items) {
        Objects.requireNonNull(items, "주문 항목은 null일 수 없습니다");

        return items.stream()
                .allMatch(item -> item.product().canOrder(item.quantity()));
    }

    /**
     * 주문 항목 레코드
     *
     * @param product  상품
     * @param quantity 수량
     */
    public record OrderItem(Product product, Quantity quantity) {
        public OrderItem {
            Objects.requireNonNull(product, "상품은 null일 수 없습니다");
            Objects.requireNonNull(quantity, "수량은 null일 수 없습니다");
        }
    }
}
