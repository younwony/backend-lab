package dev.wony.backendlab.patterns.ddd.product.domain.service;

import dev.wony.backendlab.patterns.ddd.product.domain.entity.Product;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.Money;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.Quantity;

/**
 * 가격 정책 인터페이스.
 * <p>
 * 전략 패턴을 적용하여 다양한 가격 정책을 구현할 수 있습니다.
 */
@FunctionalInterface
public interface PricingPolicy {

    /**
     * 최종 가격 계산
     *
     * @param product  상품
     * @param quantity 수량
     * @return 계산된 가격
     */
    Money calculatePrice(Product product, Quantity quantity);

    /**
     * 기본 가격 정책 (할인 없음)
     */
    static PricingPolicy standard() {
        return (product, quantity) -> product.getPrice().multiply(quantity.getValue());
    }

    /**
     * 정률 할인 정책
     *
     * @param discountPercent 할인율 (0-100)
     * @return 할인 적용 가격 정책
     */
    static PricingPolicy percentDiscount(int discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("할인율은 0-100 사이여야 합니다");
        }
        return (product, quantity) -> {
            Money basePrice = product.getPrice().multiply(quantity.getValue());
            long discounted = basePrice.getAmount()
                    .multiply(java.math.BigDecimal.valueOf(100 - discountPercent))
                    .divide(java.math.BigDecimal.valueOf(100), java.math.RoundingMode.HALF_UP)
                    .longValue();
            return Money.of(discounted);
        };
    }

    /**
     * 정액 할인 정책
     *
     * @param discountAmount 할인 금액
     * @return 할인 적용 가격 정책
     */
    static PricingPolicy fixedDiscount(Money discountAmount) {
        return (product, quantity) -> {
            Money basePrice = product.getPrice().multiply(quantity.getValue());
            if (basePrice.isLessThanOrEqual(discountAmount)) {
                return Money.ZERO;
            }
            return basePrice.subtract(discountAmount);
        };
    }

    /**
     * 대량 구매 할인 정책
     *
     * @param thresholdQuantity 할인 적용 기준 수량
     * @param discountPercent   할인율
     * @return 대량 구매 할인 정책
     */
    static PricingPolicy bulkDiscount(int thresholdQuantity, int discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("할인율은 0-100 사이여야 합니다");
        }
        return (product, quantity) -> {
            Money basePrice = product.getPrice().multiply(quantity.getValue());
            if (quantity.getValue() >= thresholdQuantity) {
                long discounted = basePrice.getAmount()
                        .multiply(java.math.BigDecimal.valueOf(100 - discountPercent))
                        .divide(java.math.BigDecimal.valueOf(100), java.math.RoundingMode.HALF_UP)
                        .longValue();
                return Money.of(discounted);
            }
            return basePrice;
        };
    }
}
