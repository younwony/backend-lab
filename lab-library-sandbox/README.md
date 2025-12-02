# lab-library-sandbox

> 새로운 라이브러리 검증, PoC(Proof of Concept), 성능 벤치마크 모듈

## 목적

프로젝트에 도입하기 전, 새로운 라이브러리를 미리 테스트하고 검증합니다. 라이브러리 간 비교 분석과 성능 측정을 수행합니다.

## 주요 실험 주제

### 객체 매핑
- **MapStruct** vs **ModelMapper** 성능 비교
- 복잡한 객체 변환 패턴
- 커스텀 매퍼 구현

### JSON 처리
- **Jackson** vs **Gson** 비교
- JSON 직렬화/역직렬화 성능
- 커스텀 직렬화 전략

### 유틸리티 라이브러리
- **Guava** 컬렉션 유틸리티
- **Apache Commons** 활용
- **Lombok** 고급 기능

### 검증 라이브러리
- **Hibernate Validator** (Bean Validation)
- **AssertJ** 풍부한 단언문
- **Awaitility** 비동기 테스트

### 캐싱
- **Caffeine** 로컬 캐시
- **Redis** 분산 캐시
- 캐시 전략 비교

## 의존성 예시

```groovy
dependencies {
    implementation project(':lab-common')

    // MapStruct
    implementation 'org.mapstruct:mapstruct:1.5.5.Final'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.5.Final'

    // Guava
    implementation 'com.google.guava:guava:32.1.3-jre'

    // Caffeine Cache
    implementation 'com.github.ben-manes.caffeine:caffeine:3.1.8'

    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
}
```

## 패키지 구조

```
src/main/java/dev/wony/backendlab/sandbox/
├── mapping/             # 객체 매핑 실험
│   ├── mapstruct/
│   └── modelmapper/
├── json/                # JSON 처리 실험
├── cache/               # 캐싱 실험
├── validation/          # 검증 라이브러리
└── benchmark/           # 벤치마크 코드

src/test/java/dev/wony/backendlab/sandbox/
├── comparison/          # 라이브러리 비교 테스트
└── benchmark/           # 성능 측정 테스트
```

## 벤치마크 예시

### MapStruct vs ModelMapper

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MapperBenchmark {

    @Benchmark
    public UserDto mapStructMapping() {
        return userMapper.toDto(user);  // MapStruct
    }

    @Benchmark
    public UserDto modelMapperMapping() {
        return modelMapper.map(user, UserDto.class);  // ModelMapper
    }
}
```

### Caffeine Cache 설정

```java
Cache<String, User> cache = Caffeine.newBuilder()
    .maximumSize(10_000)
    .expireAfterWrite(Duration.ofMinutes(5))
    .recordStats()
    .build();
```

## 실험 진행 방식

1. **가설 설정**: 어떤 라이브러리가 더 적합한지 가설 수립
2. **PoC 구현**: 실제 사용 시나리오에 맞는 코드 작성
3. **테스트 작성**: 기능 검증 테스트 및 벤치마크
4. **결과 문서화**: 결과를 README 또는 별도 문서로 정리
5. **의사결정**: 프로젝트 도입 여부 결정

## 실행 방법

```bash
# 모듈 빌드
./gradlew :lab-library-sandbox:build

# 테스트 실행
./gradlew :lab-library-sandbox:test

# 특정 벤치마크 실행
./gradlew :lab-library-sandbox:test --tests "*Benchmark*"
```

## 실험 결과 기록

각 실험 결과는 `docs/` 디렉토리에 마크다운으로 정리합니다:

```
docs/
├── mapstruct-vs-modelmapper.md
├── json-serialization-benchmark.md
└── cache-strategy-comparison.md
```
