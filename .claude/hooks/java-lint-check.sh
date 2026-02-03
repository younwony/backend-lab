#!/bin/bash
# Hook 3: Java 코드 규칙 검사 - 패턴 매칭 린트 (PostToolUse -> Edit|Write)
# CLAUDE.md 기반 8가지 규칙 검사

# stdin에서 JSON 입력 읽기
INPUT=$(cat)

# 수정 대상 파일 경로 추출
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // .tool_input.filePath // empty')

if [ -z "$FILE_PATH" ]; then
  exit 0
fi

# Java 파일이 아니면 스킵
if [[ "$FILE_PATH" != *.java ]]; then
  exit 0
fi

# 파일 존재 확인
if [ ! -f "$FILE_PATH" ]; then
  exit 0
fi

VIOLATIONS=""

# 1. System.out.println 사용 금지
if grep -n "System\.out\.println\|System\.err\.println\|System\.out\.print(" "$FILE_PATH" 2>/dev/null | grep -v "^.*//.*System\.out" | head -3 | grep -q .; then
  LINES=$(grep -n "System\.out\.println\|System\.err\.println\|System\.out\.print(" "$FILE_PATH" | head -3)
  VIOLATIONS="${VIOLATIONS}\n[SLF4J] System.out.println 사용 금지 - SLF4J 로거를 사용하세요:\n${LINES}"
fi

# 2. @Data 사용 지양
if grep -n "@Data" "$FILE_PATH" 2>/dev/null | grep -v "^.*//.*@Data" | head -3 | grep -q .; then
  LINES=$(grep -n "@Data" "$FILE_PATH" | head -3)
  VIOLATIONS="${VIOLATIONS}\n[Lombok] @Data 사용 지양 - @Getter, @Builder 조합을 권장합니다:\n${LINES}"
fi

# 3. FetchType.EAGER 금지
if grep -n "FetchType\.EAGER" "$FILE_PATH" 2>/dev/null | head -3 | grep -q .; then
  LINES=$(grep -n "FetchType\.EAGER" "$FILE_PATH" | head -3)
  VIOLATIONS="${VIOLATIONS}\n[JPA] FetchType.EAGER 금지 - LAZY 로딩을 사용하세요:\n${LINES}"
fi

# 4. catch(Exception) 금지 - 구체적 예외를 사용해야 함
if grep -n "catch\s*(Exception " "$FILE_PATH" 2>/dev/null | head -3 | grep -q .; then
  LINES=$(grep -n "catch\s*(Exception " "$FILE_PATH" | head -3)
  VIOLATIONS="${VIOLATIONS}\n[예외처리] catch(Exception) 금지 - 구체적인 예외 클래스를 사용하세요:\n${LINES}"
fi

# 5. 직접 null/empty 체크 금지 - StringUtils, CollectionUtils 사용 권장
if grep -n '!= null && !.*\.isEmpty()\|!= null && .*\.length()\|!= null && .*\.size()' "$FILE_PATH" 2>/dev/null | head -3 | grep -q .; then
  LINES=$(grep -n '!= null && !.*\.isEmpty()\|!= null && .*\.length()\|!= null && .*\.size()' "$FILE_PATH" | head -3)
  VIOLATIONS="${VIOLATIONS}\n[유틸리티] 직접 null/empty 체크 금지 - StringUtils.hasText(), CollectionUtils.isEmpty() 등을 사용하세요:\n${LINES}"
fi

# 6. 반복문 내 String += 금지
if grep -n '+= "' "$FILE_PATH" 2>/dev/null | head -3 | grep -q .; then
  # String 변수에 += 사용하는 패턴 탐지
  LINES=$(grep -n '+= "' "$FILE_PATH" | head -3)
  VIOLATIONS="${VIOLATIONS}\n[성능] 반복문 내 String += 금지 - StringBuilder를 사용하세요 (해당 라인이 반복문 내부인지 확인 필요):\n${LINES}"
fi

# 7. isPresent()+get() 패턴 금지
if grep -n '\.isPresent()' "$FILE_PATH" 2>/dev/null | head -3 | grep -q .; then
  LINES=$(grep -n '\.isPresent()' "$FILE_PATH" | head -3)
  VIOLATIONS="${VIOLATIONS}\n[Optional] isPresent()+get() 패턴 금지 - orElseThrow(), ifPresent() 등을 사용하세요:\n${LINES}"
fi

# 8. Optional 필드 사용 금지
if grep -n 'private Optional<\|protected Optional<\|Optional<.*>;' "$FILE_PATH" 2>/dev/null | grep -v "return\|public\|static" | head -3 | grep -q .; then
  LINES=$(grep -n 'private Optional<\|protected Optional<' "$FILE_PATH" | head -3)
  if [ -n "$LINES" ]; then
    VIOLATIONS="${VIOLATIONS}\n[Optional] Optional을 필드로 사용 금지 - 반환 타입으로만 사용하세요:\n${LINES}"
  fi
fi

# 위반 사항이 있으면 피드백 출력
if [ -n "$VIOLATIONS" ]; then
  echo -e "Java 코드 규칙 위반이 감지되었습니다 ($FILE_PATH):${VIOLATIONS}\n\n위 규칙은 CLAUDE.md에 정의된 코딩 표준입니다. 수정을 권장합니다."
fi

exit 0
