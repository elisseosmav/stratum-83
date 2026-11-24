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

## Development roadmap and research ideas

STRATUM-83 follows a **public, release-based roadmap** rather than fixed calendar deadlines.

- **[Development Roadmap](docs/DEVELOPMENT-ROADMAP.md)** — approved milestones, current work, future planned releases and acceptance criteria.
- **[Research Backlog](docs/RESEARCH-BACKLOG.md)** — candidate games and algorithms; ideas are not release commitments.
- **[GitHub Milestones](https://github.com/elisseosmav/stratum-83/milestones)** — track work toward v0.2, v0.3 and v0.4.
- **[Educational Wiki](https://github.com/elisseosmav/stratum-83/wiki)** — explanations of the games and their algorithms.
- **[Discussions](https://github.com/elisseosmav/stratum-83/discussions)** — share suggestions and ask questions.

Proposed future games include Checkers, Blackjack and Chess. These are **research ideas only** until reviewed and formally planned.

## Windows setup (Java 21 and Maven)

STRATUM-83 v0.1 is a **source project**. Building and playing it locally requires **JDK 21** and **Apache Maven 3.9+**. JavaFX is downloaded by Maven, so you do not need to install JavaFX yourself.

### Step 1 — Install Java 21

1. Download [Eclipse Temurin JDK 21 for Windows (official Adoptium)](https://adoptium.net/temurin/releases/?version=21&os=windows&arch=x64&package=jdk). Select **JDK 21**, **Windows x64**, **MSI** (or the architecture matching your computer).
2. Run the MSI installer. The default installation location is usually under `C:\Program Files\Eclipse Adoptium\` (the JDK directory includes its version).
3. If offered, enable **Add to PATH** and **Set JAVA_HOME** during installation.
4. Open a **new PowerShell window** and verify:

   ```powershell
   java -version
   javac -version
   ```

Both commands must report Java 21. If needed, follow the [official Windows Java installation instructions](https://adoptium.net/installation/windows/) to set `JAVA_HOME` to the JDK directory (not `bin`) and add `%JAVA_HOME%\bin` to your Windows `Path`.

### Step 2 — Install Apache Maven

1. Download the **binary ZIP** of Apache Maven 3.9+ from the [official Maven download page](https://maven.apache.org/download.cgi). Do not download the source archive.
2. Extract it to a stable location, such as `C:\Tools\apache-maven-3.9.x\` (replace the version number as appropriate). Maven is unpacked, not installed via an MSI.
3. In Windows search, open **Edit the system environment variables → Environment Variables**. Add the Maven `bin` folder, e.g. `C:\Tools\apache-maven-3.9.x\bin`, to your user `Path`.
4. Open a **new PowerShell window** and verify:

   ```powershell
   mvn -version
   ```

Maven should report version 3.9 or later and **Java 21**. See the [official Maven installation guide](https://maven.apache.org/install.html).

### Step 3 — Download the source code

Option A: clone with Git (requires Git installed):

```powershell
cd "$HOME\Projects"
git clone https://github.com/elisseosmav/stratum-83.git
cd .\stratum-83
```

Option B: open [STRATUM-83 on GitHub](https://github.com/elisseosmav/stratum-83), choose **Code → Download ZIP**, extract it, and open PowerShell **inside the extracted source directory**.

You **must** run Maven from the directory that contains `pom.xml`, not the Maven `bin` directory. Verify:

```powershell
Test-Path .\pom.xml
```

Expected output: `True`.

### Step 4 — Compile, test and play

From the directory containing `pom.xml`:

```powershell
mvn clean test
mvn javafx:run
```

- `mvn clean test` compiles the sources and runs JUnit tests.
- `mvn javafx:run` starts the resizable STRATUM-83 terminal.
- Alternatively, double-click `test-windows.cmd` or `run-windows.cmd` (requires Maven on `Path`).

### Step 5 — Already have Maven next to the source? (No PATH changes needed)

For this directory structure:

```text
STRATUM-83\
├── apache-maven\
│   └── bin\
│       └── mvn.cmd
└── stratum-83-v0.1\
    └── pom.xml
```

Use these PowerShell commands:

```powershell
cd "$HOME\Projects\STRATUM-83\stratum-83-v0.1"
Test-Path .\pom.xml
& "..\apache-maven\bin\mvn.cmd" -version
& "..\apache-maven\bin\mvn.cmd" clean test
& "..\apache-maven\bin\mvn.cmd" javafx:run
```

If your Maven folder is called `apache-maven-3.10.2` instead, replace `apache-maven` in the commands. The PowerShell `&` operator starts an executable at a given path.

### Troubleshooting

| Symptom | Solution |
| --- | --- |
| `No POM in this directory` / `MissingProjectException` | Find the project with `Get-ChildItem -Recurse -Filter pom.xml` and run Maven from that file's parent folder. |
| `mvn` is not recognized | Add Maven's `bin` folder to `Path`, reopen PowerShell, or execute `mvn.cmd` by path. |
| Compiler reports wrong Java version | Check `java -version`, `javac -version` and `mvn -version`. Make sure Maven is using JDK 21. |
| Maven fails while downloading dependencies | Internet access is required on first build for JavaFX, JUnit and Maven plugins. |
| JavaFX fails to launch | Save the console output and report the error with your Java and Maven version information. |

**Offline operation:** The application itself requires no Internet connection. The first Maven build downloads dependencies from Maven Central. Future Maven runs may also require the local Maven cache to contain all dependencies and plugins.

**Not a packaged Windows installer:** Version 0.1 contains source code only. A self-contained Windows executable is planned for a later release. Full Windows/JavaFX acceptance testing remains pending; see [build notes](docs/BUILD-NOTES.md).

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
