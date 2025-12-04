package dev.wony.backendlab.patterns.ddd.product.domain.event;

import dev.wony.backendlab.patterns.ddd.product.domain.vo.Money;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.ProductId;

/**
 * 상품 가격 변경 이벤트.
 * <p>
 * 상품 가격이 변경되었을 때 발행됩니다.
 */
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

    public ProductId getProductId() {
        return productId;
    }

    public Money getOldPrice() {
        return oldPrice;
    }

    public Money getNewPrice() {
        return newPrice;
    }

    /**
     * 가격 인상 여부
     *
     * @return 인상되었으면 true
     */
    public boolean isPriceIncreased() {
        return newPrice.isGreaterThan(oldPrice);
    }

    @Override
    public String toString() {
        return String.format("ProductPriceChangedEvent{productId=%s, oldPrice=%s, newPrice=%s}",
                productId, oldPrice, newPrice);
    }
}
