package dev.wony.backendlab.patterns.ddd.product.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 상품명을 나타내는 Value Object.
 * <p>
 * 상품명에 대한 유효성 검증 로직을 캡슐화합니다.
 */
@Getter
@EqualsAndHashCode
public final class ProductName {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    private final String value;

    private ProductName(String value) {
        this.value = value;
    }

    /**
     * 상품명 생성
     *
     * @param value 상품명
     * @return ProductName 인스턴스
     * @throws IllegalArgumentException 유효하지 않은 상품명인 경우
     */
    public static ProductName of(String value) {
        validate(value);
        return new ProductName(value.trim());
    }

    private static void validate(String value) {
        checkArgument(StringUtils.isNotBlank(value), "상품명은 필수입니다");
        String trimmed = value.trim();
        checkArgument(trimmed.length() >= MIN_LENGTH, "상품명은 최소 %s자 이상이어야 합니다", MIN_LENGTH);
        checkArgument(trimmed.length() <= MAX_LENGTH, "상품명은 최대 %s자까지 가능합니다", MAX_LENGTH);
    }

    @Override
    public String toString() {
        return value;
    }
}
