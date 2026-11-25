# Build and Verification Notes

## Verification performed in the authoring environment

- Environment: OpenJDK 21 (Linux).
- `GridZeroGame`, `Move`, `GameResult`, `MinimaxPlayer`, `ExperimentRunner`, `ExperimentResult`, and `SoundPlayer`: **compiled with `javac --release 21`**.
- JUnit test **source methods**: compiled and executed with a minimal local stand-in for the JUnit annotation and assertion signatures, because Maven and the JUnit library were not available. This is **not** a successful `mvn test` run.
- Exhaustive, deterministic game-tree checks for Minimax playing both X and O: no losses across opponent move sequences.

## Not verified here

- Maven dependency resolution and the actual JUnit 5 runner.
- JavaFX source compilation and desktop startup (OpenJFX jars were unavailable).
- Windows GUI behavior, resizing, sound playback, and `run-windows.cmd` on a Windows host.
- Cross-platform native packaging.

## How to verify on Windows

On a Windows PC with JDK 21, Maven and Internet access for the first build, run:

```powershell
mvn clean test
mvn javafx:run
```

Then follow the manual test checklist in [TESTING.md](TESTING.md). Do not treat this package as an acceptance-tested Windows binary.

## Terminal / Research Lab preview — 24 November 2026

For the **v0.3.0-beta.2 development candidate**, separate Java 21 core checks were run in the authoring environment against the same maze source and new benchmark logic: the benchmark compiled, produced equal shortest-path lengths for BFS/Dijkstra/A* across **200** seeded mazes (**600** algorithm results), repeated a 100-maze aggregate exactly, and rejected invalid sample counts. **These are not Maven/JUnit executions.**

The new JavaFX menu and result-only research screens were reviewed at source level. Their actual Windows compilation and rendering still need the procedures in [TESTING.md](TESTING.md). The earlier Windows X/O display bug was confirmed fixed by the maintainer, but this new candidate requires a regression check.

The current `v0.3.0-beta.1` release is not changed by these development commits.

## Human versus BFS Labyrinth challenge — 24 November 2026

The new `MazeChallenge` pure-Java model was compiled using `javac --release 21` with the existing maze engine. The compiled source's Git blob hash matched the GitHub file. A Java harness navigated the BFS-optimal path through **200 seeded mazes**, checking completion, exact optimal step count, 0 extra moves, 100% efficiency and rejected post-finish moves. It additionally checked backtracking (6 moves / optimum 4 / 66.7%), illegal moves and start-equals-goal.

**Not yet run:** JavaFX desktop compilation, actual JUnit 5 execution and Windows graphical verification. The route-overlay controls and completion panel were inspected in source only. See [TESTING.md](TESTING.md).

## Play / Trace / Explain / Apply educational pilot — 24 November 2026

The Grid Zero Minimax decision trace and engineering exercise have been implemented on `feature/grid-zero-learning-lab`. JUnit test sources cover trace/action consistency, immutability, scoring and the toy worst-case choice. A GitHub Actions workflow has been added for Java 21 Maven tests on Linux and Windows.

**Verified:** [GitHub Actions run 45471437228](https://github.com/elisseosmav/stratum-83/actions/runs/45471437228) completed with **successful Java 21 Maven/JUnit jobs on ubuntu-latest and windows-latest**, including source compilation. **Not verified:** visual JavaFX startup, the full keyboard interaction, resizing and manual game acceptance on a Windows desktop. See [TESTING.md](TESTING.md).

## Labyrinth algorithm learning pilot — 24 November 2026

The `feature/labyrinth-learning-lab` branch adds shared BFS/Dijkstra/A* trace instrumentation, the `MazeSearchTrace` data model, JavaFX step visualizations and a fictional weighted service-routing exercise. Tests verify the trace result is identical to the uninstrumented solver and that Dijkstra selects the minimum-cost route.

**Verified:** [Java 21 Maven/JUnit GitHub Actions run 45473229283](https://github.com/elisseosmav/stratum-83/actions/runs/45473389881) completed successfully on Ubuntu and Windows, including compilation of the new JavaFX source. **Pending:** direct Windows GUI/manual acceptance of interaction, rendering and resizing.

No stable release or official Wiki publish occurred.

## Grid Zero recorded-move lessons — 25 November 2026

The `feature/grid-zero-game-history-lessons` candidate adds a replayable `GridZeroHistory`, a `GridZeroMoveLesson` with genuinely hypothetical best-play continuations, and a guided JavaFX history/lesson interface. The third human move can be selected to explain the fourth actual computer move, with no hardcoded example positions. JUnit tests verify move integrity, replay isolation and legal alternative futures. The earlier raw search-event UI remains available only as optional advanced material.

The Windows graphical acceptance runner is configured for the new branch. A passing Java 21 Maven/CI run verifies compilation and tests, **not** the visual usability of the interactive teaching screens. The live Wiki, `main` game code and published Beta 1 remain unchanged.
