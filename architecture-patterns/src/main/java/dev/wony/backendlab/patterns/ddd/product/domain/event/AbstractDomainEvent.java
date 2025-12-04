package dev.wony.backendlab.patterns.ddd.product.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 도메인 이벤트 추상 기본 클래스.
 * <p>
 * 모든 도메인 이벤트의 공통 속성을 제공합니다.
 */
public abstract class AbstractDomainEvent implements DomainEvent {

    private final String eventId;
    private final LocalDateTime occurredAt;

    protected AbstractDomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String toString() {
        return String.format("%s{eventId='%s', occurredAt=%s}",
                getEventType(), eventId, occurredAt);
    }
}
