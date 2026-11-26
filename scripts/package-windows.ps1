$ErrorActionPreference='Stop'
Set-Location (Join-Path $PSScriptRoot '..')
if (-not (Get-Command jpackage -ErrorAction SilentlyContinue)) { throw 'Gunakan JDK 17+ yang memiliki jpackage.' }
if (-not (Test-Path 'target/DompetDeadline.jar')) { & "$PSScriptRoot/build-windows.ps1" }
New-Item -ItemType Directory -Force 'dist/input','dist/packages' | Out-Null
Copy-Item 'target/DompetDeadline.jar' 'dist/input/DompetDeadline.jar' -Force
if (Test-Path 'dist/packages/DompetDeadline') { Remove-Item 'dist/packages/DompetDeadline' -Recurse -Force }
& jpackage --type app-image --name DompetDeadline --app-version 1.0.0 --input 'dist/input' --main-jar DompetDeadline.jar --main-class id.dompetdeadline.Main --dest 'dist/packages' --vendor 'Dompet Deadline' --add-modules java.se,java.desktop,java.sql,java.naming,jdk.crypto.ec
if ($LASTEXITCODE -ne 0) { throw 'jpackage gagal.' }
Compress-Archive -Path 'dist/packages/DompetDeadline' -DestinationPath 'dist/DompetDeadline-Windows.zip' -Force
Write-Host 'Paket Windows portable: dist/DompetDeadline-Windows.zip'
