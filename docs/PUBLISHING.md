# STRATUM-83 — Beta 2 Publishing Checklist

**The clean Games + Algorithms rebuild is an unpublished development candidate.** Publishing is a separate action the maintainer must explicitly approve after testing.

## 1. Test on Windows

Review [TESTING.md](TESTING.md). Run the full Java 21 Maven/JUnit suite and start the JavaFX app. Confirm that fog hides unexplored areas, walls block vision, arrow navigation works, and A* results are shown only at completion.

The guided script [verify-beta2-windows.ps1](../scripts/verify-beta2-windows.ps1) records a local manual acceptance report and never publishes anything.

## 2. Review the candidate branch

[Compare main with feature/clean-games-algorithm-library](https://github.com/elisseosmav/stratum-83/compare/main...feature/clean-games-algorithm-library).

This feature branch contains earlier unmerged work as well as the new rebuild. Examine the entire comparison before any merge.

## 3. Prepare a separate prerelease

Only after approval, create a **new** prerelease `v0.3.0-beta.2`, targeted at the reviewed commit, with title **STRATUM-83 v0.3.0 Beta 2** and notes from [the release notes](RELEASE-NOTES-v0.3.0-beta.2.md). Never move or modify the old Beta 1 tag. Do not claim a Windows installer unless one has been built and verified.

## 4. Publish Wiki changes separately

The live Wiki is an independent Git repository. The drafts under [docs/wiki](wiki) must be reviewed, then copied/pushed separately by an authorized maintainer. Updating these source files is **not** a live Wiki publication.

## 5. Governance

The repository owner controls merges, tags, releases and Wiki changes. Visitors can inspect the source and offer feedback through Issues/Discussions.
