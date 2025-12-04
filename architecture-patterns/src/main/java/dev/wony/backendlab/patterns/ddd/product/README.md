# 상품(Product) 도메인 - DDD 구현 가이드

## 개요

이 모듈은 Domain-Driven Design(DDD) 전술적 패턴을 적용하여 상품 도메인을 구현한 예제입니다.
실제 이커머스 시스템에서 사용할 수 있는 수준의 도메인 모델을 제공합니다.

---

## 패키지 구조

```
domain/
├── vo/                     # Value Objects (값 객체)
│   ├── ProductId.java      # 상품 식별자
│   ├── CategoryId.java     # 카테고리 식별자
│   ├── ProductName.java    # 상품명
│   ├── Money.java          # 금액
│   └── Quantity.java       # 수량
│
├── entity/                 # Entities (엔티티)
│   ├── Product.java        # 상품 (Aggregate Root)
│   ├── Category.java       # 카테고리
│   └── ProductStatus.java  # 상품 상태 열거형
│
├── event/                  # Domain Events (도메인 이벤트)
│   ├── DomainEvent.java           # 이벤트 인터페이스
│   ├── AbstractDomainEvent.java   # 이벤트 추상 클래스
│   ├── ProductCreatedEvent.java   # 상품 생성 이벤트
│   ├── ProductPriceChangedEvent.java   # 가격 변경 이벤트
│   └── ProductStockChangedEvent.java   # 재고 변경 이벤트
│
├── repository/             # Repository Interfaces
│   ├── ProductRepository.java     # 상품 저장소
│   └── CategoryRepository.java    # 카테고리 저장소
│
└── service/                # Domain Services (도메인 서비스)
    ├── ProductDomainService.java  # 상품 도메인 서비스
    └── PricingPolicy.java         # 가격 정책 (전략 패턴)
```

---

## 핵심 개념

### 1. Value Object (값 객체)

값 객체는 **식별자 없이 속성 값으로만 동등성을 판단**하는 불변 객체입니다.

```
┌─────────────────────────────────────────────────────────────┐
│                      Value Object 특징                       │
├─────────────────────────────────────────────────────────────┤
│  • 불변성 (Immutable)                                        │
│  • 값으로 동등성 비교 (equals/hashCode)                       │
│  • 자기 유효성 검증 (Self-Validation)                         │
│  • 부작용 없는 행위 (Side-Effect Free)                        │
└─────────────────────────────────────────────────────────────┘
```

#### Money 예시

```java
// 생성 - 팩토리 메서드로 유효성 검증
Money price = Money.of(10000);

// 연산 - 새로운 객체 반환 (불변성)
Money total = price.multiply(3);        // 30000원
Money discounted = total.subtract(Money.of(5000));  // 25000원

// 비교
price.isGreaterThan(Money.of(5000));    // true
price.isZero();                          // false
```

#### Quantity 예시

```java
Quantity stock = Quantity.of(100);
Quantity ordered = Quantity.of(30);

// 재고 확인
stock.isGreaterThanOrEqual(ordered);    // true

// 재고 차감 (새 객체 반환)
Quantity remaining = stock.subtract(ordered);  // 70개

// 재고 부족 시 예외
stock.subtract(Quantity.of(200));       // IllegalArgumentException
```

---

### 2. Entity (엔티티)

엔티티는 **고유 식별자로 구분**되며, 생명주기 동안 상태가 변할 수 있습니다.

```
┌─────────────────────────────────────────────────────────────┐
│                       Entity 특징                            │
├─────────────────────────────────────────────────────────────┤
│  • 고유 식별자 (Identity)                                    │
│  • 식별자로 동등성 비교                                       │
│  • 상태 변경 가능 (Mutable)                                  │
│  • 생명주기 존재                                              │
└─────────────────────────────────────────────────────────────┘
```

---

### 3. Aggregate & Aggregate Root

Aggregate는 **일관성 경계**를 정의하고, Aggregate Root는 **유일한 진입점**입니다.

