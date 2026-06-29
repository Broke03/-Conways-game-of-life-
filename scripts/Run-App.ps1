$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$javaHome = "C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.1\jbr"
$java = Join-Path $javaHome "bin\java.exe"
$javac = Join-Path $javaHome "bin\javac.exe"
$buildDir = Join-Path $projectRoot "build"
$classesDir = Join-Path $buildDir "classes\main"

New-Item -ItemType Directory -Force -Path $classesDir | Out-Null

$mainSources = Get-ChildItem (Join-Path $projectRoot "src\main\java") -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if (-not $mainSources) {
    throw "No Java source files found in src/main/java."
}

& $javac -d $classesDir $mainSources
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed."
}

& $java -cp $classesDir life.main.Main
if ($LASTEXITCODE -ne 0) {
    throw "Application exited with an error."
}
