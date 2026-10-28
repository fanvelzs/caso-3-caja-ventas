$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
$candidates = @()
if ($env:JAVA_HOME) { $candidates += $env:JAVA_HOME }
$localJdks = Join-Path $env:USERPROFILE '.jdks'
if (Test-Path -LiteralPath $localJdks) {
    $candidates += Get-ChildItem -LiteralPath $localJdks -Directory | Select-Object -ExpandProperty FullName
}
$selectedJdk = $null
foreach ($candidate in $candidates) {
    $releaseFile = Join-Path $candidate 'release'
    if ((Test-Path -LiteralPath (Join-Path $candidate 'bin/javac.exe')) -and (Test-Path -LiteralPath $releaseFile)) {
        if ((Get-Content -LiteralPath $releaseFile -Raw) -match 'JAVA_VERSION="21[."]') {
            $selectedJdk = $candidate
            break
        }
    }
}
if ($selectedJdk) { $env:JAVA_HOME = $selectedJdk }
else { Write-Host 'No se detecto JDK 21. Configure JAVA_HOME con su JDK 21.'; exit 1 }
Write-Host "Usando JDK: $selectedJdk"
& (Join-Path $PSScriptRoot 'mvnw.cmd') 'javafx:run'
exit $LASTEXITCODE
