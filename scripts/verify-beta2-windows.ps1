# STRATUM-83 / Beta 2 Windows acceptance runner
# Run from any folder; works on Windows PowerShell 5.1 and PowerShell 7.
# Does not merge, publish, push, modify releases, or collect private data.
[CmdletBinding()]
param(
    [switch]$SkipTests,
    [switch]$SkipGui
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$expectedBranch = 'feature/labyrinth-learning-lab'

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    throw 'Git is required to verify the tested commit.'
}
if (-not (Test-Path (Join-Path $root 'pom.xml'))) {
    throw 'Expected pom.xml next to the scripts directory. Run the script from the repository checkout.'
}

$branch = (& git -C $root branch --show-current).Trim()
$sha = (& git -C $root rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'Cannot determine repository commit.' }

Write-Host ''
Write-Host 'STRATUM-83 / BETA 2 WINDOWS ACCEPTANCE'
Write-Host ('Branch: ' + $branch)
Write-Host ('Commit: ' + $sha)
if ($branch -ne $expectedBranch) {
    throw ('Wrong branch. Run git switch ' + $expectedBranch + ' and git pull --ff-only first.')
}

$java = Get-Command java -ErrorAction SilentlyContinue
if (-not $java) {
    throw 'Java was not found. Install Java 21 and add it to PATH.'
}

$mavenRelative = Join-Path (Split-Path -Parent $root) 'apache-maven\bin\mvn.cmd'
if (Test-Path $mavenRelative) {
    $maven = $mavenRelative
} else {
    $installed = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if (-not $installed) { $installed = Get-Command mvn -ErrorAction SilentlyContinue }
    if (-not $installed) {
        throw 'Maven was not found. Install Maven or put apache-maven next to the project directory.'
    }
    $maven = $installed.Source
}

$testStatus = 'SKIPPED'
if (-not $SkipTests) {
    Write-Host ''
    Write-Host 'Running Maven/JUnit tests...'
    Push-Location $root
    try {
        & $maven --batch-mode --no-transfer-progress clean test
        if ($LASTEXITCODE -eq 0) { $testStatus = 'PASS' }
        else { $testStatus = 'FAIL' }
    } finally {
        Pop-Location
    }
}

$guiStatus = 'SKIPPED'
if (-not $SkipGui) {
    Write-Host ''
    Write-Host 'The JavaFX window will open. Check the features listed in docs/TESTING.md.'
    Write-Host 'Close the STRATUM-83 window to return here and record your results.'
    Push-Location $root
    try {
        & $maven --batch-mode --no-transfer-progress javafx:run
        if ($LASTEXITCODE -eq 0) { $guiStatus = 'LAUNCHED AND CLOSED (interactive checks follow)' }
        else { $guiStatus = 'FAILED TO RUN' }
    } finally {
        Pop-Location
    }
}

$checks = @(
    [pscustomobject]@{ Key = 'Main menu'; Question = 'Three main menu entries, command help, and navigation work' },
    [pscustomobject]@{ Key = 'Grid Zero Play'; Question = 'X/O remain visible and normal Minimax gameplay works' },
    [pscustomobject]@{ Key = 'Grid Zero Trace'; Question = 'TRACE shows real candidate scores and PREVIOUS/NEXT steps' },
    [pscustomobject]@{ Key = 'Grid Zero Explain/Apply'; Question = 'EXPLAIN is readable; APPLY correctly picks bounded retries' },
    [pscustomobject]@{ Key = 'Labyrinth Play'; Question = 'Human navigation, move count, retry and goal results work' },
    [pscustomobject]@{ Key = 'Labyrinth Trace'; Question = 'BFS/Dijkstra/A* show current, frontier, explored and final path' },
    [pscustomobject]@{ Key = 'Labyrinth Explain/Apply'; Question = 'EXPLAIN works; APPLY picks weighted two-hop cost 4' },
    [pscustomobject]@{ Key = 'Preserved game state'; Question = 'Returning from TRACE/EXPLAIN/APPLY preserves maze position and moves' },
    [pscustomobject]@{ Key = 'Research Lab'; Question = 'Reproducible Minimax and maze benchmark reports work' },
    [pscustomobject]@{ Key = 'Responsive layout'; Question = 'At 680x600, screens scroll and remain readable; F1/F2/Esc work' }
)

$answers = @()
foreach ($check in $checks) {
    Write-Host ''
    Write-Host ($check.Key + ': ' + $check.Question)
    do {
        $answer = (Read-Host 'Enter PASS, FAIL or SKIP').Trim().ToUpperInvariant()
    } until ($answer -in @('PASS', 'FAIL', 'SKIP'))
    $answers += [pscustomobject]@{ Key = $check.Key; Status = $answer }
}

$notes = Read-Host 'Optional issue notes (one line, no secrets)'
$notes = $notes.Replace('|', '/').Replace('`', "'")
$reportDir = Join-Path $root 'reports'
New-Item -ItemType Directory -Force -Path $reportDir | Out-Null
$report = Join-Path $reportDir 'BETA2-WINDOWS-ACCEPTANCE.md'

$allPass = ($testStatus -eq 'PASS' -and $guiStatus -eq 'LAUNCHED AND CLOSED (interactive checks follow)' -and
    @($answers | Where-Object { $_.Status -ne 'PASS' }).Count -eq 0)
$overall = if ($allPass) { 'PASS (reviewed locally)' } else { 'INCOMPLETE OR ISSUES FOUND' }

$lines = @(
    '# STRATUM-83 v0.3.0 Beta 2 / Windows acceptance',
    '',
    ('- Date: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')),
    ('- Branch: ' + $branch),
    ('- Commit: ' + $sha),
    ('- Maven/JUnit: ' + $testStatus),
    ('- JavaFX startup: ' + $guiStatus),
    ('- Overall: ' + $overall),
    '',
    '| Check | Result |',
    '| --- | --- |'
)
foreach ($answer in $answers) {
    $lines += ('| ' + $answer.Key + ' | ' + $answer.Status + ' |')
}
$lines += @(
    '',
    '## Tester notes',
    '',
    $(if ($notes) { $notes } else { 'None provided.' }),
    '',
    'This is a manual acceptance report, not an automated UI test.',
    'Do not tag or publish a new release until the maintainer reviews every failure.',
    ''
)
[System.IO.File]::WriteAllLines($report, $lines, ([System.Text.UTF8Encoding]::new($false)))

Write-Host ''
Write-Host ('Report saved: ' + $report)
Write-Host ('Result: ' + $overall)
Write-Host 'Review the report before uploading it to an issue or publishing a release.'
