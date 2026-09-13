#!/usr/bin/env bash
set -e

REPO_ROOT="$(git rev-parse --show-toplevel)"
cd "$REPO_ROOT"

echo "🔍 Running pre-commit validation..."

# 1. Check for staged Kotlin files
STAGED_KT_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep '\.kt$' || true)

if [ -n "$STAGED_KT_FILES" ]; then
    echo "• Validating Kotlin imports and syntax on staged files..."
    python3 "$REPO_ROOT/scripts/validate_kotlin.py" $STAGED_KT_FILES
else
    echo "• No staged Kotlin files to validate."
fi

# 2. Check if JDK is available for local compilation / unit tests
JAVA_BIN=""
if command -v java >/dev/null 2>&1; then
    JAVA_BIN="$(command -v java)"
elif [ -d "$REPO_ROOT/.jdks" ]; then
    FOUND_JDK=$(find "$REPO_ROOT/.jdks" -maxdepth 3 -name "java" -type f -executable | head -1 || true)
    if [ -n "$FOUND_JDK" ]; then
        JAVA_BIN="$FOUND_JDK"
        export JAVA_HOME="$(dirname "$(dirname "$FOUND_JDK")")"
        export PATH="$JAVA_HOME/bin:$PATH"
    fi
elif [ -d "$HOME/.jdks" ]; then
    FOUND_JDK=$(find "$HOME/.jdks" -maxdepth 3 -name "java" -type f -executable | head -1 || true)
    if [ -n "$FOUND_JDK" ]; then
        JAVA_BIN="$FOUND_JDK"
        export JAVA_HOME="$(dirname "$(dirname "$FOUND_JDK")")"
        export PATH="$JAVA_HOME/bin:$PATH"
    fi
fi

# 3. Check if Android SDK and JDK are available for local compilation / unit tests
HAS_ANDROID_SDK=false
if [ -n "$ANDROID_HOME" ] && [ -d "$ANDROID_HOME" ]; then
    HAS_ANDROID_SDK=true
elif [ -n "$ANDROID_SDK_ROOT" ] && [ -d "$ANDROID_SDK_ROOT" ]; then
    HAS_ANDROID_SDK=true
elif [ -f "$REPO_ROOT/local.properties" ] && grep -q '^sdk\.dir=' "$REPO_ROOT/local.properties"; then
    HAS_ANDROID_SDK=true
fi

if [ "$HAS_ANDROID_SDK" = true ] && [ -n "$JAVA_BIN" ] && "$JAVA_BIN" -version >/dev/null 2>&1 && [ -f "$REPO_ROOT/gradlew" ]; then
    echo "• Running local unit tests with Gradle..."
    export GRADLE_USER_HOME="${GRADLE_USER_HOME:-$REPO_ROOT/.gradle}"
    ./gradlew testDebugUnitTest --no-daemon -q
    echo "✅ Local unit tests passed!"
else
    if [ "$HAS_ANDROID_SDK" = false ]; then
        echo "ℹ️ Note: Android SDK not configured locally (ANDROID_HOME or local.properties not found)."
        echo "   Static Kotlin validation passed! GitHub Actions CI will execute full unit tests and APK quality gates."
    else
        echo "ℹ️ Note: No operational local JDK found. Static Kotlin validation passed!"
    fi
fi

echo "✅ Pre-commit checks completed successfully."
exit 0

