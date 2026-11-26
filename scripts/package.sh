#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [[ ! -f target/DompetDeadline.jar ]]; then mvn -B clean verify; fi
mkdir -p dist/input dist/packages
cp target/DompetDeadline.jar dist/input/
# jpackage app-image must be built on the target OS; clean old image first.
rm -rf dist/packages/DompetDeadline dist/packages/DompetDeadline.app
jpackage --type app-image --name DompetDeadline --app-version 1.0.0 --input dist/input --main-jar DompetDeadline.jar --main-class id.dompetdeadline.Main --dest dist/packages --vendor 'Dompet Deadline' --add-modules java.se,java.desktop,java.sql,java.naming,jdk.crypto.ec
