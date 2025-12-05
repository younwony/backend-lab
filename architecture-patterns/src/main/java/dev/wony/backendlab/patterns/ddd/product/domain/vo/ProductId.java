package dev.wony.backendlab.patterns.ddd.product.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 상품 식별자를 나타내는 Value Object.
 * <p>
 * Entity의 식별자를 Value Object로 감싸서 타입 안전성을 확보합니다.
 */
@Getter
@EqualsAndHashCode
public final class ProductId {

    private final String value;

    private ProductId(String value) {
        this.value = value;
    }

    /**
     * 새로운 상품 ID 생성
     *
     * @return 새로운 ProductId 인스턴스
     */
    public static ProductId generate() {
        return new ProductId(UUID.randomUUID().toString());
    }

    /**
     * 기존 ID 값으로 ProductId 생성
     *
     * @param value ID 값
     * @return ProductId 인스턴스
     * @throws IllegalArgumentException ID가 null이거나 빈 문자열인 경우
     */
    public static ProductId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품 ID는 필수입니다");
        }
        return new ProductId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
