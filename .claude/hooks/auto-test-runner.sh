#!/bin/bash
# Hook 5: 테스트 자동 실행 (PostToolUse -> Edit|Write, async)
# Java 파일 수정 시 해당 모듈의 gradlew test 자동 실행

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

# 테스트 파일 수정 시 스킵 (무한 루프 방지)
if [[ "$FILE_PATH" == *Test.java ]] || [[ "$FILE_PATH" == *Tests.java ]] || [[ "$FILE_PATH" == */test/* ]]; then
  exit 0
fi

# 프로젝트 루트 경로 탐색
PROJECT_ROOT="C:/workspace/intellij/backend-lab"

# 파일 경로에서 모듈명 추출
# 경로 형태: {project_root}/{module}/src/...
RELATIVE_PATH="${FILE_PATH#$PROJECT_ROOT/}"

# Windows 경로 대응
RELATIVE_PATH=$(echo "$RELATIVE_PATH" | sed 's|\\|/|g')

# 모듈명 추출 (첫 번째 디렉토리)
MODULE_NAME=$(echo "$RELATIVE_PATH" | cut -d'/' -f1)

# 알려진 모듈 목록 확인
KNOWN_MODULES=("common" "api-test" "library-sandbox" "architecture-patterns" "macro" "board" "claude-code" "antigravity" "spring-boot-4-features")

IS_KNOWN=false
for MOD in "${KNOWN_MODULES[@]}"; do
  if [ "$MODULE_NAME" = "$MOD" ]; then
    IS_KNOWN=true
    break
  fi
done

if [ "$IS_KNOWN" = false ]; then
  exit 0
fi

# Gradle Wrapper로 해당 모듈 테스트 실행
cd "$PROJECT_ROOT"

# Windows 환경 대응
if [ -f "gradlew.bat" ] && command -v cmd.exe &>/dev/null; then
  cmd.exe /c "cd /d $PROJECT_ROOT && gradlew.bat :${MODULE_NAME}:test --no-daemon" 2>&1
else
  ./gradlew ":${MODULE_NAME}:test" --no-daemon 2>&1
fi

TEST_EXIT_CODE=$?

if [ $TEST_EXIT_CODE -ne 0 ]; then
  echo "테스트 실패: :${MODULE_NAME}:test (exit code: $TEST_EXIT_CODE)"
  echo "수정한 파일: $FILE_PATH"
  echo "테스트를 확인하고 실패 원인을 수정해주세요."
fi

exit 0
