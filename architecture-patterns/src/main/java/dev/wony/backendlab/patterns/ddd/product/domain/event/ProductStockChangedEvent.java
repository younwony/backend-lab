package dev.wony.backendlab.patterns.ddd.product.domain.event;

import dev.wony.backendlab.patterns.ddd.product.domain.vo.ProductId;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.Quantity;
import lombok.Getter;
import lombok.ToString;

/**
 * 상품 재고 변경 이벤트.
 * <p>
 * 상품 재고가 증가하거나 감소했을 때 발행됩니다.
 */
@Getter
@ToString(callSuper = true)
public final class ProductStockChangedEvent extends AbstractDomainEvent {

    private final ProductId productId;
    private final Quantity previousQuantity;
    private final Quantity currentQuantity;

    public ProductStockChangedEvent(ProductId productId,
                                     Quantity previousQuantity, Quantity currentQuantity) {
        super();
        this.productId = productId;
        this.previousQuantity = previousQuantity;
        this.currentQuantity = currentQuantity;
    }

    /**
     * 재고 증가 여부
     *
     * @return 증가했으면 true
     */
    public boolean isStockIncreased() {
        return currentQuantity.isGreaterThanOrEqual(previousQuantity) &&
               !currentQuantity.equals(previousQuantity);
    }

    /**
     * 재고 감소 여부
     *
     * @return 감소했으면 true
     */
    public boolean isStockDecreased() {
        return previousQuantity.isGreaterThanOrEqual(currentQuantity) &&
               !currentQuantity.equals(previousQuantity);
    }

    /**
     * 품절 여부
     *
     * @return 품절이면 true
     */
    public boolean isOutOfStock() {
        return currentQuantity.isZero();
    }
}
