# Publishing STRATUM-83 v0.3.0-beta.2

**This document is a maintainer checklist. Publishing a release or modifying the GitHub Wiki requires the repository owner's own GitHub credentials. Neither action is performed by committing this document.**

## 1. Verify the candidate

Use [TESTING.md](TESTING.md) to run the full Windows Maven/JUnit and JavaFX checks. Resolve failures before publication.

## 2. Review the changes

[Compare the candidate with main](https://github.com/elisseosmav/stratum-83/compare/main...feature/terminal-research-redesign)

The release candidate includes the Grid Zero X/O fix, Labyrinth, result-only Research Lab and documentation. `main` is not modified until the owner chooses to merge.

## 3. Create a new GitHub prerelease

Open [New release](https://github.com/elisseosmav/stratum-83/releases/new), then:

- Tag: `v0.3.0-beta.2` (create new).
- Target branch: `feature/terminal-research-redesign` (or an explicitly reviewed merge commit).
- Title: `STRATUM-83 v0.3.0 Beta 2`.
- Release label: **Pre-release**.
- Release notes: paste [RELEASE-NOTES-v0.3.0-beta.2.md](RELEASE-NOTES-v0.3.0-beta.2.md).
- Do not claim a compiled Windows `.exe` or attach an unverified installer.

**Do not change the existing `v0.3.0-beta.1` tag or its historical release.** GitHub automatically attaches source ZIP/tarball archives.

Alternative for a maintainer with [GitHub CLI](https://cli.github.com/) installed and authenticated:

```powershell
gh release create v0.3.0-beta.2 --repo elisseosmav/stratum-83 --target feature/terminal-research-redesign --prerelease --title "STRATUM-83 v0.3.0 Beta 2" --notes-file docs/RELEASE-NOTES-v0.3.0-beta.2.md
```

Run that command **from the reviewed repository checkout after Windows testing**. Do not run it until release approval.

## 4. Update the public GitHub Wiki

The Wiki is a separate Git repository. Editing files in `docs/wiki/` does not update it automatically.

1. Open [the Wiki](https://github.com/elisseosmav/stratum-83/wiki).
2. Copy the corresponding Markdown from [docs/wiki](wiki) into the matching Wiki pages.
3. Update **Home** and the existing **Grid Zero — Understanding the AI Experiment** article rather than creating duplicates.
4. Create **Research Lab**, **Labyrinth**, and **Development Roadmap** pages if absent.
5. Check the Wiki's actual page slugs and correct the Home navigation links.
6. Announce the new prerelease in [Discussions](https://github.com/elisseosmav/stratum-83/discussions) if desired.

## 5. Release status and code governance

- Public viewing, downloading and commenting do not grant code-write access.
- Official code merges and releases remain the maintainer's responsibility.
- Keep v0.3.0-beta.2 marked prerelease until tests pass and stability is confirmed.
