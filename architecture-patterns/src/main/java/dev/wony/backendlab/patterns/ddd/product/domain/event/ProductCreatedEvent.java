package dev.wony.backendlab.patterns.ddd.product.domain.event;

import dev.wony.backendlab.patterns.ddd.product.domain.vo.Money;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.ProductId;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.ProductName;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.Quantity;

/**
 * 상품 생성 이벤트.
 * <p>
 * 새로운 상품이 등록되었을 때 발행됩니다.
 */
public final class ProductCreatedEvent extends AbstractDomainEvent {

    private final ProductId productId;
    private final ProductName productName;
    private final Money price;
    private final Quantity initialStock;

    public ProductCreatedEvent(ProductId productId, ProductName productName,
                                Money price, Quantity initialStock) {
        super();
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.initialStock = initialStock;
    }

    public ProductId getProductId() {
        return productId;
    }

    public ProductName getProductName() {
        return productName;
    }

    public Money getPrice() {
        return price;
    }

    public Quantity getInitialStock() {
        return initialStock;
    }

    @Override
    public String toString() {
        return String.format("ProductCreatedEvent{productId=%s, productName=%s, price=%s, initialStock=%s}",
                productId, productName, price, initialStock);
    }
}
