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
$expectedBranch = 'feature/clean-games-algorithm-library'

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
    [pscustomobject]@{ Key = 'Two main views'; Question = 'GAMES and ALGORITHMS tabs work; no Research Lab, duel, or trace screens in navigation' },
    [pscustomobject]@{ Key = 'Catalog'; Question = 'Six named games, only Grid Zero and Labyrinth enabled; others clearly disabled' },
    [pscustomobject]@{ Key = 'Grid Zero human vs computer'; Question = 'Human plays X, computer automatically replies O using Minimax' },
    [pscustomobject]@{ Key = 'Grid Zero rendering'; Question = 'X/O marks remain visible, the game ends correctly, and NEW GAME resets it' },
    [pscustomobject]@{ Key = 'Labyrinth arrows'; Question = 'Arrow keys move P; clicking maze cells does not move the human' },
    [pscustomobject]@{ Key = 'Fog and sight'; Question = 'Most maze initially dark; nearby walls visible; corridors beyond walls hidden' },
    [pscustomobject]@{ Key = 'Exploration memory'; Question = 'New corridors reveal while walking; explored areas stay dim; hidden exit stays concealed' },
    [pscustomobject]@{ Key = 'Human vs A* result'; Question = 'After reaching G, the app shows human steps and A* shortest steps; only then allows showing the A* route' },
    [pscustomobject]@{ Key = 'Algorithms view'; Question = 'Short plain-language entries cover Minimax, alpha-beta, DFS, BFS, Dijkstra, A* and fog-of-war visibility' },
    [pscustomobject]@{ Key = 'Navigation and layout'; Question = '680x600 window remains usable; ESC opens games, F2 toggles sound; retry/new maze work' }
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
