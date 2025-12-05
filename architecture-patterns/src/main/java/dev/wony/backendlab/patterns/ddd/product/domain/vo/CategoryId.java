package dev.wony.backendlab.patterns.ddd.product.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 카테고리 식별자를 나타내는 Value Object.
 */
@Getter
@EqualsAndHashCode
public final class CategoryId {

    private final String value;

    private CategoryId(String value) {
        this.value = value;
    }

    /**
     * 새로운 카테고리 ID 생성
     *
     * @return 새로운 CategoryId 인스턴스
     */
    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID().toString());
    }

    /**
     * 기존 ID 값으로 CategoryId 생성
     *
     * @param value ID 값
     * @return CategoryId 인스턴스
     * @throws IllegalArgumentException ID가 null이거나 빈 문자열인 경우
     */
    public static CategoryId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("카테고리 ID는 필수입니다");
        }
        return new CategoryId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