```
┌─────────────────────────────────────────────────────────────┐
│                    Product Aggregate                         │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                                                       │  │
│  │   ┌─────────────────┐                                │  │
│  │   │    Product      │  ◄── Aggregate Root            │  │
│  │   │  (Aggregate     │                                │  │
│  │   │     Root)       │                                │  │
│  │   └────────┬────────┘                                │  │
│  │            │                                          │  │
│  │            │ contains                                 │  │
│  │            ▼                                          │  │
│  │   ┌─────────────────┐    ┌─────────────────┐        │  │
│  │   │  ProductName    │    │     Money       │        │  │
│  │   │  (Value Object) │    │ (Value Object)  │        │  │
│  │   └─────────────────┘    └─────────────────┘        │  │
│  │                                                       │  │
│  │   ┌─────────────────┐    ┌─────────────────┐        │  │
│  │   │   Quantity      │    │   CategoryId    │        │  │
│  │   │ (Value Object)  │    │ (Value Object)  │        │  │
│  │   └─────────────────┘    └─────────────────┘        │  │
│  │                                                       │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  외부에서는 반드시 Product(Root)를 통해서만 접근              │
└─────────────────────────────────────────────────────────────┘
```

#### Aggregate 규칙

1. **Root를 통한 접근**: 외부에서는 Aggregate Root만 참조
2. **트랜잭션 경계**: 하나의 트랜잭션에서 하나의 Aggregate만 수정
3. **일관성 보장**: Root가 내부 불변식(invariant) 보장

---

## 상품 생명주기

```
┌──────────┐     startSelling()     ┌──────────┐
│ PENDING  │ ─────────────────────► │ ON_SALE  │
│ (판매대기)│                        │ (판매중)  │
└──────────┘                        └────┬─────┘
                                         │
                    ┌────────────────────┼────────────────────┐
                    │                    │                    │
                    │ decreaseStock()    │ discontinue()      │
                    │ (재고 0)            │                    │
                    ▼                    │                    ▼
            ┌──────────────┐             │          ┌──────────────┐
            │ OUT_OF_STOCK │             │          │ DISCONTINUED │
            │   (품절)      │             │          │  (판매중지)   │
            └──────┬───────┘             │          └──────────────┘
                   │                     │
                   │ addStock()          │
                   │ (재고 추가)          │
                   └─────────────────────┘
```

---

## 주요 흐름

### 1. 상품 생성 흐름

```
┌─────────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│ Client  │      │   Product   │      │ Value       │      │   Domain    │
│         │      │  (Factory)  │      │ Objects     │      │   Event     │
└────┬────┘      └──────┬──────┘      └──────┬──────┘      └──────┬──────┘
     │                  │                    │                    │
     │ create(name,     │                    │                    │
     │  price, qty...)  │                    │                    │
     │─────────────────►│                    │                    │
     │                  │                    │                    │
     │                  │ validate & create  │                    │
     │                  │───────────────────►│                    │
     │                  │                    │                    │
     │                  │◄───────────────────│                    │
     │                  │                    │                    │
     │                  │ registerEvent()    │                    │
     │                  │───────────────────────────────────────►│
     │                  │                    │                    │
     │                  │                    │    ProductCreated  │
     │                  │◄───────────────────────────────────────│
     │                  │                    │                    │
     │◄─────────────────│                    │                    │
     │   Product        │                    │                    │
     │   (PENDING)      │                    │                    │
```

**코드 예시:**

```java
// 1. 상품 생성
Product product = Product.create(
    ProductName.of("맥북 프로 14인치"),
    "Apple M3 Pro 칩 탑재",
    Money.of(2_490_000),
    Quantity.of(50),
    categoryId
);

// 2. 생성 시 상태는 PENDING
assert product.getStatus() == ProductStatus.PENDING;

// 3. 도메인 이벤트 확인
List<DomainEvent> events = product.pullDomainEvents();
// → ProductCreatedEvent 발행됨
```

---

### 2. 판매 시작 흐름

```java
// 판매 시작 (PENDING → ON_SALE)
product.startSelling();

// 판매중 상태 확인
assert product.getStatus() == ProductStatus.ON_SALE;
assert product.canOrder(Quantity.of(10));  // 주문 가능
```

---

### 3. 재고 감소 흐름 (주문 처리)

