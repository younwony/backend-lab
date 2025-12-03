# lab-common

> 여러 모듈에서 공유하는 공통 유틸리티 모듈

## 목적

다른 실험 모듈들에서 반복적으로 사용되는 코드를 한 곳에서 관리합니다.

## 주요 내용

### 유틸리티 클래스
- 문자열 처리 유틸리티
- 날짜/시간 유틸리티
- JSON 변환 헬퍼

### 공통 상수
- 환경 설정 상수
- 에러 코드 정의

### 공통 예외
- 커스텀 예외 클래스
- 예외 처리 유틸리티

## 사용 방법

다른 모듈에서 의존성 추가:

```groovy
// 다른 모듈의 build.gradle
dependencies {
    implementation project(':lab-common')
}
```

## 패키지 구조

```
src/main/java/dev/wony/backendlab/common/
├── util/           # 유틸리티 클래스
├── constant/       # 상수 정의
├── exception/      # 공통 예외
└── helper/         # 헬퍼 클래스
```

## 주의사항

- 이 모듈은 **Spring Boot 플러그인을 적용하지 않습니다** (라이브러리 모듈)
- 실행 가능한 애플리케이션이 아닌, 순수 라이브러리 역할
- 특정 도메인에 종속적인 코드는 이 모듈에 포함하지 않습니다
