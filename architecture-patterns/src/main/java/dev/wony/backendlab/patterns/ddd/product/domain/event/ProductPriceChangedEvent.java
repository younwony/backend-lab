package dev.wony.backendlab.patterns.ddd.product.domain.event;

import dev.wony.backendlab.patterns.ddd.product.domain.vo.Money;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.ProductId;
import lombok.Getter;
import lombok.ToString;

/**
 * 상품 가격 변경 이벤트.
 * <p>
 * 상품 가격이 변경되었을 때 발행됩니다.
 */
@Getter
@ToString(callSuper = true)
public final class ProductPriceChangedEvent extends AbstractDomainEvent {

    private final ProductId productId;
    private final Money oldPrice;
    private final Money newPrice;

    public ProductPriceChangedEvent(ProductId productId, Money oldPrice, Money newPrice) {
        super();
        this.productId = productId;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    /**
     * 가격 인상 여부
     *
     * @return 인상되었으면 true
     */
    public boolean isPriceIncreased() {
        return newPrice.isGreaterThan(oldPrice);
    }
}
