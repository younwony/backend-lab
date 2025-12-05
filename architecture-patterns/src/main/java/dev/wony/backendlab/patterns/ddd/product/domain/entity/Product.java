package dev.wony.backendlab.patterns.ddd.product.domain.entity;

import com.google.common.collect.ImmutableList;
import dev.wony.backendlab.patterns.ddd.product.domain.event.DomainEvent;
import dev.wony.backendlab.patterns.ddd.product.domain.event.ProductCreatedEvent;
import dev.wony.backendlab.patterns.ddd.product.domain.event.ProductPriceChangedEvent;
import dev.wony.backendlab.patterns.ddd.product.domain.event.ProductStockChangedEvent;
import dev.wony.backendlab.patterns.ddd.product.domain.vo.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * 상품 Aggregate Root.
 * <p>
 * DDD에서 Aggregate Root는 일관성 경계의 진입점입니다.
 * 모든 상태 변경은 Aggregate Root를 통해서만 이루어져야 합니다.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "name", "price", "status"})
public class Product {

    private final ProductId id;
    private ProductName name;
    private String description;
    private Money price;
    private Quantity stockQuantity;
    private CategoryId categoryId;
    private ProductStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 도메인 이벤트 수집
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Product(ProductId id, ProductName name, String description,
                    Money price, Quantity stockQuantity, CategoryId categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.status = ProductStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * 새 상품 생성 (팩토리 메서드)
     *
     * @param name          상품명
     * @param description   상품 설명
     * @param price         가격
     * @param stockQuantity 재고 수량
     * @param categoryId    카테고리 ID
     * @return 새로 생성된 Product
     */
    public static Product create(ProductName name, String description,
                                  Money price, Quantity stockQuantity, CategoryId categoryId) {
        checkNotNull(name, "상품명은 필수입니다");
        checkNotNull(price, "가격은 필수입니다");
        checkNotNull(stockQuantity, "재고 수량은 필수입니다");
        checkNotNull(categoryId, "카테고리는 필수입니다");

        Product product = new Product(
                ProductId.generate(),
                name,
                description,
                price,
                stockQuantity,
                categoryId
        );

        // 도메인 이벤트 발행
        product.registerEvent(new ProductCreatedEvent(
                product.id,
                product.name,
                product.price,
                product.stockQuantity
        ));

        return product;
    }

    /**
     * 기존 상품 복원 (Repository에서 사용)
     */
    public static Product reconstitute(ProductId id, ProductName name, String description,
                                        Money price, Quantity stockQuantity, CategoryId categoryId,
                                        ProductStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        Product product = new Product(id, name, description, price, stockQuantity, categoryId);
        product.status = status;
        // createdAt, updatedAt은 private final이므로 별도 처리 필요 시 Builder 패턴 고려
        return product;
    }

    /**
     * 상품 판매 시작
     *
     * @throws IllegalStateException 판매 시작 불가 상태인 경우
     */
    public void startSelling() {
        checkState(this.status == ProductStatus.PENDING,
                "판매대기 상태에서만 판매를 시작할 수 있습니다. 현재 상태: %s", status);
        checkState(!this.stockQuantity.isZero(), "재고가 없어 판매를 시작할 수 없습니다");
        this.status = ProductStatus.ON_SALE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 상품 판매 중지
     */
    public void discontinue() {
        if (this.status == ProductStatus.DISCONTINUED) {
            return; // 이미 중지 상태면 무시
        }
        this.status = ProductStatus.DISCONTINUED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 가격 변경
     *
     * @param newPrice 새 가격
     * @throws IllegalArgumentException 가격이 null인 경우
     */
    public void changePrice(Money newPrice) {
        checkNotNull(newPrice, "가격은 null일 수 없습니다");

        if (this.price.equals(newPrice)) {
            return; // 가격이 같으면 변경하지 않음
        }

        Money oldPrice = this.price;
        this.price = newPrice;
        this.updatedAt = LocalDateTime.now();

        registerEvent(new ProductPriceChangedEvent(this.id, oldPrice, newPrice));
    }

    /**
     * 재고 추가
     *
     * @param quantity 추가할 수량
     */
    public void addStock(Quantity quantity) {
        checkNotNull(quantity, "수량은 null일 수 없습니다");

        Quantity oldQuantity = this.stockQuantity;
        this.stockQuantity = this.stockQuantity.add(quantity);
        this.updatedAt = LocalDateTime.now();

        // 품절 상태에서 재고가 추가되면 판매중으로 변경
        if (this.status == ProductStatus.OUT_OF_STOCK && this.stockQuantity.isPositive()) {
            this.status = ProductStatus.ON_SALE;
        }

        registerEvent(new ProductStockChangedEvent(this.id, oldQuantity, this.stockQuantity));
    }

    /**
     * 재고 감소 (판매 시)
     *
     * @param quantity 감소할 수량
     * @throws IllegalStateException    판매 불가 상태인 경우
     * @throws IllegalArgumentException 재고가 부족한 경우
     */
    public void decreaseStock(Quantity quantity) {
        checkNotNull(quantity, "수량은 null일 수 없습니다");
        checkState(this.status.isSellable(), "판매 불가 상태입니다. 현재 상태: %s", status);

        Quantity oldQuantity = this.stockQuantity;
        this.stockQuantity = this.stockQuantity.subtract(quantity);
        this.updatedAt = LocalDateTime.now();

        // 재고가 0이 되면 품절 처리
        if (this.stockQuantity.isZero()) {
            this.status = ProductStatus.OUT_OF_STOCK;
        }

        registerEvent(new ProductStockChangedEvent(this.id, oldQuantity, this.stockQuantity));
    }

    /**
     * 상품 정보 수정
     *
     * @param name        새 상품명
     * @param description 새 설명
     */
    public void updateInfo(ProductName name, String description) {
        checkNotNull(name, "상품명은 null일 수 없습니다");
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 카테고리 변경
     *
     * @param categoryId 새 카테고리 ID
     */
    public void changeCategory(CategoryId categoryId) {
        checkNotNull(categoryId, "카테고리는 null일 수 없습니다");
        this.categoryId = categoryId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 주문 가능 여부 확인
     *
     * @param requestedQuantity 요청 수량
     * @return 주문 가능하면 true
     */
    public boolean canOrder(Quantity requestedQuantity) {
        return status.isSellable() &&
               stockQuantity.isGreaterThanOrEqual(requestedQuantity);
    }

    // 도메인 이벤트 관련 메서드

    private void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * 수집된 도메인 이벤트 반환 및 초기화
     *
     * @return 도메인 이벤트 목록
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = ImmutableList.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

}
