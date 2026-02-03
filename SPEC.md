# Backend-Lab 기능 명세서 (SPEC)

## Claude Code Hooks

### 기능 설명

Claude Code의 도구 사용 라이프사이클에 연결하여 코드 품질 관리, 안전 장치, 테스트 자동화를 수행하는 Hook 시스템입니다.

### Hook 별 기능 명세

#### Hook 1: 위험 명령 차단

| 항목 | 내용 |
|------|------|
| 이벤트 | PreToolUse |
| 대상 도구 | Bash |
| 입력 | `tool_input.command` |
| 출력 | `{"decision":"block","reason":"..."}` 또는 빈 출력 (허용) |

**차단 패턴:** `rm -rf /`, `git push --force`, `git reset --hard`, `DROP DATABASE`, `DROP TABLE`, `TRUNCATE TABLE` 등 18개 패턴

**처리 흐름:**

```
stdin (JSON) → command 추출 → 소문자 변환 → 패턴 매칭
  ├─ 매칭됨 → {"decision":"block"} 출력
  └─ 매칭 안됨 → 빈 출력 (허용)
```

#### Hook 2: 보호 파일 수정 방지

| 항목 | 내용 |
|------|------|
| 이벤트 | PreToolUse |
| 대상 도구 | Edit, Write |
| 입력 | `tool_input.file_path` |
| 출력 | `{"decision":"ask","reason":"..."}` 또는 빈 출력 (허용) |

**보호 패턴:** `.env`, `gradlew`, `gradle/wrapper`, `.gitignore`, `settings.gradle`, `.git/`, `credentials`, `secrets` 등 9개 패턴

#### Hook 3: Java 코드 규칙 검사

| 항목 | 내용 |
|------|------|
| 이벤트 | PostToolUse |
| 대상 도구 | Edit, Write |
| 입력 | `tool_input.file_path` (*.java만 처리) |
| 출력 | 위반 사항 텍스트 (Claude에게 피드백) |

**검사 규칙 8가지:**

```
┌────┬──────────────────────────────┬──────────────────────────────┐
│ #  │ 금지 패턴                     │ 권장 대안                     │
├────┼──────────────────────────────┼──────────────────────────────┤
│ 1  │ System.out.println           │ SLF4J Logger                 │
│ 2  │ @Data                        │ @Getter + @Builder           │
│ 3  │ FetchType.EAGER              │ FetchType.LAZY               │
│ 4  │ catch(Exception)             │ 구체적 예외 클래스             │
│ 5  │ != null && !.isEmpty()       │ StringUtils.hasText()        │
│ 6  │ += " (String 연결)           │ StringBuilder                │
│ 7  │ .isPresent() + .get()        │ orElseThrow(), ifPresent()   │
│ 8  │ private Optional<>           │ 반환 타입으로만 사용           │
└────┴──────────────────────────────┴──────────────────────────────┘
```

#### Hook 4: LLM 코드 리뷰

| 항목 | 내용 |
|------|------|
| 이벤트 | PostToolUse |
| 대상 도구 | Edit, Write |
| 유형 | `type: "prompt"` (스크립트 없음) |
| 동작 | Claude 자체 아키텍처 리뷰 |

**리뷰 관점:** SOLID 원칙, Entity 노출, N+1 쿼리, @Transactional, 단일 책임

#### Hook 5: 테스트 자동 실행

| 항목 | 내용 |
|------|------|
| 이벤트 | PostToolUse |
| 대상 도구 | Edit, Write |
| 실행 모드 | async (백그라운드) |
| 타임아웃 | 120초 |

**처리 흐름:**

```
파일 경로 추출 → Java 파일 확인 → 테스트 파일 스킵
  → 모듈명 추출 → 알려진 모듈 확인
  → gradlew :모듈명:test --no-daemon 실행
```

**무한 루프 방지:** `*Test.java`, `*Tests.java`, `/test/` 경로 파일은 스킵

#### Hook 6: 세션 시작 컨텍스트

| 항목 | 내용 |
|------|------|
| 이벤트 | SessionStart |
| 동작 | 프로젝트 상태 정보 출력 |

**출력 정보:** 현재 브랜치, 최근 커밋 5건, 수정 중인 파일 목록

### 설정 구조

```
settings.local.json
└── hooks
    ├── PreToolUse
    │   ├── Bash → block-dangerous-commands.sh
    │   └── Edit|Write → protect-files.sh
    ├── PostToolUse
    │   └── Edit|Write
    │       ├── java-lint-check.sh (command)
    │       ├── LLM 리뷰 (prompt)
    │       └── auto-test-runner.sh (command, async)
    └── SessionStart
        └── session-context.sh
```

---

*Last Updated: 2026-02-03*
