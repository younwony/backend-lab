#!/bin/bash
# Hook 1: 위험 명령 차단 (PreToolUse -> Bash)
# rm -rf /, git push --force, git reset --hard, DROP DATABASE 등 차단

# stdin에서 JSON 입력 읽기
INPUT=$(cat)

# tool_input.command 추출
COMMAND=$(echo "$INPUT" | jq -r '.tool_input.command // empty')

if [ -z "$COMMAND" ]; then
  exit 0
fi

# 위험 명령 패턴 목록
DANGEROUS_PATTERNS=(
  "rm -rf /"
  "rm -rf /*"
  "rm -rf ~"
  "git push --force"
  "git push -f"
  "git reset --hard"
  "git clean -f"
  "git checkout ."
  "git restore ."
  "DROP DATABASE"
  "DROP TABLE"
  "TRUNCATE TABLE"
  "format c:"
  "del /f /s /q"
  ":(){:|:&};:"
  "mkfs."
  "dd if=/dev/"
  "> /dev/sda"
)

# 명령어를 소문자로 변환하여 비교
COMMAND_LOWER=$(echo "$COMMAND" | tr '[:upper:]' '[:lower:]')

for PATTERN in "${DANGEROUS_PATTERNS[@]}"; do
  PATTERN_LOWER=$(echo "$PATTERN" | tr '[:upper:]' '[:lower:]')
  if echo "$COMMAND_LOWER" | grep -qF "$PATTERN_LOWER"; then
    echo '{"decision":"block","reason":"위험 명령이 감지되었습니다: '"$PATTERN"'. 이 명령은 시스템에 치명적인 영향을 줄 수 있어 차단됩니다."}'
    exit 0
  fi
done

# 위험하지 않으면 아무 출력 없이 종료
exit 0
