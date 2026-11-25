# Publish selected STRATUM-83 GitHub Wiki pages from a preserved source snapshot.
# Personal project: review the diff yourself before publication.
# Requires Git, Internet access, and write permission for the GitHub Wiki.
[CmdletBinding()]
param()
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    throw 'Git is required to publish Wiki changes.'
}

# Immutable commit made before redundant docs were removed from the repository.
# This remains available through Git history even after file deletion.
$snapshot = '648c7d0789f2f352bd6e283144988a32e338510e'
$wikiRepo = 'https://github.com/elisseosmav/stratum-83.wiki.git'
$workingPath = Join-Path ([System.IO.Path]::GetTempPath()) (
    'stratum83-wiki-' + [Guid]::NewGuid().ToString('N'))
$wikiPath = Join-Path $workingPath 'wiki'
New-Item -ItemType Directory -Force -Path $workingPath | Out-Null

Write-Host 'Getting the existing GitHub Wiki...'
& git clone --quiet $wikiRepo $wikiPath
if ($LASTEXITCODE -ne 0) {
    throw 'Cannot clone the Wiki. Check GitHub Wiki access and credentials.'
}

# Current gameplay and concise algorithm explanations only.
# Historical Research Lab material is intentionally not republished.
$pages = @(
    'Home.md',
    '2.-Development-Roadmap.md',
    'Grid-Zero-AI-Experiment.md',
    'Labyrinth.md'
)
$matched = @()
$missing = @()

foreach ($page in $pages) {
    $existing = Join-Path $wikiPath $page
    if (-not (Test-Path $existing)) {
        $missing += $page
        continue
    }

    $url = 'https://raw.githubusercontent.com/elisseosmav/stratum-83/' +
        $snapshot + '/docs/wiki/' + $page
    $download = Join-Path $workingPath $page
    try {
        Invoke-WebRequest -Uri $url -OutFile $download -UseBasicParsing
    } catch {
        throw ('Unable to read the preserved source for ' + $page +
            '. No Wiki changes have been pushed.')
    }
    $body = [System.IO.File]::ReadAllText($download)
    # Remove historical notes referring to staged repository files.
    $body = [regex]::Replace($body,
        '(?m)^\*This is Wiki-ready Markdown in the repository\. Updating it does not automatically modify GitHub''s live Wiki\.\*\s*$',
        '')
    $body = [regex]::Replace($body,
        '(?m)^\*This is a Wiki-ready source page, not an automatically updated live GitHub Wiki article\.\*\s*$',
        '')
    $body = [regex]::Replace($body,
        '(?m)^\*This is a draft for the separate Wiki; it is not a live Wiki edit\.\*\s*$',
        '')
    $body = $body.Replace(
        'This is a Wiki-ready draft, not an automatically updated live page.', '')

    [System.IO.File]::WriteAllText($existing, $body,
        (New-Object System.Text.UTF8Encoding($false)))
    & git -C $wikiPath add -- $page
    if ($LASTEXITCODE -ne 0) { throw ('Unable to stage ' + $page) }
    $matched += $page
}

if ($matched.Count -eq 0) {
    Write-Warning 'No matching existing Wiki pages found. Nothing was modified or published.'
    Write-Host ('Wiki clone: ' + $wikiPath)
    exit 0
}
if ($missing.Count -gt 0) {
    Write-Warning 'These page filenames were absent and have NOT been created:'
    $missing | ForEach-Object { Write-Host ('  ' + $_) }
}

& git -C $wikiPath diff --cached --check
if ($LASTEXITCODE -ne 0) { throw 'Invalid changes found in proposed Wiki update.' }
& git -C $wikiPath diff --cached --quiet
if ($LASTEXITCODE -eq 0) {
    Write-Host 'The existing Wiki pages already match the preserved content.'
    exit 0
}
if ($LASTEXITCODE -ne 1) { throw 'Unable to compare Wiki changes.' }

Write-Host ''
Write-Host 'Review ALL proposed Wiki edits below:'
& git -C $wikiPath --no-pager diff --cached --stat
& git -C $wikiPath --no-pager diff --cached
if ($LASTEXITCODE -ne 0) { throw 'Unable to display changes.' }

Write-Warning 'The existing page contents will be replaced. Keep any Wiki-only edits manually.'
Write-Host ('Unpublished Wiki clone: ' + $wikiPath)
$approval = Read-Host 'Type PUBLISH to apply these exact changes, or anything else to cancel'
if ($approval -cne 'PUBLISH') {
    Write-Host 'Cancelled; the live Wiki remains unchanged.'
    exit 0
}

& git -C $wikiPath commit -m 'wiki: concise current games and algorithms'
if ($LASTEXITCODE -ne 0) { throw 'Wiki commit failed; verify git name/email.' }
& git -C $wikiPath push
if ($LASTEXITCODE -ne 0) {
    throw ('Wiki push failed; your local clone is at: ' + $wikiPath)
}
Write-Host 'Wiki published: https://github.com/elisseosmav/stratum-83/wiki'
