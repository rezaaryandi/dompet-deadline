$ErrorActionPreference = 'Stop'
Set-Location (Join-Path $PSScriptRoot '..')
if (-not (Get-Command javac -ErrorAction SilentlyContinue)) { throw 'Pasang JDK 11+ (disarankan JDK 21), lalu atur PATH dan JAVA_HOME.' }
$mvn = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if ($mvn) { $maven = $mvn.Source } else {
    $version = '3.9.9'
    $tools = Join-Path (Get-Location) '.tools'
    $maven = Join-Path $tools "apache-maven-$version\bin\mvn.cmd"
    if (-not (Test-Path $maven)) {
        New-Item -ItemType Directory -Force $tools | Out-Null
        $zip = Join-Path $tools "apache-maven-$version-bin.zip"
        $url = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$version/apache-maven-$version-bin.zip"
        Write-Host 'Mengunduh Maven dari Maven Central (butuh internet)...'
        Invoke-WebRequest -Uri $url -OutFile $zip
        $expected = ((Invoke-RestMethod "$url.sha512").ToString().Trim() -split '\s+')[0].ToLowerInvariant()
        $actual = (Get-FileHash $zip -Algorithm SHA512).Hash.ToLowerInvariant()
        if ($actual -ne $expected) { Remove-Item $zip; throw 'Checksum Maven tidak cocok.' }
        Expand-Archive -Path $zip -DestinationPath $tools -Force
        Remove-Item $zip
    }
}
& $maven -B clean verify
if ($LASTEXITCODE -ne 0) { throw 'Build atau unit test gagal. Lihat pesan Maven.' }
Write-Host 'Build berhasil. JAR: target\DompetDeadline.jar'
