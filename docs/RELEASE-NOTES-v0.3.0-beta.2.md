# STRATUM-83 v0.3.0 Beta 2 — Release Candidate

> **Not yet published.** These notes describe the proposed `v0.3.0-beta.2` release, sourced from [feature/retro-ai-game-catalog](https://github.com/elisseosmav/stratum-83/tree/feature/retro-ai-game-catalog).  
> Do not rewrite or move the published `v0.3.0-beta.1` tag.

STRATUM-83 is an offline, non-commercial Java research terminal with original retro styling, playable games and educational classical-AI experiments.

## New main menu: Retro AI Game Catalog

The default view is now **SELECT GAME**, with six classic or strategic game concepts and their associated fundamental AI algorithms. **Grid Zero** and **Labyrinth** are playable. **Crisis Protocol** is planned; **Checkers**, **Blackjack** and **Chess** are proposed and not scheduled. Unfinished titles are visibly disabled.

Research Lab and the existing experimental/educational source remain available by terminal commands, but do not occupy a game entry. Numeric commands match the catalog: 1–2 launch working games, while 3–6 explain that those games are not ready.

This is a user-interface change on an **unpublished candidate**. It does not imply that future games have been implemented or that Beta 2 has been released.

## New: explain real game moves

Grid Zero now records **every accepted move** and saves the Minimax analysis actually used for each computer reply. Choose **GAME HISTORY**, select the third human move (X), and the lesson reconstructs that exact board to show how the computer selected the fourth move (O).

The learner can try alternative O placements and step through hypothetical best-play continuations before revealing O's **actual recorded choice**. Hypothetical and real gameplay are explicitly distinguished. `EXPLAIN` opens the latest recorded computer move, while `HISTORY` or `TRACE` opens the list of real moves. Raw search events remain available via `ADVANCED`.

New pure-Java `GridZeroHistory` and `GridZeroMoveLesson` classes have JUnit tests validating replay, recorded decision integrity, legal alternative continuations and that lessons never alter the live game. Windows JavaFX visual acceptance remains pending.

## Educational direction: Play → Trace → Explain → Apply

**STRATUM-83 — Classic Games. Real Algorithms. Practical Engineering.**

Grid Zero now demonstrates its actual Minimax decision-making with interactive recorded search steps, candidate move scores, a readable explanation of alpha-beta pruning, and a self-contained hypothetical engineering worst-case policy exercise. The search trace and gameplay use **one shared Minimax algorithm**, with real counts and bounded event capture; no simulated fake search data.

Java 21 Maven/JUnit tests have [passed on Linux and Windows GitHub Actions](https://github.com/elisseosmav/stratum-83/actions/runs/45471437228). This does not replace a Windows **GUI** test.

### Labyrinth now teaches the algorithms

The Labyrinth Play screen now adds **TRACE / EXPLAIN / APPLY** beside navigation controls. TRACE visualizes real BFS, Dijkstra and A* node expansions, current/frontier/explored states, path costs and final shortest route. EXPLAIN details the algorithms and the fact that the current generated maze is unit-cost and loop-free. APPLY presents a fictional weighted software routing puzzle where a two-hop route can cost less than a direct edge. `MTRACE`, `MEXPLAIN`, `MAPPLY`, `BFS`, `DIJKSTRA` and `ASTAR` are available by terminal command.

Shared search routines, immutable snapshots and JUnit tests keep the explanation faithful to the real solver. The example uses hypothetical units; it makes no network performance claims. [Java 21 Maven/JUnit CI passed on Ubuntu and Windows](https://github.com/elisseosmav/stratum-83/actions/runs/45473389881); visual Windows JavaFX acceptance remains pending.

## What's new since Beta 1

### Simplified main menu

The three primary entries are now **GRID ZERO**, **LABYRINTH**, and **RESEARCH LAB**. `HELP` and `EXIT` are terminal commands, not main-menu buttons. `DUEL` starts local two-player Grid Zero. `PLAY` starts Grid Zero against Minimax.

### Play and Research are separate

**Labyrinth Play** is now a **human-versus-BFS challenge**: move with arrow keys or mouse, reach G and compare your successful steps with the shortest possible route. After completion, see extra moves and efficiency, reveal your visited cells or the optimal route, retry the same seed, or create a new maze. Paths remain hidden until completion.

**Research Lab** has two result-only screens—no interactive maze in the benchmark:
- **Minimax vs Random:** configurable signed integer seed, 1–2000 games, wins/draws/losses.
- **Pathfinding Benchmark:** configurable starting seed, 1–100 maze samples, BFS/Dijkstra/A* average shortest steps and average explored nodes.

Both modes are deterministic with identical inputs and code. All algorithms use the same existing Java game and maze engines.

### Fixes retained

- Grid Zero X/O labels are drawn separately from buttons and remain visible after moves (Issue #10).
- Experiment reports use JavaFX `Text` to avoid multiline `Label` ellipsis truncation.
- Numbered menu shortcuts and full text-command navigation.
- Human route scoring and Java 21 tests for valid/invalid steps, backtracking, route reveal state and same-seed retry.
- Updated documentation, test checklist and Wiki-ready educational materials.

## Requirements

- Java JDK 21
- Apache Maven 3.9+
- Windows desktop for the primary supported testing target

## Build and run (source distribution)

```powershell
git clone --branch feature/retro-ai-game-catalog https://github.com/elisseosmav/stratum-83.git stratum-83-research
cd stratum-83-research
mvn clean test
mvn javafx:run
```

If Maven is not on PATH, use your `mvn.cmd` installation path. An Internet connection may be required for the first Maven build; the application itself runs offline.

### Windows release acceptance

The project includes a repeatable manual acceptance runner, [verify-beta2-windows.ps1](../scripts/verify-beta2-windows.ps1). It executes local Maven tests, opens the JavaFX app and records the maintainer's checks for Play/Trace/Explain/Apply, reproducibility and resizing. **The helper's existence is not evidence that a Windows graphical test has passed.**

## Verification status

- **Checked locally:** New benchmark core compiled using OpenJDK 21; repeated seeded shortest-path comparisons were consistent.
- **Verified:** Java 21 Maven/JUnit CI on Ubuntu and Windows.
- **Not yet verified:** JavaFX graphical startup and interactive acceptance testing on a Windows desktop.
- **Not included:** packaged Windows installer, binary assets, cloud functionality or telemetry.

## Documentation and feedback

- [README and setup](https://github.com/elisseosmav/stratum-83/blob/feature/retro-ai-game-catalog/README.md)
- [Technical design](https://github.com/elisseosmav/stratum-83/blob/feature/retro-ai-game-catalog/docs/DESIGN.md)
- [Windows acceptance checks](https://github.com/elisseosmav/stratum-83/blob/feature/retro-ai-game-catalog/docs/TESTING.md)
- [Development Roadmap](https://github.com/elisseosmav/stratum-83/blob/feature/retro-ai-game-catalog/docs/DEVELOPMENT-ROADMAP.md)
- [Wiki-ready educational pages](https://github.com/elisseosmav/stratum-83/tree/feature/retro-ai-game-catalog/docs/wiki)
- [GitHub Discussions](https://github.com/elisseosmav/stratum-83/discussions)

When Windows tests pass, the maintainer may create a **new prerelease** with tag `v0.3.0-beta.2` targeting the reviewed commit. Do **not** overwrite `v0.3.0-beta.1`.
