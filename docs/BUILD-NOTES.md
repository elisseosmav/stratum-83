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
