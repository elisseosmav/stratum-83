# STRATUM-83 v0.3.0 Beta 2 — Release Candidate

> **Not yet published.** These notes describe the proposed `v0.3.0-beta.2` release, sourced from [feature/terminal-research-redesign](https://github.com/elisseosmav/stratum-83/tree/feature/terminal-research-redesign).  
> Do not rewrite or move the published `v0.3.0-beta.1` tag.

STRATUM-83 is an offline, non-commercial Java research terminal with original retro styling, playable games and educational classical-AI experiments.

## What's new since Beta 1

### Simplified main menu

The three primary entries are now **GRID ZERO**, **LABYRINTH**, and **RESEARCH LAB**. `HELP` and `EXIT` are terminal commands, not main-menu buttons. `DUEL` starts local two-player Grid Zero. `PLAY` starts Grid Zero against Minimax.

### Play and Research are separate

**Labyrinth Play** is now a playable maze only: move with arrow keys or mouse, reach G and generate new seeded mazes.

**Research Lab** has two result-only screens—no interactive maze in the benchmark:
- **Minimax vs Random:** configurable signed integer seed, 1–2000 games, wins/draws/losses.
- **Pathfinding Benchmark:** configurable starting seed, 1–100 maze samples, BFS/Dijkstra/A* average shortest steps and average explored nodes.

Both modes are deterministic with identical inputs and code. All algorithms use the same existing Java game and maze engines.

### Fixes retained

- Grid Zero X/O labels are drawn separately from buttons and remain visible after moves (Issue #10).
- Experiment reports use JavaFX `Text` to avoid multiline `Label` ellipsis truncation.
- Numbered menu shortcuts and full text-command navigation.
- Updated documentation, test checklist and Wiki-ready educational materials.

## Requirements

- Java JDK 21
- Apache Maven 3.9+
- Windows desktop for the primary supported testing target

## Build and run (source distribution)

```powershell
git clone --branch feature/terminal-research-redesign https://github.com/elisseosmav/stratum-83.git stratum-83-research
cd stratum-83-research
mvn clean test
mvn javafx:run
```

If Maven is not on PATH, use your `mvn.cmd` installation path. An Internet connection may be required for the first Maven build; the application itself runs offline.

## Verification status

- **Checked locally:** New benchmark core compiled using OpenJDK 21; repeated seeded shortest-path comparisons were consistent.
- **Not yet verified for this candidate:** full Maven/JUnit execution and Windows JavaFX GUI acceptance testing.
- **Not included:** packaged Windows installer, binary assets, cloud functionality or telemetry.

## Documentation and feedback

- [README and setup](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/README.md)
- [Technical design](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/docs/DESIGN.md)
- [Windows acceptance checks](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/docs/TESTING.md)
- [Development Roadmap](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/docs/DEVELOPMENT-ROADMAP.md)
- [Wiki-ready educational pages](https://github.com/elisseosmav/stratum-83/tree/feature/terminal-research-redesign/docs/wiki)
- [GitHub Discussions](https://github.com/elisseosmav/stratum-83/discussions)

When Windows tests pass, the maintainer may create a **new prerelease** with tag `v0.3.0-beta.2` targeting the reviewed commit. Do **not** overwrite `v0.3.0-beta.1`.
