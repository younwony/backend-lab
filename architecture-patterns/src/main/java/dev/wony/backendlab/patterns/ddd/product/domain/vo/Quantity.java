package dev.wony.backendlab.patterns.ddd.product.domain.vo;

import java.util.Objects;

/**
 * 수량을 나타내는 Value Object.
 * <p>
 * 재고 수량, 주문 수량 등에 사용됩니다.
 */
public final class Quantity {

    public static final Quantity ZERO = new Quantity(0);

    private final int value;

    private Quantity(int value) {
        this.value = value;
    }

    /**
     * 수량 생성
     *
     * @param value 수량
     * @return Quantity 인스턴스
     * @throws IllegalArgumentException 수량이 음수인 경우
     */
    public static Quantity of(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("수량은 음수일 수 없습니다: " + value);
        }
        return new Quantity(value);
    }

    /**
     * 수량 더하기
     *
     * @param other 더할 수량
     * @return 새로운 Quantity 인스턴스
     */
    public Quantity add(Quantity other) {
        Objects.requireNonNull(other, "더할 수량은 null일 수 없습니다");
        return new Quantity(this.value + other.value);
    }

    /**
     * 수량 빼기
     *
     * @param other 뺄 수량
     * @return 새로운 Quantity 인스턴스
     * @throws IllegalArgumentException 결과가 음수가 되는 경우
     */
    public Quantity subtract(Quantity other) {
        Objects.requireNonNull(other, "뺄 수량은 null일 수 없습니다");
        int result = this.value - other.value;
        if (result < 0) {
            throw new IllegalArgumentException(
                    String.format("재고가 부족합니다. 현재: %d, 요청: %d", this.value, other.value));
        }
        return new Quantity(result);
    }

    /**
     * 현재 수량이 다른 수량보다 크거나 같은지 확인
     *
     * @param other 비교할 수량
     * @return 크거나 같으면 true
     */
    public boolean isGreaterThanOrEqual(Quantity other) {
        return this.value >= other.value;
    }

    /**
     * 수량이 0인지 확인
     *
     * @return 0이면 true
     */
    public boolean isZero() {
        return this.value == 0;
    }

    /**
     * 수량이 0보다 큰지 확인
     *
     * @return 0보다 크면 true
     */
    public boolean isPositive() {
        return this.value > 0;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity = (Quantity) o;
        return value == quantity.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
