# Sync existing STRATUM-83 Wiki pages from the latest approved development docs.
# PowerShell 5.1+; requires Git and the owner's GitHub Wiki write access.
# This script only stages matching existing pages; it never creates new Wiki pages.
# A complete staged diff and explicit PUBLISH confirmation are required.
[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    throw 'Git is required to update the Wiki.'
}

$repo = 'https://github.com/elisseosmav/stratum-83.git'
$wikiRepo = 'https://github.com/elisseosmav/stratum-83.wiki.git'
$branch = 'feature/clean-games-algorithm-library'
$workspace = Join-Path ([System.IO.Path]::GetTempPath()) ('stratum83-wiki-sync-' + [Guid]::NewGuid().ToString('N'))
$sourcePath = Join-Path $workspace 'source'
$wikiPath = Join-Path $workspace 'wiki'

New-Item -ItemType Directory -Force -Path $workspace | Out-Null
Write-Host 'Cloning the candidate source and the existing Wiki...'
& git clone --quiet --depth 1 --branch $branch $repo $sourcePath
if ($LASTEXITCODE -ne 0) { throw 'Could not clone the development branch.' }
& git clone --quiet $wikiRepo $wikiPath
if ($LASTEXITCODE -ne 0) { throw 'Could not clone the Wiki. Check credentials and Wiki availability.' }

$head = (& git -C $sourcePath rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'Cannot identify candidate commit.' }
Write-Host ('Candidate commit: ' + $head)

$pages = @(
    'Home.md',
    '2.-Development-Roadmap.md',
    'Labyrinth.md',
    'Grid-Zero-AI-Experiment.md',
    'Research-Lab.md'
)

$updated = @()
$missing = @()
foreach ($page in $pages) {
    $sourceFile = Join-Path (Join-Path $sourcePath 'docs\wiki') $page
    $wikiFile = Join-Path $wikiPath $page
    if (-not (Test-Path $sourceFile)) {
        throw ('Missing source draft: ' + $sourceFile)
    }
    if (-not (Test-Path $wikiFile)) {
        $missing += $page
        continue
    }

    $body = [System.IO.File]::ReadAllText($sourceFile)
    # Remove the repository-only publication disclaimers; keep the product/release warnings.
    $body = [regex]::Replace($body,
        '(?m)^\*This is Wiki-ready Markdown in the repository\. Updating it does not automatically modify GitHub''s live Wiki\.\*\s*$',
        '')
    $body = [regex]::Replace($body,
        '(?m)^\*This is a Wiki-ready source page, not an automatically updated live GitHub Wiki article\.\*\s*$',
        '')
    $body = [regex]::Replace($body,
        '(?m)^\*This is a draft for the separate Wiki; it is not a live Wiki edit\.\*\s*$',
        '')
    $body = $body.Replace('This is a Wiki-ready draft, not an automatically updated live page.', '')
    [System.IO.File]::WriteAllText($wikiFile, $body,
        (New-Object System.Text.UTF8Encoding($false)))
    & git -C $wikiPath add -- $page
    if ($LASTEXITCODE -ne 0) { throw ('Staging failed for ' + $page) }
    $updated += $page
}

Write-Host ''
Write-Host 'Pages checked for synchronization:'
$updated | ForEach-Object { Write-Host ('  - ' + $_) }
if ($missing.Count -gt 0) {
    Write-Host ''
    Write-Warning 'The following Wiki page filenames do not exist, so no replacement was attempted:'
    $missing | ForEach-Object { Write-Host ('  - ' + $_) }
}

& git -C $wikiPath diff --cached --check
if ($LASTEXITCODE -ne 0) { throw 'Wiki change whitespace validation failed.' }
& git -C $wikiPath diff --cached --quiet
if ($LASTEXITCODE -eq 0) {
    Write-Host 'No Wiki content differs at these known page paths.'
    exit 0
}
if ($LASTEXITCODE -ne 1) { throw 'Cannot inspect the Wiki staged changes.' }

Write-Host ''
Write-Host 'REVIEW THESE CHANGES CAREFULLY BEFORE PUBLISHING:'
& git -C $wikiPath --no-pager diff --cached --stat
& git -C $wikiPath --no-pager diff --cached
if ($LASTEXITCODE -ne 0) { throw 'Could not display the Wiki diff.' }

Write-Host ''
Write-Warning 'This will replace the CONTENTS of the listed existing Wiki pages.'
Write-Warning 'Custom Wiki-only edits must be manually retained before publication.'
Write-Host ('Wiki working checkout: ' + $wikiPath)
$approval = Read-Host 'Type PUBLISH to commit and push the reviewed Wiki changes, or anything else to cancel'
if ($approval -cne 'PUBLISH') {
    Write-Host 'Cancelled. No Wiki push performed.'
    exit 0
}

& git -C $wikiPath commit -m 'docs(wiki): synchronize clean Games + Algorithms and fog-of-war status'
if ($LASTEXITCODE -ne 0) { throw 'Wiki commit failed. Check git user.name and git user.email.' }
& git -C $wikiPath push
if ($LASTEXITCODE -ne 0) {
    throw ('Wiki push failed. Local checkout retained at: ' + $wikiPath)
}
Write-Host 'Wiki synchronization complete: https://github.com/elisseosmav/stratum-83/wiki'
Write-Host ('Updated from candidate commit: ' + $head)