```
┌─────────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│ Order   │      │   Product   │      │  Quantity   │      │   Domain    │
│ Service │      │             │      │             │      │   Event     │
└────┬────┘      └──────┬──────┘      └──────┬──────┘      └──────┬──────┘
     │                  │                    │                    │
     │ canOrder(qty)?   │                    │                    │
     │─────────────────►│                    │                    │
     │                  │ isGreaterThanOrEqual│                   │
     │                  │───────────────────►│                    │
     │◄─────────────────│◄───────────────────│                    │
     │   true           │                    │                    │
     │                  │                    │                    │
     │ decreaseStock()  │                    │                    │
     │─────────────────►│                    │                    │
     │                  │ subtract()         │                    │
     │                  │───────────────────►│                    │
     │                  │◄───────────────────│                    │
     │                  │                    │                    │
     │                  │ registerEvent()    │                    │
     │                  │───────────────────────────────────────►│
     │                  │                    │  StockChangedEvent │
     │◄─────────────────│◄───────────────────────────────────────│
     │   완료           │                    │                    │
```

**코드 예시:**

```java
// 1. 주문 가능 여부 확인
Quantity orderQty = Quantity.of(5);
if (product.canOrder(orderQty)) {

    // 2. 재고 차감
    product.decreaseStock(orderQty);

    // 3. 이벤트 발행됨
    List<DomainEvent> events = product.pullDomainEvents();
    // → ProductStockChangedEvent 발행
}

// 4. 재고가 0이 되면 자동으로 품절 처리
product.decreaseStock(product.getStockQuantity());
assert product.getStatus() == ProductStatus.OUT_OF_STOCK;
```

---

### 4. 가격 정책 적용 흐름

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   Client    │      │  Pricing    │      │   Product   │
│             │      │  Policy     │      │             │
└──────┬──────┘      └──────┬──────┘      └──────┬──────┘
       │                    │                    │
       │ calculatePrice()   │                    │
       │───────────────────►│                    │
       │                    │ getPrice()         │
       │                    │───────────────────►│
       │                    │◄───────────────────│
       │                    │    Money           │
       │                    │                    │
       │                    │ apply discount     │
       │                    │ logic              │
       │◄───────────────────│                    │
       │  discounted Money  │                    │
```

**코드 예시:**

```java
// 다양한 가격 정책 적용
PricingPolicy standard = PricingPolicy.standard();
PricingPolicy tenPercentOff = PricingPolicy.percentDiscount(10);
PricingPolicy bulkDiscount = PricingPolicy.bulkDiscount(10, 20);  // 10개 이상 20% 할인

Quantity qty = Quantity.of(15);

Money standardPrice = standard.calculatePrice(product, qty);
// → 37,350,000원 (정가)

Money discountedPrice = tenPercentOff.calculatePrice(product, qty);
// → 33,615,000원 (10% 할인)

Money bulkPrice = bulkDiscount.calculatePrice(product, qty);
// → 29,880,000원 (20% 대량 할인)
```

---

## 도메인 이벤트

도메인 이벤트는 **도메인에서 발생한 중요한 사건**을 나타냅니다.

```
┌─────────────────────────────────────────────────────────────┐
│                    Domain Event 흐름                         │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────┐    상태 변경    ┌──────────────┐              │
│  │ Product  │ ─────────────► │ Domain Event │              │
│  │          │                │  (수집됨)     │              │
│  └──────────┘                └───────┬──────┘              │
│                                      │                      │
│                                      │ pullDomainEvents()   │
│                                      ▼                      │
│                              ┌──────────────┐              │
│                              │ Application  │              │
│                              │   Service    │              │
│                              └───────┬──────┘              │
│                                      │                      │
│                                      │ publish              │
│                                      ▼                      │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │ Event       │    │ Notification│    │ Analytics   │    │
│  │ Store       │    │ Service     │    │ Service     │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 이벤트 종류

| 이벤트 | 발생 시점 | 포함 정보 |
|--------|----------|----------|
| `ProductCreatedEvent` | 상품 생성 시 | 상품ID, 상품명, 가격, 초기재고 |
| `ProductPriceChangedEvent` | 가격 변경 시 | 상품ID, 이전가격, 새가격 |
| `ProductStockChangedEvent` | 재고 변경 시 | 상품ID, 이전수량, 현재수량 |

---

## Repository 패턴

Repository는 **Aggregate의 영속성을 추상화**합니다.

