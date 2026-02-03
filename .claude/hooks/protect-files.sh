#!/bin/bash
# Hook 2: 보호 파일 수정 방지 (PreToolUse -> Edit|Write)
# .env, gradlew, gradle/wrapper, .gitignore, settings.gradle 등 보호

# stdin에서 JSON 입력 읽기
INPUT=$(cat)

# 수정 대상 파일 경로 추출
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // .tool_input.filePath // empty')

if [ -z "$FILE_PATH" ]; then
  exit 0
fi

# 보호 파일 패턴 목록
PROTECTED_PATTERNS=(
  ".env"
  "gradlew"
  "gradlew.bat"
  "gradle/wrapper"
  ".gitignore"
  "settings.gradle"
  ".git/"
  "credentials"
  "secrets"
)

for PATTERN in "${PROTECTED_PATTERNS[@]}"; do
  if echo "$FILE_PATH" | grep -qF "$PATTERN"; then
    echo '{"decision":"ask","reason":"보호 대상 파일입니다: '"$FILE_PATH"'. 이 파일은 프로젝트 핵심 설정 파일로, 수정 시 주의가 필요합니다."}'
    exit 0
  fi
done

# 보호 대상이 아니면 아무 출력 없이 종료
exit 0
