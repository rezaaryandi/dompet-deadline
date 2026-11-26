#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
classes="$(mktemp -d)"
trap 'rm -rf "$classes"' EXIT
find src/main/java -name '*.java' -print0 | xargs -0 javac --release 11 -encoding UTF-8 -d "$classes"
javac --release 11 -encoding UTF-8 -cp "$classes" -d "$classes" src/test/java/id/dompetdeadline/support/MemoryRepository.java scripts/preview/*.java
java -cp "$classes" OfflineSmoke
if command -v xvfb-run >/dev/null; then
  xvfb-run -a -s '-screen 0 1440x960x24' java -Djava.util.prefs.userRoot="$classes/prefs" -cp "$classes" id.dompetdeadline.view.ModularSmoke
else
  java -Djava.util.prefs.userRoot="$classes/prefs" -cp "$classes" id.dompetdeadline.view.ModularSmoke
fi
