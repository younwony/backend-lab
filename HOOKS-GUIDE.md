# Claude Code Hooks 가이드

Backend-Lab 프로젝트에 설정된 Claude Code Hooks 설명서입니다.

## 개요

Claude Code Hooks는 Claude의 도구 사용 전/후에 자동으로 실행되는 스크립트입니다.
코드 품질, 안전성, 생산성을 자동으로 관리합니다.

## Hook 목록

### 1. 위험 명령 차단 (PreToolUse → Bash)

| 항목 | 내용 |
|------|------|
| 파일 | `.claude/hooks/block-dangerous-commands.sh` |
| 트리거 | Bash 도구 사용 전 |
| 동작 | `decision: "block"` 반환으로 실행 차단 |

**차단 대상 명령:**

```
rm -rf /          git push --force     git reset --hard
git clean -f      git checkout .       git restore .
DROP DATABASE     DROP TABLE           TRUNCATE TABLE
format c:         del /f /s /q         mkfs.
dd if=/dev/       > /dev/sda           :(){:|:&};:
```

### 2. 보호 파일 수정 방지 (PreToolUse → Edit|Write)

| 항목 | 내용 |
|------|------|
| 파일 | `.claude/hooks/protect-files.sh` |
| 트리거 | Edit/Write 도구 사용 전 |
| 동작 | `decision: "ask"` 반환으로 사용자 확인 요청 |

**보호 대상 파일:**

```
.env              gradlew             gradlew.bat
gradle/wrapper    .gitignore          settings.gradle
.git/             credentials         secrets
```

### 3. Java 코드 규칙 검사 (PostToolUse → Edit|Write)

| 항목 | 내용 |
|------|------|
| 파일 | `.claude/hooks/java-lint-check.sh` |
| 트리거 | Java 파일 수정 후 |
| 동작 | 위반 사항 피드백 출력 |

**검사 규칙 (CLAUDE.md 기반):**

| # | 규칙 | 대안 |
|---|------|------|
| 1 | `System.out.println` 사용 금지 | SLF4J 로거 사용 |
| 2 | `@Data` 사용 지양 | `@Getter` + `@Builder` 조합 |
| 3 | `FetchType.EAGER` 금지 | `FetchType.LAZY` 사용 |
| 4 | `catch(Exception)` 금지 | 구체적 예외 클래스 사용 |
| 5 | 직접 null/empty 체크 금지 | `StringUtils.hasText()` 등 |
| 6 | 반복문 내 `String +=` 금지 | `StringBuilder` 사용 |
| 7 | `isPresent()+get()` 패턴 금지 | `orElseThrow()`, `ifPresent()` |
| 8 | `Optional` 필드 사용 금지 | 반환 타입으로만 사용 |

### 4. LLM 코드 리뷰 (PostToolUse → Edit|Write)

| 항목 | 내용 |
|------|------|
| 설정 | `settings.local.json` 내 `type: "prompt"` |
| 트리거 | Edit/Write 도구 사용 후 |
| 동작 | Claude가 아키텍처 수준 자체 리뷰 수행 |

**리뷰 관점:**

1. SOLID 원칙 위반 여부
2. Entity의 API 레이어 직접 노출 여부
3. N+1 쿼리 가능성
4. `@Transactional(readOnly=true)` 누락 여부
5. 메서드 단일 책임 원칙 준수 여부

### 5. 테스트 자동 실행 (PostToolUse → Edit|Write, async)

| 항목 | 내용 |
|------|------|
| 파일 | `.claude/hooks/auto-test-runner.sh` |
| 트리거 | Java 소스 파일 수정 후 |
| 동작 | 해당 모듈의 `gradlew :모듈명:test` 백그라운드 실행 |
| 타임아웃 | 120초 |

**특이사항:**
- `Test.java`, `Tests.java`, `/test/` 경로의 파일은 스킵 (무한 루프 방지)
- `async: true`로 백그라운드 실행
- Windows 환경에서 `gradlew.bat` 자동 감지

**지원 모듈:**

```
common, api-test, library-sandbox, architecture-patterns,
macro, board, claude-code, antigravity, spring-boot-4-features
```

### 6. 세션 시작 컨텍스트 (SessionStart)

| 항목 | 내용 |
|------|------|
| 파일 | `.claude/hooks/session-context.sh` |
| 트리거 | Claude Code 세션 시작 시 |
| 동작 | 프로젝트 현재 상태 표시 |

**출력 정보:**
- 현재 Git 브랜치
- 최근 커밋 5건
- 수정 중인 파일 목록

## 파일 구조

```
.claude/
  settings.local.json              # hooks 설정 포함
  hooks/
    block-dangerous-commands.sh    # Hook 1: 위험 명령 차단
    protect-files.sh               # Hook 2: 보호 파일 방지
    java-lint-check.sh             # Hook 3: Java 린트 검사
    auto-test-runner.sh            # Hook 5: 테스트 자동 실행
    session-context.sh             # Hook 6: 세션 컨텍스트
```

## Hook 실행 흐름

```
세션 시작
  └─ SessionStart → session-context.sh (프로젝트 상태 표시)

Bash 명령 실행 시
  └─ PreToolUse → block-dangerous-commands.sh
       ├─ 위험 명령 → 차단 (block)
       └─ 안전한 명령 → 실행 허용

파일 수정 시 (Edit/Write)
  ├─ PreToolUse → protect-files.sh
  │    ├─ 보호 파일 → 사용자 확인 (ask)
  │    └─ 일반 파일 → 실행 허용
  └─ PostToolUse
       ├─ java-lint-check.sh (패턴 매칭 린트)
       ├─ LLM 코드 리뷰 (prompt)
       └─ auto-test-runner.sh (비동기 테스트)
```

## 검증 방법

| Hook | 테스트 방법 | 예상 결과 |
|------|-----------|----------|
| 위험 명령 | Claude에게 `rm -rf /` 요청 | 차단 메시지 |
| 보호 파일 | `.env` 수정 요청 | 사용자 확인 프롬프트 |
| 린트 | `System.out.println` 포함 코드 작성 | 경고 피드백 |
| LLM 리뷰 | Java 코드 수정 | 아키텍처 리뷰 출력 |
| 테스트 | common 모듈 Java 파일 수정 | `:common:test` 자동 실행 |
| 세션 | 새 세션 시작 | 프로젝트 상태 표시 |

## 커스터마이징

### 위험 명령 추가

`block-dangerous-commands.sh`의 `DANGEROUS_PATTERNS` 배열에 패턴 추가:

```bash
DANGEROUS_PATTERNS=(
  # 기존 패턴...
  "새로운 위험 명령"
)
```

### 보호 파일 추가

`protect-files.sh`의 `PROTECTED_PATTERNS` 배열에 패턴 추가:

```bash
PROTECTED_PATTERNS=(
  # 기존 패턴...
  "새로운 보호 파일 패턴"
)
```

### 린트 규칙 추가

`java-lint-check.sh`에 grep 패턴 추가:

```bash
# 새 규칙
if grep -n "금지 패턴" "$FILE_PATH" 2>/dev/null | head -3 | grep -q .; then
  LINES=$(grep -n "금지 패턴" "$FILE_PATH" | head -3)
  VIOLATIONS="${VIOLATIONS}\n[카테고리] 규칙 설명:\n${LINES}"
fi
```

### 모듈 추가

`auto-test-runner.sh`의 `KNOWN_MODULES` 배열에 모듈명 추가:

```bash
KNOWN_MODULES=("common" "api-test" ... "새모듈")
```
