package dev.wony.backendlab.patterns.ddd.product.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 도메인 이벤트 기본 인터페이스.
 * <p>
 * DDD에서 도메인 이벤트는 도메인에서 발생한 중요한 사건을 나타냅니다.
 * 이벤트는 과거형으로 명명하며, 불변이어야 합니다.
 */
public interface DomainEvent {

    /**
     * 이벤트 ID
     *
     * @return 고유 이벤트 ID
     */
    String getEventId();

    /**
     * 이벤트 발생 시각
     *
     * @return 발생 시각
     */
    LocalDateTime getOccurredAt();

    /**
     * 이벤트 타입명
     *
     * @return 이벤트 타입
     */
    default String getEventType() {
        return this.getClass().getSimpleName();
    }
}
