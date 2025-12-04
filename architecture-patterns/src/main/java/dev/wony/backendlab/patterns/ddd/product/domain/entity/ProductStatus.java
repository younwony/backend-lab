package dev.wony.backendlab.patterns.ddd.product.domain.entity;

/**
 * 상품 상태를 나타내는 열거형.
 */
public enum ProductStatus {

    /**
     * 판매 대기 - 등록되었으나 아직 판매 시작 전
     */
    PENDING("판매대기"),

    /**
     * 판매중 - 현재 판매 가능한 상태
     */
    ON_SALE("판매중"),

    /**
     * 품절 - 재고 소진
     */
    OUT_OF_STOCK("품절"),

    /**
     * 판매 중지 - 관리자에 의해 판매 중지됨
     */
    DISCONTINUED("판매중지");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 판매 가능한 상태인지 확인
     *
     * @return 판매 가능하면 true
     */
    public boolean isSellable() {
        return this == ON_SALE;
    }
}
