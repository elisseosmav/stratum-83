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
