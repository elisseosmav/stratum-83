# STRATUM-83

**Version 0.1.0 — a local Java research terminal**

STRATUM-83 is a small, offline desktop program inspired by the atmosphere of early computer terminals. It contains an original implementation of **Grid Zero** (tic-tac-toe), a perfect-play **Minimax** computer opponent, a local two-player mode, and a repeatable AI experiment.

The project is for educational, non-commercial research and demonstration. It does not connect to AWS or any AI service, and it does not send telemetry.

It is an independent project, **not affiliated with or endorsed by the creators of any film**. It does not include movie artwork, dialogue, music, or sound assets.

## What works in v0.1

- Resizable green-on-black JavaFX terminal window.
- Menu buttons, text commands and keyboard shortcuts.
- Grid Zero: human versus perfect-play Minimax computer, or two local players.
- Restart a game and return to the menu.
- Minimax versus random: a repeatable, seeded 200-game experiment.
- Optional synthesized retro click sound, **off by default**.
- Completely offline while playing; no account or API key required.
- JUnit tests for game rules, AI moves and reproducible experiments.

## Windows setup

You need **JDK 21** and **Apache Maven 3.9+** installed and available on `PATH`.

1. Extract the downloaded ZIP to a folder (for example `C:\Projects\stratum-83`).
2. Open PowerShell or Command Prompt in the extracted project directory.
3. Check `java -version` and `mvn -version`. Both must work; Java must report version 21.
4. Run the tests:

   ```powershell
   mvn test
   ```

5. Launch the desktop application:

   ```powershell
   mvn javafx:run
   ```

Alternatively, double-click `run-windows.cmd` (or `test-windows.cmd`) when Java and Maven are correctly installed.

**First build:** Maven downloads JavaFX, JUnit, and plugin dependencies from Maven Central, so Internet access is required for the initial build unless those dependencies are already cached. **Playing the game requires no Internet access.**

The ZIP is a **source distribution**, not a Windows installer. A bundled-runtime Windows executable is a later packaging milestone.

## Linux and macOS

Use JDK 21 and Maven 3.9+, then run `mvn test` and `mvn javafx:run`. A desktop environment and graphics support are required. Windows is the priority platform for v0.1; the others are not yet acceptance-tested.

## Terminal commands

| Command | Meaning |
| --- | --- |
| `PLAY` | Start human vs computer (you are X) |
| `DUEL` | Start two-player local Grid Zero |
| `EXPERIMENT` | Open the Minimax vs random experiment |
| `HELP` | Show help |
| `MENU` | Return to the main menu |
| `SOUND ON` / `SOUND OFF` | Toggle optional click sounds |
| `EXIT` | Close the program |

Shortcuts: **F1** help, **F2** sound toggle, **Esc** main menu.

## Experiment details

The experiment runs 200 games against a random legal-move opponent using a fixed random seed (`1983`). Minimax alternates between X and O across matches. The result counts are for this experiment and do not measure the behaviour of a language model or cloud AI service. Minimax should never lose at 3x3 tic-tac-toe.

## Code organization

```text
src/main/java/org/stratum83/
  game/       GridZeroGame, Move, MinimaxPlayer, GameResult
  research/   ExperimentRunner, ExperimentResult
  ui/         StratumApp, SoundPlayer
src/main/resources/terminal.css
src/test/java/org/stratum83/   JUnit tests
docs/                           design notes and test checklist
```

The game and research code do not import JavaFX. Only the UI layer depends on JavaFX. There is no dependency injection framework, database or network layer.

## Development guidelines

- Clear and conventional Java; no speculative abstraction layers.
- Keep game rules separate from the display.
- Prefer ordinary loops and descriptive names.
- Test algorithms and edge cases.
- No artificial complexity or misleading claims of validation.

See [docs/DESIGN.md](docs/DESIGN.md) for the design, [docs/TESTING.md](docs/TESTING.md) for the acceptance checklist, and [docs/BUILD-NOTES.md](docs/BUILD-NOTES.md) for verified and unverified build steps.

## Scope and limitations

- One playable game in v0.1. Labyrinth and Crisis Protocol are future ideas, **not implemented**.
- The interface is an original retro style, not an exact recreation of a film interface.
- Sound depends on an available local audio output device. If unavailable, the game continues silently.
- No native Windows `.exe` is included.

## Licence

Source code: **Apache License 2.0**. See [LICENSE](LICENSE). The copyright notice refers to the STRATUM-83 contributors.