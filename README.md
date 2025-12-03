# Backend Lab

> API 테스트, 라이브러리 검증, 아키텍처 패턴 테스트 등 '프로젝트라기엔 작고, 코드라기엔 복잡한' 것들을 모아둔 실험실입니다.

## 프로젝트 소개

이 프로젝트는 **Gradle 멀티 모듈** 구조로 구성되어 있습니다. 각 모듈은 독립적인 실험 공간으로, 서로 다른 주제의 코드를 깔끔하게 분리하여 관리할 수 있습니다.

### 왜 멀티 모듈인가?

- **관심사 분리**: API 테스트, 라이브러리 PoC, 아키텍처 패턴 등 성격이 다른 코드를 명확히 구분
- **의존성 격리**: 각 모듈별로 필요한 의존성만 추가하여 충돌 방지
- **빌드 최적화**: 변경된 모듈만 재빌드하여 시간 절약
- **설계 역량 향상**: 멀티 모듈 구조 자체가 실무에서 자주 사용되는 패턴

## 모듈 구조

```
backend-lab/
├── build.gradle                    # 루트 빌드 설정 (공통 설정)
├── settings.gradle                 # 서브 모듈 등록
│
├── common/                         # 공통 유틸리티 모듈
│   ├── build.gradle
│   └── src/
│
├── api-test/                       # API 테스트 모듈
│   ├── build.gradle
│   └── src/
│
├── library-sandbox/                # 라이브러리 샌드박스 모듈
│   ├── build.gradle
│   └── src/
│
└── architecture-patterns/          # 아키텍처 패턴 모듈
    ├── build.gradle
    └── src/
```

## 모듈 설명

| 모듈 | 설명 | 주요 용도 |
|------|------|----------|
| [common](./common/README.md) | 공통 유틸리티 | 여러 모듈에서 공유하는 헬퍼, 유틸리티 클래스 |
| [api-test](./api-test/README.md) | API 테스트 | 외부 API 호출, REST Client 테스트, HTTP 통신 패턴 |
| [library-sandbox](./library-sandbox/README.md) | 라이브러리 샌드박스 | 새로운 라이브러리 검증, PoC, 벤치마크 |
| [architecture-patterns](./architecture-patterns/README.md) | 아키텍처 패턴 | 디자인 패턴, 아키텍처 패턴, DDD 실험 |

## 기술 스택

- **Java 17**
- **Spring Boot 4.0.0**
- **Gradle** (멀티 모듈)
- **JUnit 5** (테스트)

## 시작하기

### 요구사항

- JDK 17 이상
- Gradle 8.x (또는 Gradle Wrapper 사용)

### 빌드

```bash
# 전체 프로젝트 빌드
./gradlew build

# 특정 모듈만 빌드
./gradlew :api-test:build

# 테스트 실행
./gradlew test

# 특정 모듈 테스트
./gradlew :library-sandbox:test
```

### IntelliJ에서 열기

1. `File > Open` 선택
2. `backend-lab` 폴더 선택
3. `Open as Project` 선택
4. Gradle import 완료 대기

## 새 모듈 추가하기

1. 모듈 디렉토리 생성:
```bash
mkdir -p new-module/src/main/java/dev/wony/backendlab/newmodule
mkdir -p new-module/src/test/java/dev/wony/backendlab/newmodule
```

2. `new-module/build.gradle` 작성:
```groovy
plugins {
    id 'org.springframework.boot'
}

dependencies {
    implementation project(':common')
    // 필요한 의존성 추가
}
```

3. `settings.gradle`에 모듈 등록:
```groovy
include 'new-module'
```

4. Gradle 새로고침

## 라이선스

MIT License
