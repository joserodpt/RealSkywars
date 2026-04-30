#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MODULE_DIR="$PROJECT_DIR/realskywars-plugin"
TARGET_DIR="/Users/jose/Desktop/dev/plugins"

if ! command -v mvn >/dev/null 2>&1; then
  echo "Error: Maven is not installed. Install it with: brew install maven"
  exit 1
fi

cd "$PROJECT_DIR"

VERSION="$(mvn -f "$MODULE_DIR/pom.xml" help:evaluate -Dexpression=project.version -q -DforceStdout | tail -n 1)"
if [[ -z "$VERSION" ]]; then
  echo "Error: Could not read plugin version from realskywars-plugin/pom.xml"
  exit 1
fi

JAR_NAME="RealSkywarsPlugin-${VERSION}.jar"

echo "Building RealSkywarsPlugin..."
mvn -pl realskywars-plugin -am clean package

SOURCE_JAR="$MODULE_DIR/target/$JAR_NAME"
if [[ ! -f "$SOURCE_JAR" ]]; then
  SOURCE_JAR="$(ls -t "$MODULE_DIR"/target/RealSkywarsPlugin-*.jar 2>/dev/null | grep -v '/original-' | head -n 1 || true)"
  if [[ -z "$SOURCE_JAR" || ! -f "$SOURCE_JAR" ]]; then
    echo "Error: Built jar not found in $MODULE_DIR/target"
    exit 1
  fi
  JAR_NAME="$(basename "$SOURCE_JAR")"
fi

mkdir -p "$TARGET_DIR"
cp "$SOURCE_JAR" "$TARGET_DIR/"

echo "Done: Copied $JAR_NAME to $TARGET_DIR"