```
┌─────────────────────────────────────────────────────────────┐
│                    Repository 구조                           │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Domain Layer                    Infrastructure Layer       │
│  ┌──────────────────┐           ┌──────────────────┐       │
│  │ <<interface>>    │           │ JpaProduct       │       │
│  │ ProductRepository│◄──────────│ Repository       │       │
│  │                  │ implements│                  │       │
│  │ + save()         │           │ + save()         │       │
│  │ + findById()     │           │ + findById()     │       │
│  │ + findByStatus() │           │ + findByStatus() │       │
│  └──────────────────┘           └──────────────────┘       │
│           ▲                              │                  │
│           │                              │                  │
│           │ uses                         │ uses             │
│           │                              ▼                  │
│  ┌──────────────────┐           ┌──────────────────┐       │
│  │ Domain Service   │           │ JPA / Database   │       │
│  │ Application Svc  │           │                  │       │
│  └──────────────────┘           └──────────────────┘       │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**인터페이스 정의 (Domain Layer):**

```java
public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(ProductId id);
    List<Product> findByStatus(ProductStatus status);
    // ...
}
```

---

## 통합 예시: 주문 처리

```java
@Service
@Transactional
public class OrderApplicationService {

    private final ProductRepository productRepository;
    private final ProductDomainService domainService;
    private final ApplicationEventPublisher eventPublisher;

    public void placeOrder(PlaceOrderCommand command) {
        // 1. 상품 조회
        Product product = productRepository.getById(command.productId());
        Quantity orderQty = Quantity.of(command.quantity());

        // 2. 주문 가능 여부 확인
        if (!product.canOrder(orderQty)) {
            throw new OrderException("주문 불가: 재고 부족 또는 판매 불가 상태");
        }

        // 3. 총 금액 계산
        Money totalPrice = domainService.calculateTotalPrice(product, orderQty);

        // 4. 재고 차감
        product.decreaseStock(orderQty);

        // 5. 저장
        productRepository.save(product);

        // 6. 도메인 이벤트 발행
        product.pullDomainEvents().forEach(eventPublisher::publishEvent);
    }
}
```

---

## 테스트 가이드

### Value Object 테스트

```java
@Test
void money_더하기_성공() {
    Money a = Money.of(1000);
    Money b = Money.of(500);

    Money result = a.add(b);

    assertEquals(Money.of(1500), result);
}
```

### Aggregate 테스트

```java
@Test
void 재고_감소시_품절_처리() {
    Product product = createSellableProduct(Quantity.of(10));

    product.decreaseStock(Quantity.of(10));

    assertEquals(ProductStatus.OUT_OF_STOCK, product.getStatus());
}
```

### Domain Service 테스트

```java
@Test
void 대량_주문_가능_여부_확인() {
    List<OrderItem> items = List.of(
        new OrderItem(product1, Quantity.of(5)),
        new OrderItem(product2, Quantity.of(10))
    );

    boolean canOrder = domainService.canOrderAll(items);

    assertTrue(canOrder);
}
```

---

## 확장 포인트

### 1. 새로운 가격 정책 추가

```java
// 시간대별 할인 정책
PricingPolicy timeBasedDiscount = (product, quantity) -> {
    LocalTime now = LocalTime.now();
    if (now.isAfter(LocalTime.of(22, 0))) {
        // 야간 10% 할인
        return PricingPolicy.percentDiscount(10)
            .calculatePrice(product, quantity);
    }
    return PricingPolicy.standard()
        .calculatePrice(product, quantity);
};
```

### 2. 새로운 도메인 이벤트 추가

```java
public class ProductDiscontinuedEvent extends AbstractDomainEvent {
    private final ProductId productId;
    private final String reason;
    // ...
}
```

### 3. 새로운 Value Object 추가

```java
public final class Sku {
    private final String value;

    public static Sku of(String value) {
        // SKU 형식 검증
        if (!isValidSkuFormat(value)) {
            throw new IllegalArgumentException("유효하지 않은 SKU 형식");
        }
        return new Sku(value);
    }
}
```

---

## 참고 자료

- Eric Evans, "Domain-Driven Design: Tackling Complexity in the Heart of Software"
- Vaughn Vernon, "Implementing Domain-Driven Design"
- Martin Fowler, "Patterns of Enterprise Application Architecture"
