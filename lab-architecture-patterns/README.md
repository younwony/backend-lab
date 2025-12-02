# lab-architecture-patterns

> 디자인 패턴, 아키텍처 패턴, DDD 실험 모듈

## 목적

소프트웨어 설계 패턴을 직접 구현하고 테스트합니다. 이론으로만 알던 패턴을 실제 코드로 구현하여 이해도를 높이고, 실무 적용 시 참고할 수 있는 레퍼런스를 만듭니다.

## 주요 실험 주제

### GoF 디자인 패턴

#### 생성 패턴 (Creational)
- **Factory Method**: 객체 생성 로직 캡슐화
- **Abstract Factory**: 관련 객체군 생성
- **Builder**: 복잡한 객체 단계적 생성
- **Singleton**: 인스턴스 하나만 보장

#### 구조 패턴 (Structural)
- **Adapter**: 인터페이스 변환
- **Decorator**: 동적 기능 추가
- **Facade**: 복잡한 시스템의 단순 인터페이스
- **Proxy**: 접근 제어 및 지연 로딩

#### 행위 패턴 (Behavioral)
- **Strategy**: 알고리즘 캡슐화 및 교체
- **Observer**: 이벤트 기반 통신
- **Template Method**: 알고리즘 골격 정의
- **Command**: 요청을 객체로 캡슐화

### 아키텍처 패턴

#### 계층형 아키텍처
- **Layered Architecture**: Controller → Service → Repository
- 계층 간 의존성 규칙

#### 헥사고날 아키텍처 (Ports & Adapters)
- **Domain**: 핵심 비즈니스 로직
- **Ports**: 인터페이스 정의 (인바운드/아웃바운드)
- **Adapters**: 외부 시스템 연결

#### 클린 아키텍처
- **Entities**: 핵심 비즈니스 규칙
- **Use Cases**: 애플리케이션 비즈니스 규칙
- **Interface Adapters**: 컨트롤러, 프레젠터
- **Frameworks**: 외부 프레임워크

### DDD (Domain-Driven Design)

#### 전술적 설계
- **Entity**: 식별자를 가진 객체
- **Value Object**: 불변 값 객체
- **Aggregate**: 일관성 경계
- **Repository**: 영속성 추상화
- **Domain Service**: 도메인 로직
- **Domain Event**: 도메인 이벤트

#### 전략적 설계
- **Bounded Context**: 경계 컨텍스트
- **Context Map**: 컨텍스트 간 관계

### 이벤트 기반 아키텍처

- **Event Sourcing**: 이벤트로 상태 저장
- **CQRS**: 명령과 조회 분리
- **Saga Pattern**: 분산 트랜잭션 관리

## 의존성 예시

```groovy
dependencies {
    implementation project(':lab-common')
    implementation 'org.springframework.boot:spring-boot-starter'

    // 이벤트 기반 아키텍처
    // implementation 'org.springframework.boot:spring-boot-starter-amqp'

    // 캐싱 패턴
    // implementation 'org.springframework.boot:spring-boot-starter-cache'

    // JPA (Repository 패턴)
    // implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
}
```

## 패키지 구조

```
src/main/java/dev/wony/backendlab/patterns/
├── gof/                         # GoF 디자인 패턴
│   ├── creational/
│   │   ├── factory/
│   │   ├── builder/
│   │   └── singleton/
│   ├── structural/
│   │   ├── adapter/
│   │   ├── decorator/
│   │   └── proxy/
│   └── behavioral/
│       ├── strategy/
│       ├── observer/
│       └── template/
│
├── architecture/                # 아키텍처 패턴
│   ├── layered/
│   ├── hexagonal/
│   └── clean/
│
├── ddd/                         # DDD 패턴
│   ├── entity/
│   ├── valueobject/
│   ├── aggregate/
│   ├── repository/
│   └── domainevent/
│
└── event/                       # 이벤트 기반
    ├── sourcing/
    ├── cqrs/
    └── saga/
```

## 실험 예시

### Strategy 패턴

```java
// 전략 인터페이스
public interface PaymentStrategy {
    void pay(int amount);
}

// 구체적인 전략들
public class CreditCardPayment implements PaymentStrategy { ... }
public class KakaoPayPayment implements PaymentStrategy { ... }

// 컨텍스트
public class PaymentContext {
    private PaymentStrategy strategy;

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void executePayment(int amount) {
        strategy.pay(amount);
    }
}
```

### 헥사고날 아키텍처

```
┌─────────────────────────────────────────┐
│              Adapters (In)              │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  │
│  │   REST  │  │  gRPC   │  │  CLI    │  │
│  └────┬────┘  └────┬────┘  └────┬────┘  │
│       │            │            │       │
│  ┌────▼────────────▼────────────▼────┐  │
│  │         Ports (Inbound)           │  │
│  └────────────────┬──────────────────┘  │
│                   │                     │
│  ┌────────────────▼──────────────────┐  │
│  │             Domain                │  │
│  │    (Entities, Services, Rules)    │  │
│  └────────────────┬──────────────────┘  │
│                   │                     │
│  ┌────────────────▼──────────────────┐  │
│  │        Ports (Outbound)           │  │
│  └────┬────────────┬────────────┬────┘  │
│       │            │            │       │
│  ┌────▼────┐  ┌────▼────┐  ┌────▼────┐  │
│  │   DB    │  │  Cache  │  │  API    │  │
│  └─────────┘  └─────────┘  └─────────┘  │
│              Adapters (Out)             │
└─────────────────────────────────────────┘
```

### Aggregate Root (DDD)

```java
public class Order {  // Aggregate Root
    private OrderId id;
    private List<OrderLine> orderLines;  // 내부 엔티티
    private Money totalAmount;           // Value Object

    public void addItem(Product product, int quantity) {
        // 불변식(Invariant) 검증
        // 비즈니스 규칙 적용
        orderLines.add(new OrderLine(product, quantity));
        recalculateTotal();
    }
}
```

## 실행 방법

```bash
# 모듈 빌드
./gradlew :lab-architecture-patterns:build

# 테스트 실행
./gradlew :lab-architecture-patterns:test

# 특정 패턴 테스트
./gradlew :lab-architecture-patterns:test --tests "*Strategy*"
```

## 학습 자료

- [Refactoring Guru - Design Patterns](https://refactoring.guru/design-patterns)
- [Martin Fowler - Patterns of Enterprise Application Architecture](https://martinfowler.com/eaaCatalog/)
- [Eric Evans - Domain-Driven Design](https://domainlanguage.com/ddd/)
- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
