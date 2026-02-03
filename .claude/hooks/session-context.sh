#!/bin/bash
# Hook 6: 세션 시작 컨텍스트 (SessionStart)
# 현재 브랜치, 최근 커밋 5건, 수정 중인 파일 목록 표시

PROJECT_ROOT="C:/workspace/intellij/backend-lab"

cd "$PROJECT_ROOT" 2>/dev/null || exit 0

echo "=== Backend-Lab 프로젝트 상태 ==="
echo ""

# 현재 브랜치
BRANCH=$(git branch --show-current 2>/dev/null)
echo "브랜치: ${BRANCH:-알 수 없음}"
echo ""

# 최근 커밋 5건
echo "--- 최근 커밋 ---"
git log --oneline -5 2>/dev/null || echo "(커밋 정보를 가져올 수 없습니다)"
echo ""

# 수정 중인 파일 목록
MODIFIED=$(git status --short 2>/dev/null)
if [ -n "$MODIFIED" ]; then
  echo "--- 수정 중인 파일 ---"
  echo "$MODIFIED"
else
  echo "--- 수정 중인 파일 없음 ---"
fi
echo ""
echo "================================="
