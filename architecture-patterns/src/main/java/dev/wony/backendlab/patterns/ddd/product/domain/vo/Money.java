package dev.wony.backendlab.patterns.ddd.product.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 금액을 나타내는 Value Object.
 * <p>
 * 불변 객체로 설계되어 있으며, 금액 관련 연산을 제공합니다.
 * DDD에서 Value Object는 식별자가 없고 속성 값으로만 동등성을 판단합니다.
 */
@Getter
@EqualsAndHashCode
public final class Money {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        this.amount = amount.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 금액 생성
     *
     * @param amount 금액
     * @return Money 인스턴스
     * @throws IllegalArgumentException 금액이 null이거나 음수인 경우
     */
    public static Money of(BigDecimal amount) {
        checkNotNull(amount, "금액은 null일 수 없습니다");
        checkArgument(amount.compareTo(BigDecimal.ZERO) >= 0, "금액은 음수일 수 없습니다: %s", amount);
        return new Money(amount);
    }

    /**
     * long 타입으로 금액 생성
     *
     * @param amount 금액
     * @return Money 인스턴스
     */
    public static Money of(long amount) {
        return of(BigDecimal.valueOf(amount));
    }

    /**
     * 금액 더하기
     *
     * @param other 더할 금액
     * @return 새로운 Money 인스턴스
     */
    public Money add(Money other) {
        checkNotNull(other, "더할 금액은 null일 수 없습니다");
        return new Money(this.amount.add(other.amount));
    }

    /**
     * 금액 빼기
     *
     * @param other 뺄 금액
     * @return 새로운 Money 인스턴스
     * @throws IllegalArgumentException 결과가 음수가 되는 경우
     */
    public Money subtract(Money other) {
        checkNotNull(other, "뺄 금액은 null일 수 없습니다");
        BigDecimal result = this.amount.subtract(other.amount);
        checkArgument(result.compareTo(BigDecimal.ZERO) >= 0, "결과 금액이 음수가 됩니다");
        return new Money(result);
    }

    /**
     * 금액 곱하기
     *
     * @param multiplier 곱할 수
     * @return 새로운 Money 인스턴스
     */
    public Money multiply(int multiplier) {
        checkArgument(multiplier >= 0, "곱하는 수는 음수일 수 없습니다");
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)));
    }

    /**
     * 현재 금액이 다른 금액보다 큰지 확인
     *
     * @param other 비교할 금액
     * @return 크면 true
     */
    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * 현재 금액이 다른 금액보다 작거나 같은지 확인
     *
     * @param other 비교할 금액
     * @return 작거나 같으면 true
     */
    public boolean isLessThanOrEqual(Money other) {
        return this.amount.compareTo(other.amount) <= 0;
    }

    /**
     * 금액이 0인지 확인
     *
     * @return 0이면 true
     */
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public String toString() {
        return amount + "원";
    }
}
