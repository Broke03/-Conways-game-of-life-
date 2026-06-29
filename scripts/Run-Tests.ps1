$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$javaHome = "C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.1\jbr"
$java = Join-Path $javaHome "bin\java.exe"
$javac = Join-Path $javaHome "bin\javac.exe"
$depsDir = Join-Path $projectRoot ".deps"
$buildDir = Join-Path $projectRoot "build"
$mainClassesDir = Join-Path $buildDir "classes\main"
$testClassesDir = Join-Path $buildDir "classes\test"
$reportsDir = Join-Path $buildDir "reports"
$coverageHtmlDir = Join-Path $reportsDir "jacoco-html"
$coverageXmlFile = Join-Path $reportsDir "jacoco.xml"
$executionFile = Join-Path $buildDir "jacoco.exec"

$junitJar = Join-Path $depsDir "junit-platform-console-standalone-1.10.2.jar"
$jacocoAgentJar = Join-Path $depsDir "org.jacoco.agent-0.8.11-runtime.jar"
$jacocoCliJar = Join-Path $depsDir "org.jacoco.cli-0.8.11-nodeps.jar"

New-Item -ItemType Directory -Force -Path $depsDir | Out-Null
New-Item -ItemType Directory -Force -Path $mainClassesDir | Out-Null
New-Item -ItemType Directory -Force -Path $testClassesDir | Out-Null
New-Item -ItemType Directory -Force -Path $reportsDir | Out-Null

if (-not (Test-Path $junitJar)) {
    Invoke-WebRequest "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar" -OutFile $junitJar
}

if (-not (Test-Path $jacocoAgentJar)) {
    Invoke-WebRequest "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.11/org.jacoco.agent-0.8.11-runtime.jar" -OutFile $jacocoAgentJar
}

if (-not (Test-Path $jacocoCliJar)) {
    Invoke-WebRequest "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.cli/0.8.11/org.jacoco.cli-0.8.11-nodeps.jar" -OutFile $jacocoCliJar
}

$mainSources = Get-ChildItem (Join-Path $projectRoot "src\main\java") -Recurse -Filter *.java | ForEach-Object { $_.FullName }
$testSources = Get-ChildItem (Join-Path $projectRoot "src\test\java") -Recurse -Filter *.java | ForEach-Object { $_.FullName }

if (-not $mainSources) {
    throw "No main source files found."
}

if (-not $testSources) {
    throw "No test source files found."
}

& $javac -d $mainClassesDir $mainSources
if ($LASTEXITCODE -ne 0) {
    throw "Main source compilation failed."
}

& $javac -cp "$mainClassesDir;$junitJar" -d $testClassesDir $testSources
if ($LASTEXITCODE -ne 0) {
    throw "Test source compilation failed."
}

if (Test-Path $executionFile) {
    Remove-Item $executionFile -Force
}

& $java "-javaagent:$jacocoAgentJar=destfile=$executionFile" -jar $junitJar execute --class-path "$mainClassesDir;$testClassesDir" --scan-class-path
if ($LASTEXITCODE -ne 0) {
    throw "JUnit execution failed."
}

$coverageClassDirs = @(
    (Join-Path $mainClassesDir "life\model"),
    (Join-Path $mainClassesDir "life\clock"),
    (Join-Path $mainClassesDir "life\controller")
)

$jacocoArguments = @("report", $executionFile)
foreach ($coverageDir in $coverageClassDirs) {
    $jacocoArguments += @("--classfiles", $coverageDir)
}
$jacocoArguments += @(
    "--sourcefiles", (Join-Path $projectRoot "src\main\java"),
    "--html", $coverageHtmlDir,
    "--xml", $coverageXmlFile
)

& $java -jar $jacocoCliJar @jacocoArguments
if ($LASTEXITCODE -ne 0) {
    throw "JaCoCo report generation failed."
}

[xml]$xml = Get-Content $coverageXmlFile
$counters = @{}
foreach ($counter in $xml.report.counter) {
    $counters[$counter.type] = $counter
}

$lineCounter = $counters["LINE"]
$branchCounter = $counters["BRANCH"]
$lineCovered = [int]$lineCounter.covered
$lineMissed = [int]$lineCounter.missed
$branchCovered = [int]$branchCounter.covered
$branchMissed = [int]$branchCounter.missed

$linePercent = if (($lineCovered + $lineMissed) -eq 0) { 100 } else { [math]::Round(($lineCovered / ($lineCovered + $lineMissed)) * 100, 2) }
$branchPercent = if (($branchCovered + $branchMissed) -eq 0) { 100 } else { [math]::Round(($branchCovered / ($branchCovered + $branchMissed)) * 100, 2) }

Write-Host ""
Write-Host "Coverage summary"
Write-Host "Line coverage:   $linePercent% ($lineCovered covered / $lineMissed missed)"
Write-Host "Branch coverage: $branchPercent% ($branchCovered covered / $branchMissed missed)"
Write-Host ""
Write-Host "HTML report: $coverageHtmlDir"
